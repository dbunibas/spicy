package it.unibas.spicy.model.mapping.rewriting;

public class RewritingConfiguration {

    private boolean rewriteSubsumptions;
    private boolean rewriteCoverages;
    private boolean rewriteSelfJoins;
    private boolean rewriteOnlyProperHomomorphisms;
    private boolean rewriteOverlaps;
    private boolean useLocalSkolems;

    public boolean isRewriteSubsumptions() {
        return rewriteSubsumptions;
    }

    public void setRewriteSubsumptions(boolean rewriteSubsumptions) {
        this.rewriteSubsumptions = rewriteSubsumptions;
    }

    public boolean isRewriteCoverages() {
        return rewriteCoverages;
    }

    public void setRewriteCoverages(boolean rewriteCoverages) {
        this.rewriteCoverages = rewriteCoverages;
    }

    public boolean isRewriteSelfJoins() {
        return rewriteSelfJoins;
    }

    public void setRewriteSelfJoins(boolean rewriteSelfJoins) {
        this.rewriteSelfJoins = rewriteSelfJoins;
    }

    public boolean isRewriteOnlyProperHomomorphisms() {
        return rewriteOnlyProperHomomorphisms;
    }

    public void setRewriteOnlyProperHomomorphisms(boolean rewriteOnlyProperHomomorphisms) {
        this.rewriteOnlyProperHomomorphisms = rewriteOnlyProperHomomorphisms;
    }

    public boolean isRewriteOverlaps() {
        return rewriteOverlaps;
    }

    public void setRewriteOverlaps(boolean rewriteOverlaps) {
        this.rewriteOverlaps = rewriteOverlaps;
    }

    public boolean isUseLocalSkolems() {
        return useLocalSkolems;
    }

    public void setUseLocalSkolems(boolean useLocalSkolems) {
        this.useLocalSkolems = useLocalSkolems;
    }

    @Override
    public String toString() {
        return "RewritingConfiguration{" + 
                "rewriteSubsumptions=" + rewriteSubsumptions + 
                ", rewriteCoverages=" + rewriteCoverages + 
                ", rewriteSelfJoins=" + rewriteSelfJoins + 
                ", rewriteOnlyProperHomomorphisms=" + rewriteOnlyProperHomomorphisms + 
                ", rewriteOverlaps=" + rewriteOverlaps + 
                ", useLocalSkolems=" + useLocalSkolems + '}';
    }
}
