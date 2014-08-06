/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.usd.pheno2grn.restdatabases.phenoscape;

import java.util.List;

/**
 *
 * @author Nick.Weinandt
 */
public class PhenoscapeUtilities {

    public static String generateTabDelimitedPhenoIdAndGenes(List<PhenotypeIdentifier> phenoIds) {
        StringBuilder sb = new StringBuilder();

        //priting all the Ids on the first line, and finding the one with the most genes
        int mostGenesLength = 0;
        for (PhenotypeIdentifier phenoId : phenoIds) {
            sb.append(phenoId.getPhenotypeDescription() + "\t");
            if (phenoId.getGenesAssociatedWith().size() > mostGenesLength) {
                mostGenesLength = phenoId.getGenesAssociatedWith().size();
            }
        }
        sb.append("\n");

        int lineNumber = 0;
        while (lineNumber < mostGenesLength) {
            for (int i = 0; i < phenoIds.size(); i++) {
                if (phenoIds.get(i).getGenesAssociatedWith().size() > lineNumber) {
                    sb.append(phenoIds.get(i).getGenesAssociatedWith().get(lineNumber) + "\t");
                } else {
                    sb.append("\t");
                }
            }
            lineNumber++;
            sb.append("\n");
        }
        return sb.toString();
    }
}
