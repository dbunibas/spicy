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
package it.unibas.spicy.model.mapping;

import it.unibas.spicy.model.algebra.IAlgebraOperator;
import it.unibas.spicy.model.algebra.operators.GenerateAlgebraTree;
import it.unibas.spicy.model.algebra.query.operators.sql.GenerateSQL;
import it.unibas.spicy.model.algebra.query.operators.xquery.GenerateXQuery;
import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import it.unibas.spicy.model.mapping.operators.GenerateSolution;
import it.unibas.spicy.model.mapping.operators.MinimizeTGDs;
import it.unibas.spicy.model.mapping.operators.RenameSetAliases;
import it.unibas.spicy.model.mapping.rewriting.CoverageMap;
import it.unibas.spicy.model.mapping.rewriting.SubsumptionMap;
import it.unibas.spicy.model.mapping.rewriting.ExpansionPartialOrder;
import it.unibas.spicy.model.mapping.rewriting.Expansion;
import it.unibas.spicy.model.mapping.rewriting.ExpansionMap;
import it.unibas.spicy.model.mapping.rewriting.egds.STTGDOverlapMap;
import it.unibas.spicy.model.mapping.rewriting.egds.operators.GenerateSTTGDOverlapMapFactory;
import it.unibas.spicy.model.mapping.rewriting.egds.operators.IGenerateSTTGDOverlapMap;
import it.unibas.spicy.model.mapping.rewriting.operators.GenerateCoverageMap;
import it.unibas.spicy.model.mapping.rewriting.operators.GenerateTgdSubsumptionMapWithoutSelfJoins;
import it.unibas.spicy.model.mapping.rewriting.operators.GenerateExpansions;
import it.unibas.spicy.model.mapping.rewriting.operators.GenerateExpansionMoreCompactOrder;
import it.unibas.spicy.model.mapping.rewriting.operators.GenerateExpansionMoreInformativeOrder;
import it.unibas.spicy.model.mapping.rewriting.operators.RemoveClonesFromConclusion;
import it.unibas.spicy.model.mapping.rewriting.operators.RewriteTgds;
import it.unibas.spicy.model.mapping.rewriting.sourcenulls.RewriteTgdsToHandleSourceNulls;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.operators.ContextualizePaths;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.List;

public class MappingData {

    private MappingTask mappingTask;
    private List<VariableCorrespondence> correspondences;
    // input s-t tgds
    private List<FORule> candidateSTTgds;
    // input s-t tgds plus overlap tgds
    private List<FORule> stTgds;
    private List<FORule> stTgdsForCanonicalSolution;
    // rewriting data structures
    private STTGDOverlapMap stOverlapMap;
    private SubsumptionMap stSubsumptionMap;
    private CoverageMap stCoverageMap;
    private ExpansionMap expansionMap;
    private ExpansionPartialOrder moreCompactPartialOrder;
    private ExpansionPartialOrder moreInformativePartialOrder;
    private SubsumptionMap expansionRulesSubsumptionMap;
    // after rewriting
    private List<FORule> rewrittenRules;
    private IAlgebraOperator algebraTree;
    private IAlgebraOperator canonicalTree;
    private IDataSourceProxy solution;
    private IDataSourceProxy canonicalSolution;
    private String sqlScript;
    private String xQueryScript;

    public MappingData(MappingTask mappingTask) {
        this.mappingTask = mappingTask;
    }

    public List<VariableCorrespondence> getCorrespondences() {
        if (this.correspondences == null) {
            ContextualizePaths contextualizer = new ContextualizePaths();
            this.correspondences = contextualizer.contextualizeCorrespondences(mappingTask);
        }
        return correspondences;
    }

    public List<FORule> getCandidateSTTgds() {
        if (this.candidateSTTgds == null) {
            if (this.mappingTask.getLoadedTgds() == null) {
                this.candidateSTTgds = new MinimizeTGDs().generateAndMinimizeTGDs(mappingTask);
            } else {
                this.candidateSTTgds = mappingTask.getLoadedTgds();
            }
            if (this.mappingTask.getConfig().rewriteTgdsForSourceNulls()) {
                RewriteTgdsToHandleSourceNulls rewriter = new RewriteTgdsToHandleSourceNulls();
                this.candidateSTTgds = rewriter.rewrite(this.candidateSTTgds, this.mappingTask.getSourceProxy());
            }            
        }
        return candidateSTTgds;
    }
    
