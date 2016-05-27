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
package it.unibas.spicy.parser.operators;

import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.mapping.proxies.ChainingDataSourceProxy;
import it.unibas.spicy.model.mapping.proxies.ConstantDataSourceProxy;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.proxies.MergeDataSourceProxy;
import it.unibas.spicy.model.datasource.values.IOIDGeneratorStrategy;
import it.unibas.spicy.model.datasource.values.IntegerOIDGenerator;
import it.unibas.spicy.model.paths.operators.ContextualizePaths;
import it.unibas.spicy.parser.ParserException;
import it.unibas.spicy.parser.ParserFD;
import it.unibas.spicy.parser.ParserInstance;
import it.unibas.spicy.parser.ParserTGD;
import it.unibas.spicy.parser.output.TGDMappingTaskLexer;
import it.unibas.spicy.parser.output.TGDMappingTaskParser;
import it.unibas.spicy.persistence.DAOException;
import it.unibas.spicy.persistence.DAOMappingTaskLines;
import it.unibas.spicy.persistence.DAOMappingTaskTgds;
import it.unibas.spicy.persistence.xml.DAOXsd;
import it.unibas.spicy.persistence.xml.operators.TransformFilePaths;
import it.unibas.spicy.utility.SpicyEngineConstants;
import java.util.ArrayList;
import java.util.List;
import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

abstract class AbstractParseMappingTask implements IParseMappingTask {

    private static Log logger = LogFactory.getLog(AbstractParseMappingTask.class);
    protected String mappingTaskFilePath;
    protected MappingTask mappingTask;
    protected boolean generateSource;
    protected boolean generateTarget;
    protected List<ParserTGD> stParserTgds = new ArrayList<ParserTGD>();
    protected List<ParserTGD> targetParserTgds = new ArrayList<ParserTGD>();
    protected List<ParserFD> sourceFDs = new ArrayList<ParserFD>();
    protected List<ParserFD> targetFDs = new ArrayList<ParserFD>();
    protected List<ParserInstance> sourceInstances = new ArrayList<ParserInstance>();
    protected DAOXsd daoXSD = new DAOXsd();
    protected TransformFilePaths filePathTransformator = new TransformFilePaths();
    protected ContextualizePaths pathContextualizer = new ContextualizePaths();
    protected IOIDGeneratorStrategy oidGenerator = new IntegerOIDGenerator();

    /////////////////////   CALLBACK METHODS FOR THE GRAMMAR  ////////////////////////////
    public MappingTask generateMappingTask(String mappingTaskFile) throws Exception {
        try {
            if (!mappingTaskFile.endsWith(PARSER_EXTENSION)) {
                throw new IllegalArgumentException("TGD file must end with " + PARSER_EXTENSION);
            }
            this.mappingTaskFilePath = mappingTaskFile;
            TGDMappingTaskLexer lex = new TGDMappingTaskLexer(new ANTLRFileStream(mappingTaskFile));
            CommonTokenStream tokens = new CommonTokenStream(lex);
            TGDMappingTaskParser g = new TGDMappingTaskParser(tokens);
            try {
                g.setGenerator(this);
                g.prog();
            } catch (RecognitionException ex) {
                logger.error("Unable to load mapping task: " + ex.getMessage());
                throw new ParserException(ex);
            }
            mappingTask.setFileName(mappingTaskFile);
            return this.mappingTask;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            throw new ParserException(e);
        }
    }

    /////////////////////   CALLBACK METHODS FOR THE GRAMMAR  ////////////////////////////
    public MappingTask generateMappingTaskFromString(String mappingTaskString) throws Exception {
        try {
            TGDMappingTaskLexer lex = new TGDMappingTaskLexer(new ANTLRStringStream(mappingTaskString));
            CommonTokenStream tokens = new CommonTokenStream(lex);
            TGDMappingTaskParser g = new TGDMappingTaskParser(tokens);
            try {
                g.setGenerator(this);
                g.prog();
            } catch (RecognitionException ex) {
                logger.error("Unable to load mapping task: " + ex.getMessage());
                throw new ParserException(ex);
            }
            mappingTask.setFileName("");
            return this.mappingTask;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            throw new ParserException(e);
        }
    }

    public void addSTTGD(ParserTGD tgd) {
        this.stParserTgds.add(tgd);
    }

    public void addTargetTGD(ParserTGD tgd) {
        this.targetParserTgds.add(tgd);
    }

    public void setSourceFDs(List<ParserFD> fds) {
        this.sourceFDs = fds;
    }

    public void setTargetFDs(List<ParserFD> fds) {
        this.targetFDs = fds;
    }

    public void addSourceInstance(ParserInstance instance) {
        this.sourceInstances.add(instance);
    }

