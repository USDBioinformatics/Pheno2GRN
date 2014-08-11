package edu.usd.pheno2grn.restdatabases.phenoscape;

import java.util.List;

/**
 * Contains functionality for generating tab delimited data.
 */
public class PhenoscapeUtilities {

    /**
     * Generates tab delimited data without headers.
     *
     * @param phenoIds List of phenotype Ids
     * @return Tab delimited data. Columns represent the gene list for a
     * phenotype. The columns are most likely different lengths.
     */
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