    public STTGDOverlapMap getSTTGDOverlapMap() {
        if (this.stOverlapMap == null) {
            IGenerateSTTGDOverlapMap overlapGenerator = GenerateSTTGDOverlapMapFactory.getGenerator();
            this.stOverlapMap = overlapGenerator.generateOverlapMap(getCandidateSTTgds(), mappingTask);
        }
        return stOverlapMap;
    }

    public List<FORule> getOverlapSTTgds() {
        return getSTTGDOverlapMap().getOverlapTgds();
    }

    public List<FORule> getSTTgds() {
        if (this.stTgds == null) {
            this.stTgds = new ArrayList<FORule>(getCandidateSTTgds());
            if (this.mappingTask.getConfig().rewriteOverlaps()) {
                this.stTgds = new ArrayList<FORule>(getOverlapSTTgds());
            }
        }
        return this.stTgds;
    }

    public List<FORule> getSTTgdsForCanonicalSolutions() {
        if (this.stTgdsForCanonicalSolution == null) {
            RemoveClonesFromConclusion cloneRemover = new RemoveClonesFromConclusion();
            this.stTgdsForCanonicalSolution = cloneRemover.cloneAndRemoveClonesFromConclusions(getCandidateSTTgds());
        }
        return this.stTgdsForCanonicalSolution;
    }

    public List<FORule> getRewrittenRules() {
        if (this.rewrittenRules == null) {
            RewriteTgds rewriter = new RewriteTgds();
            this.rewrittenRules = rewriter.rewriteTgds(getSTTgds(), mappingTask);
        }
        return this.rewrittenRules;
    }

    public SubsumptionMap getSTSubsumptionMap() {
        if (this.stSubsumptionMap == null) {
            GenerateTgdSubsumptionMapWithoutSelfJoins generator = new GenerateTgdSubsumptionMapWithoutSelfJoins();
            this.stSubsumptionMap = generator.generateSTSubsumptionMap(getSTTgds(), mappingTask);
        }
        return stSubsumptionMap;
    }

    public CoverageMap getSTCoverageMap() {
        if (this.stCoverageMap == null) {
            GenerateCoverageMap generator = new GenerateCoverageMap();
            this.stCoverageMap = generator.generateCoverageMap(getSTTgds(), mappingTask);
        }
        return stCoverageMap;
    }

    public boolean hasSelfJoinsInTgdConclusions() {
        return SpicyEngineUtility.hasSelfJoinsInTgds(mappingTask);
    }

    public ExpansionMap getExpansionsMap() {
        if (this.expansionMap == null) {
            GenerateExpansions generator = new GenerateExpansions();
            this.expansionMap = generator.generateExpansions(getSTTgds(), mappingTask);
        }
        return expansionMap;
    }

    public List<Expansion> getExpansions() {
        return getExpansionsMap().getExpansions();
//        return getExpansionsMoreCompactOrderMap().getExpansions();
    }

    public ExpansionPartialOrder getExpansionsMoreCompactOrderMap() {
        if (this.moreCompactPartialOrder == null) {
            GenerateExpansionMoreCompactOrder generator = new GenerateExpansionMoreCompactOrder();
            this.moreCompactPartialOrder = generator.generateOrdering(getExpansionsMap().getExpansions(), mappingTask);
        }
        return moreCompactPartialOrder;
    }

    public ExpansionPartialOrder getExpansionsMoreInformativeOrderMap() {
        if (this.moreInformativePartialOrder == null) {
            GenerateExpansionMoreInformativeOrder generator = new GenerateExpansionMoreInformativeOrder();
            this.moreInformativePartialOrder = generator.generateOrdering(getExpansionsMap().getExpansions(), mappingTask);
        }
        return moreInformativePartialOrder;
    }

    public SubsumptionMap getExpansionRulesSubsumptionMap() {
        getRewrittenRules();
        return expansionRulesSubsumptionMap;
    }

    public void setExpansionRulesSubsumptionMap(SubsumptionMap expansionRulesSubsumptionMap) {
        // this is not generated, it is set by the rule rewriter
        this.expansionRulesSubsumptionMap = expansionRulesSubsumptionMap;
    }