    public void createMappingTask(List<String> sourceSchemaFiles, List<String> sourceInstanceFiles, String targetSchemaFile) throws Exception {
        try {
            List<IDataSourceProxy> sources = new ArrayList<IDataSourceProxy>();
            for (int i = 0; i < sourceSchemaFiles.size(); i++) {
                String sourceSchemaFile = sourceSchemaFiles.get(i);
                String sourceInstanceFile = sourceInstanceFiles.get(i);
                IDataSourceProxy sourceProxy = null;
                if (sourceSchemaFile.equalsIgnoreCase(GENERATE)) {
                    sourceProxy = generateEmptyDataSource(SpicyEngineConstants.DATASOURCE_ROOT_LABEL);
                    this.generateSource = true;
                } else if (sourceSchemaFile.startsWith(CHAIN)) {
                    String previousStepFilePath = filePathTransformator.expand(mappingTaskFilePath, sourceSchemaFile.substring(CHAIN.length()));
                    MappingTask previousStep = loadMappingTask(previousStepFilePath);
                    sourceProxy = new ChainingDataSourceProxy(previousStep, previousStepFilePath);
                    sourceProxy.generateIntermediateSchema();
                } else {
                    String absoluteSourceSchemaFile = filePathTransformator.expand(mappingTaskFilePath, sourceSchemaFile.substring(6));
                    String absoluteSourceInstanceFile = filePathTransformator.expand(mappingTaskFilePath, sourceInstanceFile.substring(6));
                    sourceProxy = loadSource(absoluteSourceSchemaFile, absoluteSourceInstanceFile);
                }
                sources.add(sourceProxy);
            }
            IDataSourceProxy source = null;
            if (sources.size() == 1) {
                source = sources.get(0);
            } else {
                source = new MergeDataSourceProxy(sources);
            }
            IDataSourceProxy target = null;
            if (targetSchemaFile.equalsIgnoreCase(GENERATE)) {
                target = generateEmptyDataSource(SpicyEngineConstants.DATASOURCE_ROOT_LABEL);
                this.generateTarget = true;
            } else {
                String absoluteTargetSchemaFile = filePathTransformator.expand(mappingTaskFilePath, targetSchemaFile.substring(6));
                target = loadSource(absoluteTargetSchemaFile, null);
            }
            this.mappingTask = new MappingTask(source, target, SpicyEngineConstants.TGD_BASED_MAPPING_TASK);
        } catch (Exception e) {
            logger.error(e);
            throw new ParserException(e);
        }
    }

    private IDataSourceProxy generateEmptyDataSource(String name) {
        INode tupleNode = new TupleNode(name);
        tupleNode.setRoot(true);
        IDataSourceProxy dataSource = new ConstantDataSourceProxy(new DataSource(name, tupleNode));
        return dataSource;
    }

    public IDataSourceProxy loadSource(String schemaFile, String instanceFile) {
        try {
            IDataSourceProxy dataSource = daoXSD.loadSchema(schemaFile);
            if (instanceFile != null) {
                daoXSD.loadInstance(dataSource, instanceFile);
            }
            return dataSource;
        } catch (Exception ex) {
            logger.error("Unable to load sources: " + ex.getMessage());
            throw new ParserException(ex);
        }
    }

    private MappingTask loadMappingTask(String filePath) throws DAOException {
        if (filePath.endsWith(".xml")) {
            return new DAOMappingTaskLines().loadMappingTask(filePath);
        } else if (filePath.endsWith(".tgd")) {
            return new DAOMappingTaskTgds().loadMappingTask(filePath);
        }
        throw new IllegalArgumentException("Illegal file name for mapping task: " + filePath);
    }

    public abstract void processTGDs();

    public void setSourceNulls(String sourceNulls) {
        mappingTask.getConfig().setRewriteTgdsForSourceNulls(Integer.valueOf(sourceNulls) == 1);
    }

    public void setSubsumptions(String subsumptions) {
        mappingTask.getConfig().setRewriteSubsumptions(Integer.valueOf(subsumptions) == 1);
    }

    public void setCoverages(String coverages) {
        mappingTask.getConfig().setRewriteCoverages(Integer.valueOf(coverages) == 1);
    }

    public void setSelfJoins(String selfJoins) {
        mappingTask.getConfig().setRewriteSelfJoins(Integer.valueOf(selfJoins) == 1);
    }

    public void setNoRewriting(String value) {
        if (Integer.valueOf(value) == 1) {
            mappingTask.getConfig().setNoRewriting();
        }
    }

    public void setEgds(String egds) {
        mappingTask.getConfig().setRewriteEGDs(Integer.valueOf(egds) == 1);
    }

    public void setOverlaps(String overlaps) {
        mappingTask.getConfig().setRewriteOverlaps(Integer.valueOf(overlaps) == 1);
    }

    public void setSkolemsForEgds(String skolems) {
        mappingTask.getConfig().setRewriteSkolemsForEGDs(Integer.valueOf(skolems) == 1);
    }

    public void setSkolemStrings(String skolemStrings) {
        mappingTask.getConfig().setUseSkolemStrings(Integer.valueOf(skolemStrings) == 1);
    }

    public void setLocalSkolems(String localSkolems) {
        mappingTask.getConfig().setUseLocalSkolem(Integer.valueOf(localSkolems) == 1);
    }

    public void setSortStrategy(String sortStrategy) {
        mappingTask.getConfig().setSortStrategy(Integer.valueOf(sortStrategy));
    }

    public void setSkolemTableStrategy(String skolemTableStrategy) {
        mappingTask.getConfig().setSkolemTableStrategy(Integer.valueOf(skolemTableStrategy));
    }
}
