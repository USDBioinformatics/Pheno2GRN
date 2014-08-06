package edu.usd.pheno2grn.restdatabases.ensembl;

/**
 * This class represents the reselt of conducting a homology query in the
 * ensembl database.
 *
 * @author Nick.Weinandt
 */
public class HomologyResult {

    private String sourceGeneSymbol;
    private String targetGeneSymbol;
    private String sourceEnsemblId;
    private String targetEnsemblId;
    private String sourceSpecies;
    private String targetSpecies;

    public String getSourceSpecies() {
        return sourceSpecies;
    }

    public void setSourceSpecies(String sourceSpecies) {
        this.sourceSpecies = sourceSpecies;
    }

    public String getTargetSpecies() {
        return targetSpecies;
    }

    public void setTargetSpecies(String targetSpecies) {
        this.targetSpecies = targetSpecies;
    }

    public String getSourceGeneSymbol() {
        return sourceGeneSymbol;
    }

    public void setSourceGeneSymbol(String sourceGeneSymbol) {
        this.sourceGeneSymbol = sourceGeneSymbol;
    }

    public String getTargetGeneSymbol() {
        return targetGeneSymbol;
    }

    public void setTargetGeneSymbol(String targetGeneSymbol) {
        this.targetGeneSymbol = targetGeneSymbol;
    }

    public String getSourceEnsemblId() {
        return sourceEnsemblId;
    }

    public void setSourceEnsemblId(String sourceEnsemblId) {
        this.sourceEnsemblId = sourceEnsemblId;
    }

    public String getTargetEnsemblId() {
        return targetEnsemblId;
    }

    public void setTargetEnsemblId(String targetEnsemblId) {
        this.targetEnsemblId = targetEnsemblId;
    }
}
