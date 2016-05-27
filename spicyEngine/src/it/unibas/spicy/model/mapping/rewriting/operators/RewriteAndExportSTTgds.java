package it.unibas.spicy.model.mapping.rewriting.operators;

import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.persistence.DAOException;
import it.unibas.spicy.persistence.DAOMappingTaskTgds;
import java.util.Collections;
import java.util.List;

public class RewriteAndExportSTTgds {

    private final DAOMappingTaskTgds daoMappingTaskTgds = new DAOMappingTaskTgds();
    private final RewriteTgds rewriter = new RewriteTgds();

    public String rewriteAndExport(String tgdString) throws DAOException {
        String mappingTaskString = generateMappingTaskString(tgdString);
        MappingTask mappingTask = loadMappingTask(mappingTaskString);
        List<FORule> rewrittenTgds = rewriteRules(mappingTask);
        return buildResultString(rewrittenTgds, mappingTask);
    }

    private String generateMappingTaskString(String tgdString) {
        StringBuilder result = new StringBuilder();
        result.append("Mapping task:\n");
        result.append("Source schema:     generate\n");
        result.append("Source instance:   generate\n");
        result.append("Target schema:     generate\n");
        result.append("SOURCE TO TARGET TGDs:\n");
        result.append(tgdString);
        return result.toString();
    }

    private MappingTask loadMappingTask(String tgdString) throws DAOException {
        MappingTask mappingTask = daoMappingTaskTgds.loadMappingTaskFromString(tgdString);
        
        mappingTask.getConfig().setRewriteSubsumptions(true);
        mappingTask.getConfig().setRewriteCoverages(true);
        mappingTask.getConfig().setRewriteSelfJoins(false);
        mappingTask.getConfig().setRewriteOnlyProperHomomorphisms(false);
        
        mappingTask.getConfig().setRewriteOverlaps(true);
        
        mappingTask.getConfig().setUseLocalSkolem(true);
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
