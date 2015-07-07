/*
Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
Giansalvatore Mecca - giansalvatore.mecca@unibas.it
Salvatore Raunich - salrau@gmail.com

This file is part of ++Spicy - a Schema Mapping and Data Exchange Tool

++Spicy is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
any later version.

++Spicy is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with ++Spicy.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.unibas.spicy.persistence;

import it.unibas.spicy.model.datasource.FunctionalDependency;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.KeyConstraint;
import it.unibas.spicy.model.mapping.EngineConfiguration;
import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.mapping.operators.RenameSetAliases;
import it.unibas.spicy.model.paths.VariableFunctionalDependency;
import it.unibas.spicy.model.paths.operators.ContextualizePaths;
import it.unibas.spicy.parser.operators.IParseMappingTask;
import it.unibas.spicy.parser.operators.ParseMappingTask;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DAOMappingTaskTgds {

    private static Log logger = LogFactory.getLog(DAOMappingTaskTgds.class);

    /*  /////////////////////////////////////////////////////////
     *                        LOAD
     *  ///////////////////////////////////////////////////////// */
    public MappingTask loadMappingTask(String filePath) throws DAOException {
        try {
            IParseMappingTask generator = new ParseMappingTask();
            MappingTask mappingTask = generator.generateMappingTask(filePath);
            mappingTask.setModified(false);
            mappingTask.setToBeSaved(false);
            return mappingTask;
        } catch (Exception ex) {
//            ex.printStackTrace();
            logger.error(ex);
            throw new DAOException(ex.getMessage());
        }
    }

    /*  /////////////////////////////////////////////////////////
     *                        SAVE
     *  ///////////////////////////////////////////////////////// */
    public void saveMappingTask(MappingTask mappingTask, String filePath) throws DAOException {
        StringBuilder result = new StringBuilder();
        result.append("Mapping task:\n");
        result.append("Source schema: 		generate\n");
        result.append("Source instance:	generate\n");
        result.append("Target schema:		generate\n");
        result.append("\n");
        result.append("SOURCE TO TARGET TGDs:\n");
        RenameSetAliases renamer = new RenameSetAliases();
        List<FORule> rewrittenRules = renamer.renameAliasesInRules(mappingTask.getMappingData().getRewrittenRules());
        for (FORule tgd : rewrittenRules) {
            result.append(tgd.toSaveString("", mappingTask)).append("\n");
        }
        EngineConfiguration conf = mappingTask.getConfig();
        if (conf.rewriteSkolemsForEGDs() || conf.rewriteOverlaps()) {
            IDataSourceProxy sourceProxy = mappingTask.getSourceProxy();
            if ((sourceProxy.getFunctionalDependencies().size() > 0)
                    || (sourceProxy.getKeyConstraints().size() > 0)) {
                result.append("SOURCE FDs:\n");
                saveFunctionalDependencies(sourceProxy, result);
                result.append("\n");
            }
            IDataSourceProxy targetProxy = mappingTask.getTargetProxy();
            if ((targetProxy.getFunctionalDependencies().size() > 0)
                    || (targetProxy.getKeyConstraints().size() > 0)) {
                result.append("TARGET FDs:\n");
                saveFunctionalDependencies(targetProxy, result);
                result.append("\n");
            }
        }
        if (mappingTask.getSourceProxy().getInstances().size() > 0) {
            result.append("SOURCE INSTANCE:\n");
            INode instance = mappingTask.getSourceProxy().getInstances().get(0);
            result.append(instance.toSaveString());
            result.append("\n");
        }
        mappingTask.getConfig().setNoRewriting();
        mappingTask.getConfig().setRewriteOverlaps(false);
        result.append("CONFIG:\n");
        if (conf.noRewriting()) {
            result.append("NOREWRITING: ").append(convertBooleanToNumber(conf.noRewriting())).append("\n");
        } else {
            result.append("SUBSUMPTIONS: ").append(convertBooleanToNumber(conf.rewriteSubsumptions())).append("\n");
            result.append("COVERAGES: ").append(convertBooleanToNumber(conf.rewriteCoverages())).append("\n");
            result.append("SELFJOINS: ").append(convertBooleanToNumber(conf.rewriteSelfJoins())).append("\n");
        }
        if (conf.rewriteEGDs()) {
            result.append("EGDS: ").append(convertBooleanToNumber(conf.rewriteEGDs())).append("\n");
        } else {
            result.append("OVERLAPS: ").append(convertBooleanToNumber(conf.rewriteOverlaps())).append("\n");
            result.append("SKOLEMSFOREGDS: ").append(convertBooleanToNumber(conf.rewriteSkolemsForEGDs())).append("\n");
        }
        result.append("SKOLEMSTRINGS: ").append(convertBooleanToNumber(conf.useSkolemStrings())).append("\n");
        result.append("LOCALSKOLEMS: ").append(convertBooleanToNumber(conf.useLocalSkolem())).append("\n");
        result.append("SORTSTRATEGY: ").append(conf.getSortStrategy()).append("\n");
        result.append("SKOLEMTABLESTRATEGY: ").append(conf.getSkolemTableStrategy()).append("\n");

        writeStringToFile(result.toString(), filePath);

    }

    private void saveFunctionalDependencies(IDataSourceProxy sourceProxy, StringBuilder result) {
        ContextualizePaths contextualizePath = new ContextualizePaths();
        List<VariableFunctionalDependency> functionalDependencies = contextualizePath.contextualizeFunctionalDependencies(sourceProxy);
        for (VariableFunctionalDependency functionalDependency : functionalDependencies) {
            result.append(functionalDependency.toSaveString()).append("\n");
        }
    }

    private void writeStringToFile(String content, String filePath) throws DAOException {
        File fileOutput;
        try {
            fileOutput = new File(filePath);
            fileOutput.getParentFile().mkdirs();

            InputStream in = new ByteArrayInputStream(content.getBytes());
            FileOutputStream out = new FileOutputStream(fileOutput);
            byte[] buffer = new byte[1024];
            int len;

            while ((len = in.read(buffer)) >= 0) {
                out.write(buffer, 0, len);
            }
        } catch (Throwable t) {
            logger.error(t);
            throw new DAOException(t.getMessage());
        }
    }

    private String convertBooleanToNumber(boolean value) {
        if (value) {
            return "1";
        }
        return "0";
    }
}