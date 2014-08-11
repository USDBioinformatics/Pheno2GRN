package edu.usd.pheno2grn.restdatabases.psicquic;

import java.util.List;

/**
 * Performs some of the utilities of PSICQUIC.
 */
public class PsicquicUtilities {

    /**
     * Generates tabDelimited results for the PSICQUIC result. Doesn't include
     * headers.
     *
     * @param psicquicResults   List of psicquicResults. Can't be null.
     * @return
     */
    public static String generateTabDelimitedResults(List<PsicquicResult> psicquicResults) {
        if(psicquicResults==null){
            throw new IllegalArgumentException("Psicquic Result list cannot be null.");
        }
        StringBuilder sb = new StringBuilder();

        for (PsicquicResult result : psicquicResults) {
            sb.append(result.getInteractorAID() + "\t");
            sb.append(result.getInteractorBID() + "\t");
            sb.append(result.getGeneNameA() + "\t");
            sb.append(result.getGeneNameB() + "\n");
        }

        return sb.toString();
    }
}