    public IAlgebraOperator getCanonicalAlgebraTree() {
        if (this.canonicalTree == null) {
            this.canonicalTree = new GenerateAlgebraTree().generateCanonicalTreeForMappingTask(mappingTask);
        }
        return canonicalTree;
    }

    public IAlgebraOperator getAlgebraTree() {
        if (this.algebraTree == null) {
            this.algebraTree = new GenerateAlgebraTree().generateTreeForMappingTask(mappingTask);
        }
        return algebraTree;
    }

    public IDataSourceProxy getSolution() {
        if (this.solution == null) {
            GenerateSolution generator = new GenerateSolution();
            this.solution = generator.generateSolution(this.mappingTask);
        }
        return this.solution;
    }

    public void verifySolution() {
        GenerateSolution generator = new GenerateSolution();
        generator.verifySolution(getSolution(), mappingTask);
    }

    public IDataSourceProxy getCanonicalSolution() {
        if (this.canonicalSolution == null) {
//            if (this.mappingTask.getConfig().rewriteEGDs()) {
//                throw new IllegalMappingTaskException("This engine is unable to chase EGDs. Try the core solution.");
//            }
            this.canonicalSolution = getCanonicalAlgebraTree().execute(mappingTask.getSourceProxy());
        }
        return this.canonicalSolution;
    }

    public String getSQLScript() {
        if (this.sqlScript == null) {
            this.sqlScript = new GenerateSQL().generateSQL(mappingTask);
        }
        return sqlScript;
    }

    public String getXQueryScript(String fileName) {
        if (this.xQueryScript == null) {
            this.xQueryScript = new GenerateXQuery().generateXQuery(mappingTask, fileName);
        }
        return xQueryScript;
//        return new GenerateXQuery().generateXQuery(mappingTask, fileName);
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("-----------------------------  MAPPING DATA -----------------------------\n");
        result.append("Source Schema:\n").append(this.mappingTask.getSourceProxy()).append("\n");
        result.append(this.mappingTask.getSourceProxy().getMappingData()).append("\n");
        result.append("Target Schema:\n").append(this.mappingTask.getTargetProxy()).append("\n");
        result.append(this.mappingTask.getTargetProxy().getMappingData()).append("\n");
        result.append("================ Correspondences ===================\n");
        for (ValueCorrespondence correspondence : this.mappingTask.getValueCorrespondences()) {
            result.append(correspondence).append("\n");
        }
//        result.append(rewritingStatsString());
        result.append("================ Source to Target TGDs ==================\n");
        for (FORule tgd : getSTTgds()) {
            result.append(tgd).append("\n");
        }
        return result.toString();
    }

    public String rewritingStatsString() {
        StringBuilder result = new StringBuilder();
        result.append("\n");
        result.append("************************* Rewriting Statistics *****************************\n");
        result.append("Number of ST tgds: ").append(getCandidateSTTgds().size()).append("\n");
        if (this.mappingTask.getConfig().rewriteOverlaps()) {
            result.append("Number of ST tgds after overlaps: ").append(getOverlapSTTgds().size()).append("\n");
            result.append("Number of ST tgd overlaps: ").append(getSTTGDOverlapMap().getNumberOfOverlaps()).append("\n");
        }
        if (!hasSelfJoinsInTgdConclusions()) {
            result.append("Number of ST tgd subsumptions: ").append(getSTSubsumptionMap().getSize()).append("\n");
            result.append("Number of ST tgd coverages: ").append(getSTCoverageMap().getNumberOfCoverages()).append("\n");
        }
        if (hasSelfJoinsInTgdConclusions() && mappingTask.getConfig().rewriteSelfJoins()) {
            result.append("Number of expansions: ").append(getExpansions().size()).append("\n");
            result.append("More compact orderings: ").append(getExpansionsMoreCompactOrderMap().getSize()).append("\n");
            result.append("More informative orderings: ").append(getExpansionsMoreInformativeOrderMap().getSize()).append("\n");
            result.append("Number of normalized expansion rule subsumptions: ").append(getExpansionRulesSubsumptionMap().getSize()).append("\n");
        }
        result.append("Number of rewritten rules: ").append(getRewrittenRules().size()).append("\n");
        result.append("**********************************************************\n");
        return result.toString();
    }

