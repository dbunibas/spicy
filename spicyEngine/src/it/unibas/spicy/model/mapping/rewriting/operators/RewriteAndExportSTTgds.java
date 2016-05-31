package it.unibas.spicy.model.mapping.rewriting.operators;

import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.model.mapping.rewriting.RewritingConfiguration;
import it.unibas.spicy.persistence.DAOException;
import it.unibas.spicy.persistence.DAOMappingTaskTgds;
import java.util.Collections;
import java.util.List;

public class RewriteAndExportSTTgds {

    private final DAOMappingTaskTgds daoMappingTaskTgds = new DAOMappingTaskTgds();
    private final RewriteTgds rewriter = new RewriteTgds();

    public String rewriteAndExport(String tgdString, String fdString, RewritingConfiguration config) throws DAOException {
        String mappingTaskString = generateMappingTaskString(tgdString, fdString);
        MappingTask mappingTask = loadMappingTask(mappingTaskString, config);
        List<FORule> rewrittenTgds = rewriteRules(mappingTask);
        return buildResultString(rewrittenTgds, mappingTask);
    }

    private String generateMappingTaskString(String tgdString, String fdString) {
        StringBuilder result = new StringBuilder();
        result.append("Mapping task:\n");
        result.append("Source schema:     generate\n");
        result.append("Source instance:   generate\n");
        result.append("Target schema:     generate\n");
        result.append("SOURCE TO TARGET TGDs:\n");
        result.append(tgdString);
        if (fdString != null && !fdString.isEmpty()) {
            result.append("TARGET FDs:\n");
            result.append(fdString);
        }
        return result.toString();
    }

    private MappingTask loadMappingTask(String tgdString, RewritingConfiguration config) throws DAOException {
        MappingTask mappingTask = daoMappingTaskTgds.loadMappingTaskFromString(tgdString);
        
        mappingTask.getConfig().setRewriteSubsumptions(config.isRewriteSubsumptions());
        mappingTask.getConfig().setRewriteCoverages(config.isRewriteCoverages());
        mappingTask.getConfig().setRewriteSelfJoins(config.isRewriteSelfJoins());
        mappingTask.getConfig().setRewriteOnlyProperHomomorphisms(config.isRewriteOnlyProperHomomorphisms());
        
        mappingTask.getConfig().setRewriteOverlaps(config.isRewriteOverlaps());
        
        mappingTask.getConfig().setUseLocalSkolem(config.isUseLocalSkolems());
        return mappingTask;
    }
    
    private List<FORule> rewriteRules(MappingTask mappingTask) {
        List<FORule> stTgds = mappingTask.getMappingData().getSTTgds();
        List<FORule> rewrittenSTTgds = rewriter.rewriteTgds(stTgds, mappingTask);
        Collections.sort(rewrittenSTTgds);    
        return rewrittenSTTgds;        
    }

    private String buildResultString(List<FORule> rewrittenTgds, MappingTask mappingTask) {
        StringBuilder result = new StringBuilder();
        for (FORule rule : rewrittenTgds) {
            result.append(rule.toSaveString("", mappingTask)).append("\n");
        }
        return result.toString();
    }

}
