/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.usd.pheno2grn.restdatabases.phenoscape;

import edu.usd.pheno2grn.utilities.json.GraphColors;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Nick.Weinandt
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

    public String getNumberOfAssociatedGenes(){
        return ""+this.getGenesAssociatedWith().size();
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