    public String toLongString() {
        StringBuilder result = new StringBuilder();
        result.append("=============================  MAPPING DATA =============================\n");
        result.append(mappingTask.getConfig());
        result.append("Source Schema:\n").append(this.mappingTask.getSourceProxy()).append("\n");
//        result.append(this.mappingTask.getSource().getMappingData()).append("\n");
        result.append("Target Schema:\n").append(this.mappingTask.getTargetProxy()).append("\n");
//        result.append(this.mappingTask.getTarget().getMappingData()).append("\n");
        result.append("================ Correspondences ===================\n");
        for (ValueCorrespondence correspondence : this.mappingTask.getValueCorrespondences()) {
            result.append(correspondence).append("\n");
        }
        result.append(rewritingStatsString());
        result.append("================ Candidate Source to Target TGDs ==================\n");
        RenameSetAliases renamer = new RenameSetAliases();
        List<FORule> renamedCandidateTgds = renamer.renameAliasesInRules(getCandidateSTTgds());
        for (FORule tgd : renamedCandidateTgds) {
            result.append(tgd).append("\n");
        }
        result.append("================ Candidate Source to Target TGDs  (in logical form) ==================\n");
        for (FORule tgd : renamedCandidateTgds) {
            result.append(tgd.toLogicalString(mappingTask)).append("\n");
        }
        if (this.mappingTask.getConfig().rewriteOverlaps()) {
            result.append("================ Final Source to Target TGDs ==================\n");
            List<FORule> renamedTgds = renamer.renameAliasesInRules(getSTTgds());
            for (FORule tgd : renamedTgds) {
                result.append(tgd).append("\n");
            }
            result.append("================ Final Source to Target TGDs (in logical form) ==================\n");
            for (FORule tgd : renamedTgds) {
                result.append(tgd.toLogicalString(mappingTask)).append("\n");
            }
        }
        result.append("================ Final FO Rules ==================\n");
        List<FORule> renamedRewrittenRules = getRewrittenRules();
//        List<FORule> renamedRewrittenRules = renamer.renameAliasesInRules(getRewrittenRules());
        for (FORule tgd : renamedRewrittenRules) {
            result.append(tgd).append("\n");
        }
        result.append("================ Final FO Rules (in logical form) ==================\n");
        for (FORule tgd : renamedRewrittenRules) {
            result.append(tgd.toLogicalString(mappingTask)).append("\n");
//            result.append("Generators: \n").append(tgd.getGenerators(mappingTask));
        }
        return result.toString();
    }

    public String toVeryLongString() {
        StringBuilder result = new StringBuilder();
        result.append(toLongString());
        if (this.mappingTask.getConfig().rewriteOverlaps()) {
            result.append("----------------------   Overlap Map -----------------------\n");
            result.append(this.getSTTGDOverlapMap()).append("\n");
        }
        if (!hasSelfJoinsInTgdConclusions()) {
            result.append("----------------------   TGD Subsumptions -----------------------\n");
            result.append(this.getSTSubsumptionMap()).append("\n");
            result.append("---------------------------   TGD Coverages ---------------------------\n");
            result.append(this.getSTCoverageMap()).append("\n");
        }
        if (hasSelfJoinsInTgdConclusions()) {
            result.append("----------------------   Expansions -----------------------\n");
            result.append(this.getExpansionsMap().toString(mappingTask)).append("\n");
            result.append("----------------------   Expansion 'More Compact' Partial Order  -----------------------\n");
            result.append(this.getExpansionsMoreCompactOrderMap()).append("\n");
            result.append("----------------------   Expansion 'More Informative Partial Order' -----------------------\n");
            result.append(this.getExpansionsMoreInformativeOrderMap()).append("\n");
            result.append("----------------------   Final Subsumptions -----------------------\n");
            result.append(this.getExpansionRulesSubsumptionMap()).append("\n");
        }
        result.append("======================   Rewritten Rules ====================================\n");
        for (FORule rule : getRewrittenRules()) {
            result.append(rule).append("\n");
            result.append(rule.getGenerators(mappingTask)).append("\n");
        }
//        result.append("-----------------------------  TRANSFORMATION -----------------------------\n");
//        result.append(this.getAlgebraTree()).append("\n");
        return result.toString();
    }
}
