package edu.usd.pheno2grn.restdatabases.phenoscape;

import edu.usd.pheno2grn.utilities.json.GraphColors;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a phenotype from phenoscape. Contains attributes for determining
 * color of nodes associated with the PhenotypeIdentifier.
 */
public class PhenotypeIdentifier {

    public String id = "";
    public List<String> genesAssociatedWith = new ArrayList<>();
    boolean important = false;
    GraphColors color = new GraphColors(GraphColors.BROWN, "Brown");
    private String phenotypeDescription;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PhenotypeIdentifier)) {
            return false;
        }

        PhenotypeIdentifier convertedObj = (PhenotypeIdentifier) obj;
        return convertedObj.getId().equals(this.getId());
    }

    public String getNumberOfAssociatedGenes() {
        return "" + this.getGenesAssociatedWith().size();
    }

    public String getPhenotypeDescription() {
        return phenotypeDescription;
    }

    public void setPhenotypeDescription(String phenotypeDescription) {
        this.phenotypeDescription = phenotypeDescription;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getGenesAssociatedWith() {
        return genesAssociatedWith;
    }

    public void setGenesAssociatedWith(List<String> genesAssociatedWith) {
        this.genesAssociatedWith = genesAssociatedWith;
    }

    public GraphColors getColor() {
        return color;
    }

    public void setColor(GraphColors color) {
        this.color = color;
    }

}
