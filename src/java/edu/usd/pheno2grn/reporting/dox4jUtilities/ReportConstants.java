/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.usd.pheno2grn.reporting.dox4jUtilities;

import edu.usd.pheno2grn.reporting.PossibleSteps;
import edu.usd.pheno2grn.reporting.ReportStep;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Nick.Weinandt
 */
public class ReportConstants {

    protected static final String reportHeader = "******************************\n"
            + "* Pheno2GRN Report *\n"
            + "******************************\n"
            + "";

    protected static final String firstSummaryText = "This work aims to understand the ";
    protected static final String secondSummaryText = ", and the impact on genomic and metagenomic processes. "
            + "This report aids in the reproducibility and sharability of results[1].";

    protected static final String methodsContents = "Pheno2GRN consists of a number of steps outlined in the image below.  The steps completed"
            + " and selected during this session are described below the image.";

    
    
    
    protected static String mapStepToDescription(ReportStep reportStep) {
        String stepName = reportStep.getStepName();

        if (stepName.equals(PossibleSteps.PHENOSCAPE_QUERY)) {
            return "Query phenomics databases and ontologies (currently Phenoscape KB) to "
            + "extract phenotype ids related to a phenotype keyword such as fin or limb.";
        }
        if (stepName.equals(PossibleSteps.PSICQUIC_QUERY)) {
            return "Submit interactor query to 31 PSICQUIC standardized molecular interaction databases.";
        }
        if (stepName.equals(PossibleSteps.GRNINFER_IPLANT)) {
            return "Submit GRNInfer job to iPlant using user's inputted dataset.";
        }
        if (stepName.equals(PossibleSteps.CYTOSCAPE_COMBINED_IPLANT)) {
            return "Generate combined PSICQUIC and GRNInfer interaction network";
        }
        if (stepName.equals(PossibleSteps.CYTOSCAPE_COMBINED_USER_UPLOADED)) {
            return "Generate combined PSICQUIC and user uploaded .dot file interaction network";
        }
        if (stepName.equals(PossibleSteps.CYTOSCAPE_GRNINFER_IPLANT)) {
            return "Generate interaction network for GRNInfer results only.";
        }
        if (stepName.equals(PossibleSteps.CYTOSCAPE_PSICQUIC)) {
            return "Generate interaction network obtained directly from PSICQUIC query results";
        }
        if (stepName.equals(PossibleSteps.CYTOSCAPE_USER_UPLOADED)) {
            return "Generate interaction network for user uploaded .dot file only.";
        }
        if (stepName.equals(PossibleSteps.ENSEMBL_HOMOLOGUES)) {
            return "Query ENSEMBL for homologue genes in a user inputed target species.";
        }
        if (stepName.equals(PossibleSteps.VENN_DIAGRAM)) {
            return "Create Venn Diagram detailing the intersection of multiple gene lists.";
        }

        //returning empty string if reporting step doesn't match (should never get here)
        return "";
    }

    protected static List<String> mapStepToReferences(ReportStep reportStep) {
        List<String> references = new ArrayList<>();

        switch (reportStep.getStepName()) {
            case PossibleSteps.PHENOSCAPE_QUERY:
                references.add("Mabee, B. P., Balhoff, J. P., Dahdul, W. M., Lapp, H., Midford, P. E., Vision, T. J., & Westerfield, M. (2012). 500,000 fish phenotypes: The new informatics landscape for evolutionary and developmental biology of the vertebrate skeleton. Zeitschrift Fur Angewandte Ichthyologie = Journal of Applied Ichthyology, 28(3), 300–305. doi:10.1111/j.1439-0426.2012.01985.x");
                references.add("Groth, P., Pavlova, N., Kalev, I., Tonov, S., Georgiev, G., Pohlenz, H.-D., & Weiss, B. (2007). PhenomicDB: a new cross-species genotype/phenotype resource. Nucleic Acids Research, 35(Database issue), D696–9. doi:10.1093/nar/gkl662");
                break;
            case PossibleSteps.ENSEMBL_HOMOLOGUES:
                references.add("Flicek, P., Ahmed, I., Amode, M. R., Barrell, D., Beal, K., Brent, S., … Searle, S. M. J. (2013). Ensembl 2013. Nucleic Acids Research, 41(Database issue), D48–55. doi:10.1093/nar/gks1236");
                break;
            case PossibleSteps.GRNINFER_IPLANT:
                references.add("Wang, Y., Joshi, T., Zhang, X., & Xu, D. (2006). Inferring Gene Regulatory Networks From Multiple Microarray Datasets. Bioinformatics, 22(19), 2413–2420.");
                break;
            default:
                break;        
        }
        return references;
    }
}
