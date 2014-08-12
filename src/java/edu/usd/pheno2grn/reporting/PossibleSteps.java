package edu.usd.pheno2grn.reporting;

/**
 * Class containing all possible steps in the workflow.
 */
public class PossibleSteps {

    public static final String PHENOSCAPE_QUERY = "Phenoscape Query";
    public static final String PSICQUIC_QUERY = "PSICQUIC Query";
    public static final String CYTOSCAPE_PSICQUIC = "PSICQUIC Graph Visualization";
    public static final String CYTOSCAPE_GRNINFER_IPLANT = "GRNInfer Graph Visualization Run on Ipant";
    public static final String CYTOSCAPE_COMBINED_IPLANT = "Combined Graph Visualization Run on iPlant";
    public static final String CYTOSCAPE_COMBINED_USER_UPLOADED = "Combined Graph Visualization User Uploaded .dot File";
    public static final String CYTOSCAPE_USER_UPLOADED = "Graph Visualization of User Uploaded .dot File";
    public static final String VENN_DIAGRAM = "Gene Intersection Diagram";
    public static final String GRNINFER_IPLANT = "GRNInfer iPlant Run";
    public static final String ENSEMBL_HOMOLOGUES = "Homologue Query";
}
