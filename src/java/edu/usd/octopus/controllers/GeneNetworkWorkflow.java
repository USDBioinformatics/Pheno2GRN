/**
 * Copyright 2013 University of South Dakota
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package edu.usd.octopus.controllers;

import edu.usd.octopus.exceptions.IPlantException;
import edu.usd.octopus.exceptions.NoResultQueryException;
import edu.usd.octopus.exceptions.PackageFileReadException;
import edu.usd.octopus.exceptions.QueryException;
import edu.usd.octopus.exceptions.ReportCreationException;
import edu.usd.octopus.exceptions.WorkflowException;
import edu.usd.octopus.fileconversion.DOTConverter;
import edu.usd.octopus.iplant.GRNInfer;
import edu.usd.octopus.iplant.IplantFunctionality;
import edu.usd.octopus.iplant.JobFunctionality;
import edu.usd.octopus.exceptions.JobSubmissionException;
import edu.usd.octopus.reporting.PossibleSteps;
import edu.usd.octopus.reporting.ReportStep;
import edu.usd.octopus.reporting.ReportStepUtilities;
import edu.usd.octopus.reporting.dox4jUtilities.ReportCreation;
import edu.usd.octopus.restdatabases.ensembl.EnsemblQueries;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import org.primefaces.model.DualListModel;
import edu.usd.octopus.restdatabases.ensembl.HomologyResult;
import edu.usd.octopus.restdatabases.phenoscape.PhenoscapeQuery;
import edu.usd.octopus.restdatabases.phenoscape.PhenoscapeUtilities;
import edu.usd.octopus.restdatabases.phenoscape.PhenotypeIdentifier;
import edu.usd.octopus.restdatabases.psicquic.PSICQUICQueryRunnable;
import edu.usd.octopus.restdatabases.psicquic.PsicquicQuery;
import edu.usd.octopus.restdatabases.psicquic.PsicquicResult;
import edu.usd.octopus.restdatabases.psicquic.PsicquicSynchronizedResultSet;
import edu.usd.octopus.restdatabases.psicquic.PsicquicUtilities;
import edu.usd.octopus.restdatabases.psicquic.QueryPosition;
import edu.usd.octopus.utilities.json.CreateCytoscapeJSON;
import edu.usd.octopus.utilities.json.Edge;
import edu.usd.octopus.utilities.json.GraphColors;
import edu.usd.octopus.utilities.json.Node;
import edu.usd.octopus.venn.VennPanel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.iplant.foundation_api.job.Job;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * This class is the controller class for the Gene Network Mapper Workflow
 *
 * @author Nick Weinandt
 */
@ManagedBean(eager = true, name = "GeneNetworkWorkflow")
@SessionScoped
public class GeneNetworkWorkflow implements Serializable, HttpSessionBindingListener {

    //fields for color table of graph
    private boolean showReportingPanel;
    private String phenoscapeInput = "";
    private List<PhenotypeIdentifier> phenoIds = new ArrayList<>();

    //fields needed for the Phenoscape Query page
    private DualListModel<String> genes = new DualListModel<>();
    private boolean homologues;
    private List<HomologyResult> homologyResults;
    private String sourceSpeciesForHomology;
    private String targetSpeciesForHomology;
    private boolean includeIndirectInteractions = false;
    private int totInteractNum;
    private List<PaginationStep> paginationList;
    private String spaceSeparatedGenes;

    //VennDiagram Fields
    private boolean showVennImage = false;
    private File vennImageFile;

    //fields needed table information page
    private List<PsicquicResult> psicquicResultList;
    private List<PsicquicResult> filteredPsicquicResults;
    private PsicquicResult[] selectedPsicquicResults;

    //fields needed for the graph display
    private String psicquicJson = "";
    private String grninferJson = "";
    private String psicquicAndGrninferCombinedJson = "";

    private PhenotypeIdentifier selectedIdentifier;
    private String newColor;
    private boolean showVenn = false;
    private List<PhenotypeIdentifier> selectedVennPhenoIds;
    private List<PhenotypeIdentifier> completeVennList;

    //grninfer fields
    private Job grninferJob;
    boolean jobHasBeenSubmitted = false;
    boolean jobIsRunning = false;
    private List<Node> psicquicNodes;
    private List<Edge> psicquicEdges;
    private List<ReportStep> reportingSteps;
    private List<ReportStep> selectedReportingSteps;

    //reporting fields
    private StreamedContent reportStream;

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        if (vennImageFile != null && vennImageFile.exists()) {
            System.out.println("Session Ended, deleting the file");
            vennImageFile.delete();
        }
    }

    public void vennFileUpload(FileUploadEvent event) {
        System.out.println("In the file upload");
        //creating new pheno ID
        PhenotypeIdentifier phenoId = new PhenotypeIdentifier();
        //getting last name and incrementing
        if (phenoIds.size() != completeVennList.size()) {
            //getting the name of the last Venn user uploaded id and incrementing
            PhenotypeIdentifier lastPheno = completeVennList.get(completeVennList.size() - 1);
            String lastName = lastPheno.getPhenotypeDescription();
            int lastNumber = Integer.parseInt(lastName.substring(lastName.length() - 1));
            System.out.println("last number: " + lastNumber);
            phenoId.setPhenotypeDescription("User Venn Uploaded #" + ++lastNumber);

        } else {
            phenoId.setPhenotypeDescription("User Venn Uploaded #1");
        }
        phenoId.setId("Venn Diagram Use Only");

        //adding genes associated with
        Scanner sc;
        try {
            sc = new Scanner(event.getFile().getInputstream());
        } catch (IOException e) {
            System.out.println("Getting info from the file bombed");
            return;
        }
        while (sc.hasNext()) {
            String nextGene = sc.next();
            if (!phenoId.getGenesAssociatedWith().contains(nextGene)) {
                phenoId.getGenesAssociatedWith().add(nextGene);
            }
        }
        //adding the new pheno Id to the venn list
        completeVennList.add(phenoId);
    }

    public void submitSpaceSeparatedGenes() {
        try {
            resetInterface();

            if (spaceSeparatedGenes == null | spaceSeparatedGenes.isEmpty()) {
                throw new WorkflowException("Cannot be empty.");
            }

            //clearing out the phenoscape query text
            phenoscapeInput = "";

            Scanner sc = new Scanner(spaceSeparatedGenes);
            PhenotypeIdentifier phenoId = new PhenotypeIdentifier();
            phenoId.setId("Space Separated Genes");
            phenoId.setPhenotypeDescription("User uploaded genes.");

            //using a hashSet to avoid duplicates
            HashSet<String> noDupsSet = new HashSet<>();
            boolean hadDuplicates = false;
            while (sc.hasNext()) {
                if (!noDupsSet.add(sc.next())) {
                    hadDuplicates = true;
                }
            }
            if (hadDuplicates) {
                FacesContext.getCurrentInstance().addMessage(
                        "growl", new FacesMessage("Removed Duplicates",
                                "Duplicates from submitted gene list removed."));

            }
            phenoId.setGenesAssociatedWith(new ArrayList<>(noDupsSet));

            genes.getTarget().addAll(phenoId.getGenesAssociatedWith());
            phenoIds.add(phenoId);

            try {
                //setting the max number of results
                totInteractNum = PsicquicQuery.countAll(genes.getTarget());

                updatePsicquicTable();

            } catch (QueryException e) {
                throw new WorkflowException("Could not query PSICQUIC. Please try again.");
            }
            showReportingPanel = true;
            updateDisplay();
        } catch (WorkflowException e) {
            FacesContext.getCurrentInstance().addMessage("userGeneUploadMessages",
                    new FacesMessage("Problem Submitting Genes", e.getMessage()));
        }
    }

    public void updateFromGeneSelection() {
        PaginationStep firstStep = paginationList.get(0);

        paginationList = new ArrayList<>();
        paginationList.add(firstStep);
        try {
            updatePsicquicTable();
        } catch (QueryException e) {
            FacesContext.getCurrentInstance().addMessage("geneSelectionMessages",
                    new FacesMessage("Problem with query", "Please try again."));
            return;
        }

        updateDisplay();
    }

    public void nextPsicquicPagination() {
        try {
            updatePsicquicTable();
        } catch (QueryException e) {
            FacesContext.getCurrentInstance().addMessage("paginationMessages",
                    new FacesMessage("Error Getting Next Data", e.getMessage()));
            return;
        }

        updateDisplay();
    }

    public void previousPsicquicPagination() {
        //removing last two pagination resutls
        paginationList.remove(paginationList.size() - 1);

        //arrayList size has decreased, so removing at same position
        paginationList.remove(paginationList.size() - 1);

        try {
            updatePsicquicTable();
        } catch (QueryException e) {
            FacesContext.getCurrentInstance().addMessage("paginationMessages",
                    new FacesMessage("Error Getting Previous Data", e.getMessage()));
            return;
        }
        updateDisplay();
    }

    public void uploadFileContainingGenes(FileUploadEvent fileUpEvent) throws Exception {
        resetInterface();

        //clearing out the phenoscape query text
        phenoscapeInput = "";

        Scanner sc = new Scanner(fileUpEvent.getFile().getInputstream());
        PhenotypeIdentifier phenoId = new PhenotypeIdentifier();
        phenoId.setId("Uploaded Genes");
        phenoId.setPhenotypeDescription("User uploaded genes.");

        //using a hashSet to avoid duplicates
        HashSet<String> noDupsSet = new HashSet<>();
        boolean hadDuplicates = false;
        while (sc.hasNext()) {
            if (!noDupsSet.add(sc.next())) {
                hadDuplicates = true;
            }
        }
        if (hadDuplicates) {
            FacesContext.getCurrentInstance().addMessage(
                    "growl", new FacesMessage("Removed Duplicates",
                            "Duplicates from submitted gene list removed."));

        }
        phenoId.setGenesAssociatedWith(new ArrayList<>(noDupsSet));

        genes.getTarget().addAll(phenoId.getGenesAssociatedWith());
        phenoIds.add(phenoId);

        //setting the max number of results
        totInteractNum = PsicquicQuery.countAll(genes.getTarget());

        updatePsicquicTable();

        showReportingPanel = true;
        updateDisplay();

    }

    public void uploadFileRow(FileUploadEvent fileUpEvent) {
        //this attribute could just be the rowstep
        ReportStep stepName = (ReportStep) fileUpEvent.getComponent().getAttributes().get("ReportStep");
        stepName.setImageFile(fileUpEvent.getFile());
    }

    public void generateFinalReport() {
        OutputStream output = null;
        FacesContext fc = null;
        try {
            WordprocessingMLPackage wordMLPackage = null;
            try {
                if (reportingSteps == null || phenoIds == null) {
                    return;
                }
                wordMLPackage = ReportCreation.createReport(selectedReportingSteps, phenoIds,
                        phenoscapeInput, psicquicResultList, vennImageFile);

            } catch (ReportCreationException e) {
                e.printStackTrace();
                throw new WorkflowException(e.getMessage());
            }

            //Taking output stream and converting to inputsteam
            fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();

            ec.responseReset();
            ec.setResponseContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            // ec.setResponseContentLength(1000); 
            ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + "test.docx" + "\"");

            try {
                output = ec.getResponseOutputStream();
            } catch (IOException e) {
                throw new WorkflowException("Could not supply download.");
            }

            try {
                wordMLPackage.save(output);
            } catch (Docx4JException e) {
                throw new WorkflowException("Could not save output");
            }
        } catch (WorkflowException e) {

        } finally {
            //cleaning up
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    System.out.println("Couldn't close Faces report outputStream");
                }
            }
            if (fc != null) {
                fc.responseComplete();
            }
        }

    }

    public void generateStream(ReportStep reportStep) {
        if (reportStep.getStepName().equals(PossibleSteps.PHENOSCAPE_QUERY)) {
            InputStream in = IOUtils.toInputStream(PhenoscapeUtilities.generateTabDelimitedPhenoIdAndGenes(phenoIds));

            reportStream = new DefaultStreamedContent(in, "text/html", reportStep.getFileName());
        } else if (reportStep.getStepName().equals(PossibleSteps.PSICQUIC_QUERY)) {
            InputStream in = IOUtils.toInputStream(PsicquicUtilities.generateTabDelimitedResults(psicquicResultList));
            reportStream = new DefaultStreamedContent(in, "text/html", reportStep.getFileName());
        } else if (reportStep.getStepName().equals(PossibleSteps.GRNINFER_IPLANT)) {
            //getting the file from iplant
            File tempFile = null;
            try {
                tempFile = IplantFunctionality.downloadFile(grninferJob.getArchivePath().substring(19) + "grninferInput.dot");

                //setting the download file stream
                reportStream = new DefaultStreamedContent(new FileInputStream(tempFile), "text/html", "GRNInfer_Ouput.dot");
            } catch (IPlantException | FileNotFoundException e) {
                FacesContext.getCurrentInstance().addMessage("reportMessages",
                        new FacesMessage("Could Not Retrieve File", "iPlant may be down."));
            } finally {
                if (tempFile != null) {
                    tempFile.delete();
                }
            }
        } else if (reportStep.getStepName().equals(PossibleSteps.VENN_DIAGRAM)) {
            //reseting the the Venn Image stream, as it has already be completely read to display the image
            try {
                reportStream = new DefaultStreamedContent(new FileInputStream(vennImageFile), "image/png", "Venn.png");
            } catch (IOException e) {
                FacesContext.getCurrentInstance().addMessage("reportMessages",
                        new FacesMessage("Could not retrieve image file", "Please regenerate the venn diagram."));
            }
        }
    }

    public void checkGRNInferStatus() {
        try {

            if (JobFunctionality.isJobStillRunning(grninferJob)) {
                FacesMessage message = new FacesMessage("Job Still Running", "Check back later");
                FacesContext.getCurrentInstance().addMessage("grninferGrowl", message);
                FacesContext.getCurrentInstance().addMessage("combinedGrowl", message);
            } else {
                jobIsRunning = false;
                jobHasBeenSubmitted = true;
                if (JobFunctionality.didJobFail(grninferJob)) {
                    //Job failed, notifing the user to try again
                    FacesMessage message = new FacesMessage("Job Failed", "Navigate to \"Reverse Engineering\""
                            + "tab to try again.");
                    FacesContext.getCurrentInstance().addMessage("grninferGrowl", message);
                    FacesContext.getCurrentInstance().addMessage("combinedGrowl", message);
                } else {
                    //getting contents of file and displaying them
                    String path = grninferJob.getArchivePath() + "grninferInput.dot";
                    File grninferOutput = IplantFunctionality.downloadFile(path.substring(19));

                    //conveting the file contents to a string
                    try {
                        String fileContents = FileUtils.readFileToString(grninferOutput);
                        grninferOutput.delete();

                        HashSet<Node> listOfNodes = new HashSet<>(DOTConverter.convertDotToList(fileContents));
                        //creating a list of edges
                        HashSet<Edge> listOfEdges = new HashSet<>();
                        for (Node node : listOfNodes) {
                            List<String> neighbors = node.getNodesConnectedTo();
                            for (String s : neighbors) {
                                Edge newEdge = new Edge();
                                newEdge.setSource(node.getNodeName());
                                newEdge.setTarget(s);
                                listOfEdges.add(newEdge);
                            }
                        }
                        grninferJson = CreateCytoscapeJSON.convertListToJSON(new ArrayList(listOfNodes),
                                new ArrayList(listOfEdges), new ArrayList<PhenotypeIdentifier>());

                        //adding grninfer alone as a report step
                        ReportStep grninferAloneJSON = new ReportStep(PossibleSteps.CYTOSCAPE_GRNINFER_IPLANT);
                        grninferAloneJSON.setHasImage(true);
                        ReportStepUtilities.addStepToCompleteListAndSelectedList(reportingSteps, selectedReportingSteps, grninferAloneJSON);

                        //created json necessary for the combined graph
                        //checking if grninfer already has the node. If it does, changing color to show overlap
                        for (Node psicquicNode : psicquicNodes) {
                            //creating new node and adding all the psicquicNode
                            //properties so not passing by reference
                            Node holderNode = new Node();
                            holderNode.setNodeName(psicquicNode.getNodeName());
                            holderNode.setNodeColor(GraphColors.GREEN);

                            //if the list already contains the node, changing the
                            //node in the list to black
                            boolean alreadyInList = !listOfNodes.add(holderNode);
                            if (alreadyInList) {
                                //removing the node to change the color
                                listOfNodes.remove(holderNode);
                                holderNode.setNodeColor(GraphColors.BLACK);

                                //adding node back in after color change
                                listOfNodes.add(holderNode);
                            }
                        }

                        //seeing if possible to add the edges
                        for (Edge psicquicEdge : psicquicEdges) {
                            listOfEdges.add(psicquicEdge);
                        }
                        psicquicAndGrninferCombinedJson = CreateCytoscapeJSON.convertListToJSON(new ArrayList<>(listOfNodes),
                                new ArrayList<>(listOfEdges), new ArrayList<PhenotypeIdentifier>());
                        //adding grninfer combined graph as a reporting step
                        ReportStep grninferCombined = new ReportStep(PossibleSteps.CYTOSCAPE_COMBINED_IPLANT);
                        grninferCombined.setHasImage(true);
                        ReportStepUtilities.addStepToCompleteListAndSelectedList(reportingSteps, selectedReportingSteps, grninferCombined);

                    } catch (IOException e) {
                        //let the user know it bombed
                        System.out.println("Problem generating grninfer json");
                    }
                    FacesContext.getCurrentInstance()
                            .addMessage("grninferGrowl", new FacesMessage("Job Completed", ""));

                    //adding grninfer as a report step
                    ReportStep grninferIplant = new ReportStep(PossibleSteps.GRNINFER_IPLANT);
                    grninferIplant.setFileName("GRNInfer_Ouput.dot");
                    ReportStepUtilities.addStepToCompleteListAndSelectedList(reportingSteps, selectedReportingSteps, grninferIplant);

                }

            }
        } catch (IPlantException e) {
            FacesContext.getCurrentInstance()
                    .addMessage("grninferGrowl", new FacesMessage("iPlant Not Functioning Properly", "Navigate to Reverse Engineering tab to try again."));
        }
    }

    public void onRowSelectVennTable(SelectEvent selectEvent) {
        //Making sure the number of selected phenotypes does exceed 4
        if (selectedVennPhenoIds.size() > 4) {

            //removing the selected pheno from the list
            FacesContext.getCurrentInstance().addMessage("vennSelectionTableMessages",
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Please select a max of 4", "You can unselect by holding Ctrl and clicking on the row"));
            selectedVennPhenoIds.remove((PhenotypeIdentifier) selectEvent.getObject());

        }
        //hiding the Venn picture, no matter what, only button can display it
        showVennImage = false;
    }

    public void onRowUnselectVennTable(UnselectEvent unselectEvent) {
        if (selectedVennPhenoIds == null || selectedVennPhenoIds.size() < 2) {
            showVennImage = false;
        }
    }

    public void setUpVennDiagram() {
        try {
            try {
                //adding grninfer as a report step
                ReportStep vennDiagram = new ReportStep(PossibleSteps.VENN_DIAGRAM);
                vennDiagram.setFileName("Venn.png");

                //deleting the file if vennImageFile already exists
                if (vennImageFile != null) {
                    vennImageFile.delete();
                }

                //looping until find a file which doesn't already exist and using that name
                for (int i = 0; i < 10000; i++) {
                    vennImageFile = new File(i + "venn.png");
                    if (!vennImageFile.exists()) {
                        vennImageFile.createNewFile();
                        break;
                    }
                }

                //if file still does not exist, throwing exception because ran out of names
                if (!vennImageFile.exists()) {
                    throw new WorkflowException("Too many files");
                }
                VennPanel vennPanel=new VennPanel();
                vennPanel.addImageToFile(vennImageFile, PhenoscapeUtilities.generateTabDelimitedPhenoIdAndGenes(selectedVennPhenoIds));
                
                showVennImage = true;
                ReportStepUtilities.addStepToCompleteListAndSelectedList(reportingSteps, selectedReportingSteps, vennDiagram);

            } catch (IOException e) {
                e.printStackTrace();
                throw new WorkflowException("Error generating Venn image.");
            }
        } catch (WorkflowException e) {
            FacesContext.getCurrentInstance().addMessage("vennSelectionTableMessages",
                    new FacesMessage(e.getMessage(), "Try another selection."));
        }
    }

    public void onRowSelect(SelectEvent e) throws Exception {
        selectedIdentifier = (PhenotypeIdentifier) e.getObject();
    }

    public void showVenn() {
        showVenn = true;

        //if the Venn hasn't been shown yet, adding all the phenotype ids in
        if (completeVennList == null) {
            completeVennList = new ArrayList<>(phenoIds);
        }

    }

    public void showCytoscape() {
        showVenn = false;
    }

    public void changeColor() {
        /*
         if the new color is orange, green, pink, or blue, seeing if any other
         Phenotype Identifier has that color. 
         */
        try {
            for (PhenotypeIdentifier id : phenoIds) {
                if (id.equals(selectedIdentifier)) {
                    id.setColor(new GraphColors(newColor, GraphColors.covertHexToName(newColor)));
                }//must be else if, not just if, otherwise assign new value and wipe it at the same time
                else {
                    if (id.getColor().getColorValue().equals(newColor)) {
                        //if another tao had that value, setting it to brown
                        id.setColor(new GraphColors(GraphColors.BROWN, GraphColors.covertHexToName(GraphColors.BROWN)));
                    }
                }
            }
            updateDisplay();
            selectedIdentifier = null;
        } catch (Exception e) {
        }

    }

    /////////////////////////////////////////////////////////////////////
    /**
     * Used by Primefaces autocomplete to provide a list results containing the
     * query string. The query and the string it is being compared to are first
     * converted to lowercase and then compared. The Phenotypes.txt file in the
     * edu.usd.octopus.restdatabases.phenoscape package is the resource queried.
     * If Phenoscape is updated, the new file containing the updated phenotypes
     * needs to replace the Phenotypes.txt in that package.
     *
     * @param query String from the inputText being used to query phenotypes
     * @return A list of autocomplete results containg(lower-case) the query
     * string
     */
    public List<String> autocompletePhenoscape(String query) {
        //getting the phenotypes query file from the phenoscape package
        InputStream in = PhenoscapeQuery.class
                .getResourceAsStream("Phenotypes_Sorted_By_Description.txt");

        Scanner lineScanner = new Scanner(in);
        List<String> results = new ArrayList<>();
        String directMatch = null;
        while (lineScanner.hasNextLine()) {
            //getting the second column (as that is the description info)
            Scanner columnScanner = new Scanner(lineScanner.nextLine());
            //using a tab delimination, as it is Phenoscape.txt is tab-delimited
            columnScanner.useDelimiter("\t");
            //skipping over the first column (contains TAO info)
            columnScanner.next();

            String phenotype = columnScanner.next();

            //seeing if query is contained in the phenotype string
            if (phenotype.toLowerCase().contains(query.toLowerCase())) {
                //seeing if the phenotype result was already added to the list
                //binary search is allowed becuase the Phenotypes.txt is sorted by phenotype
                int index = Collections.binarySearch(results, phenotype);
                if (index < 0) {
                    //phenotype wasn't in the list, so adding it
                    //seeing if phenotype directly matches the query (if so adding it at the end)
                    if (phenotype.toLowerCase().equals(query.toLowerCase())) {
                        directMatch = phenotype;
                    } else {
                        results.add(phenotype);
                    }
                }
            }
        }
        //if had a direct match adding it to the begining of the list
        if (directMatch != null) {
            results.add(0, directMatch);
        }
        //cleaning up and closing the input stream
        try {
            in.close();
        } catch (IOException e) {
            //doing nothing, as no way to update messages with a p:autocomplete
        }
        return results;
    }

    private PaginationStep generateNewPagniationStep(PaginationStep input, PaginationStep output) {
        PaginationStep newStep = new PaginationStep();

        //input and output should have the same size
        for (int i = 0; i < input.positionList.size(); i++) {
            //if the input and output have the same position, don't add
            if (!input.positionList.get(i).position.equals(output.positionList.get(i).position)) {
                newStep.positionList.add(output.positionList.get(i));
            }
        }

        return newStep;
    }

    /**
     * Queries Psicquic with target gene. If no genes are selected, or the query
     * doesn't return any results, the table will just be empty
     *
     * @throws QueryException Thrown if an error occurs during the query process
     */
    public void updatePsicquicTable() throws QueryException {
        //if the target gene list is null or empty, clear the psicquic table
        if (genes.getTarget().isEmpty() || genes.getTarget() == null) {
            psicquicResultList = new ArrayList<>();
        } else {
            //multithreading all queries to Psicquic Databases
            PaginationStep copyStep = PaginationStep.deepCopy(paginationList.get(paginationList.size() - 1));

            PsicquicSynchronizedResultSet resultSet = new PsicquicSynchronizedResultSet();
            //keeping a list of the threads
            List<Thread> threadList = new ArrayList<>();
            for (URLAndPosition urlAndPos : copyStep.positionList) {
                Thread thread = new Thread(new PSICQUICQueryRunnable(urlAndPos.url,
                        resultSet, urlAndPos.position, genes.getTarget(), !includeIndirectInteractions));
                thread.start();
                threadList.add(thread);
            }

            for (Thread thread : threadList) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    //do something here
                }
            }

            //updating the pagnination list
            paginationList.add(generateNewPagniationStep(paginationList.get(paginationList.size() - 1), copyStep));

            psicquicResultList = new ArrayList<>(resultSet.resultSet);
        }

        //Automatically selecting every result
        selectedPsicquicResults = psicquicResultList.toArray(new PsicquicResult[0]);
        //adding the phenoscape query to report steps    
        ReportStep phenoscapeQuery = new ReportStep(PossibleSteps.PSICQUIC_QUERY);
        phenoscapeQuery.setFileName("PSICQUIC_INTERACTORS.txt");
        ReportStepUtilities.addStepToCompleteListAndSelectedList(reportingSteps, selectedReportingSteps, phenoscapeQuery);

    }

    /**
     * Updates the Phenoscape/Venn Diagram Display
     */
    public void updateDisplay() {
        selectedIdentifier = null;
        newColor = "";
        showVenn = false;
        if (selectedVennPhenoIds != null) {
            selectedVennPhenoIds.clear();
        }

        psicquicNodes = new ArrayList<>();
        psicquicEdges = new ArrayList<>();

        for (int i = 0; i < selectedPsicquicResults.length; i++) {
            //getting the input and output gene names
            String inputGeneName = selectedPsicquicResults[i].getGeneNameA();
            String outputGeneName = selectedPsicquicResults[i].getGeneNameB();

            //checking to make sure the input/output genes are not null
            //if they are, not adding to the list
            if (inputGeneName == null || outputGeneName == null) {
                continue;
            }

            //finding the PhenoId a node is associated with
            Node inputNode = new Node();
            inputNode.setNodeName(inputGeneName);

            //if the node list doesn't already contain the node, add it
            if (!psicquicNodes.contains(inputNode)) {
                for (PhenotypeIdentifier phenoId : phenoIds) {
                    for (String gene : phenoId.genesAssociatedWith) {
                        if (inputNode.getNodeName().toLowerCase().equals(gene.toLowerCase())) {
                            inputNode.getTaosBelongTo().add(phenoId.getId());
                        }
                    }
                }
                psicquicNodes.add(inputNode);
            }

            //finding the PhenoId a node is associated with
            Node outputNode = new Node();
            outputNode.setNodeName(outputGeneName);

            //seeing if node is already in node list
            boolean alreadyInList = false;
            for (Node n : psicquicNodes) {
                if (n.equals(outputNode)) {
                    alreadyInList = true;
                    break;
                }
            }

            if (!alreadyInList) {
                for (PhenotypeIdentifier phenoId : phenoIds) {
                    for (String gene : phenoId.genesAssociatedWith) {
                        if (outputNode.getNodeName().toLowerCase().equals(gene.toLowerCase())) {
                            outputNode.getTaosBelongTo().add(phenoId.getId());
                        }
                    }
                }
                psicquicNodes.add(outputNode);
            }

            //checking to see if the edge is already in the list and adding it if it isn't
            Edge thisEdge = new Edge();
            thisEdge.setSource(inputGeneName);
            thisEdge.setTarget(outputGeneName);
            alreadyInList = false;
            for (Edge e : psicquicEdges) {
                if (e.equals(thisEdge)) {
                    alreadyInList = true;
                }
            }

            if (!alreadyInList) {
                psicquicEdges.add(thisEdge);
            }
        }
        psicquicJson = CreateCytoscapeJSON.convertListToJSON(psicquicNodes, psicquicEdges, phenoIds);

        //Adding as a report step
        ReportStep psicquicCytoscape = new ReportStep(PossibleSteps.CYTOSCAPE_PSICQUIC);
        psicquicCytoscape.setHasImage(true);
        ReportStepUtilities.addStepToCompleteListAndSelectedList(reportingSteps, selectedReportingSteps, psicquicCytoscape);
    }

    /**
     * Resets entire interface. Hides the reporting table and clears the
     * picklist
     */
    public void resetInterface() throws WorkflowException {
        showReportingPanel = false;
        psicquicResultList = null;
        genes = new DualListModel<>();
        phenoIds.clear();
        showVenn = false;
        reportingSteps = new ArrayList<>();
        selectedReportingSteps = new ArrayList<>();

        //resetting the pagination list
        List<String> psicquicUrls;
        try {
            psicquicUrls = PsicquicQuery.getListOfPsicquicUrls();
        } catch (PackageFileReadException e) {
            throw new WorkflowException("Could not get list of URLs");
        }

        PaginationStep step = new PaginationStep();
        for (String url : psicquicUrls) {
            QueryPosition queryPos = new QueryPosition();
            URLAndPosition urlAndPosition = new URLAndPosition(url, queryPos);
            step.positionList.add(urlAndPosition);
        }
        paginationList = new ArrayList<>();
        paginationList.add(step);
    }

    /**
     * Gets a gene list from Phenoscape. Does this by getting all the phenotype
     * ids associated with a phenotype. It then submits a query to Phenoscape
     * for each id and adds the results (repeated entries are simply added
     * once).
     *
     */
    public void phenoscapeQuery() {
        //Hiding the result panel and clearing the gene list

        try {
            resetInterface();

            if (phenoscapeInput == null || phenoscapeInput.isEmpty()) {
                throw new WorkflowException("Phenotype Cannot Be Empty");
            }

            //getting all the tao accociated with a phenotype
            //getting the phenotypes query file
            InputStream in = PhenoscapeQuery.class
                    .getResourceAsStream("Phenotypes_Sorted_By_Description.txt");

            Scanner lineScanner = new Scanner(in);

            //Using a HashSet because don't want duplicates and don't care about order
            HashSet<String> phenoscapeResults = new HashSet<>();

            //!!!!!!!!!!!!!!!!!!!!!!!
            int counter = 0;
            List<String> alreadySeen = new ArrayList<>();

            while (lineScanner.hasNextLine()) {
                //getting the second column (as that is the description info)
                Scanner columnScanner = new Scanner(lineScanner.nextLine());
                //Phenotypes.txt is tab delimited
                columnScanner.useDelimiter("\t");

                String tao = columnScanner.next();
                String phenotype = columnScanner.next();

                //Don't need to worry about lower-case here becuase Autocomplete
                //forces the selection of something in the list
                if (phenotype.toLowerCase().contains(phenoscapeInput.trim().toLowerCase()) && !alreadySeen.contains(tao)) {
                    //submitting tao to phenoscape and holding the results
                    List<String> resultsForTao = PhenoscapeQuery.getGenesByTao(tao);

                    PhenotypeIdentifier newPhenoId = new PhenotypeIdentifier();
                    newPhenoId.setId(tao);
                    newPhenoId.setGenesAssociatedWith(resultsForTao);
                    newPhenoId.setPhenotypeDescription(phenotype);

                    if (counter < 4) {
                        if (counter == 0) {
                            newPhenoId.setColor(new GraphColors(GraphColors.GREEN, "Green"));
                        }
                        if (counter == 1) {
                            newPhenoId.setColor(new GraphColors(GraphColors.BLUE, "Blue"));
                        }
                        if (counter == 2) {
                            newPhenoId.setColor(new GraphColors(GraphColors.PINK, "Pink"));
                        }
                        if (counter == 3) {
                            newPhenoId.setColor(new GraphColors(GraphColors.ORANGE, "Orange"));
                        }
                        counter++;
                    }
                    phenoIds.add(newPhenoId);
                    alreadySeen.add(tao);
                    phenoscapeResults.addAll(PhenoscapeQuery.getGenesByTao(tao));
                }
            }

            //adding the phenoScape results to the genes DualList
            Iterator it = phenoscapeResults.iterator();

            while (it.hasNext()) {
                String s = (String) it.next();
                genes.getTarget().add(s);
            }

            //setting the max number of results
            totInteractNum = PsicquicQuery.countAll(genes.getTarget());

            //showing the reporting panel becuase query returned results            
            showReportingPanel = true;

            System.out.println(
                    "Query to phenoscape completed successfully");

            //adding the phenoscape query to report steps
            ReportStep phenoscapeQuery = new ReportStep(PossibleSteps.PHENOSCAPE_QUERY);
            phenoscapeQuery.setFileName("PhenoIds_And_Genes_Tab.txt");
            ReportStepUtilities.addStepToCompleteListAndSelectedList(reportingSteps, selectedReportingSteps, phenoscapeQuery);

            updatePsicquicTable();
            updateDisplay();

        } catch (NoResultQueryException e) {
            //No results for that query, so hidding the panel and telling the user
            showReportingPanel = false;
            FacesContext.getCurrentInstance().addMessage("PhenoscapeMessage", new FacesMessage(FacesMessage.SEVERITY_INFO, "Did Not Find Results", null));
        } catch (QueryException | WorkflowException e) {
            //Was some error, hiding reporting panel
            showReportingPanel = false;
            FacesContext.getCurrentInstance().addMessage("PhenoscapeMessage", new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }
    }

    public List<PhenotypeIdentifier> getSelectedVennPhenoIds() {
        return selectedVennPhenoIds;
    }

    public void setSelectedVennPhenoIds(List<PhenotypeIdentifier> selectedVennPhenoIds) {
        this.selectedVennPhenoIds = selectedVennPhenoIds;
    }

    /**
     * Takes a grninfer inputfile and submits it to iPlant and gets the results
     *
     * @param event
     *
     */
    public void ownReverseEngineeringFileUpload(FileUploadEvent event) {
        //turning the file input stream into a string
        try {
            String dotFileContents;
            try {
                dotFileContents = IOUtils.toString(event.getFile().getInputstream());
            } catch (IOException e) {
                throw new WorkflowException("Could not read .dot file");
            }

            jobIsRunning = false;
            jobHasBeenSubmitted = true;

            HashSet<Node> listOfNodes = new HashSet<>(DOTConverter.convertDotToList(dotFileContents));
            //creating a list of edges
            HashSet<Edge> listOfEdges = new HashSet<>();
            for (Node node : listOfNodes) {
                List<String> neighbors = node.getNodesConnectedTo();
                for (String s : neighbors) {
                    Edge newEdge = new Edge();
                    newEdge.setSource(node.getNodeName());
                    newEdge.setTarget(s);
                    listOfEdges.add(newEdge);
                }
            }
            grninferJson = CreateCytoscapeJSON.convertListToJSON(new ArrayList(listOfNodes),
                    new ArrayList(listOfEdges), new ArrayList<PhenotypeIdentifier>());

            //adding reporting step
            ReportStep userUploadAloneCytoscape = new ReportStep(PossibleSteps.CYTOSCAPE_USER_UPLOADED);
            userUploadAloneCytoscape.setHasImage(true);
            ReportStepUtilities.addStepToCompleteListAndSelectedList(reportingSteps, selectedReportingSteps, userUploadAloneCytoscape);

            //created json necessary for the combined graph
            //checking if grninfer already has the node. If it does, changing color to show overlap
            for (Node psicquicNode : psicquicNodes) {
                //creating new node and adding all the psicquicNode
                //properties so not passing by reference
                Node holderNode = new Node();
                holderNode.setNodeName(psicquicNode.getNodeName());
                holderNode.setNodeColor(GraphColors.GREEN);

                //if the list already contains the node, changing the
                //node in the list to black
                boolean alreadyInList = !listOfNodes.add(holderNode);
                if (alreadyInList) {
                    //removing the node to change the color
                    listOfNodes.remove(holderNode);
                    holderNode.setNodeColor(GraphColors.BLACK);

                    //adding node back in after color change
                    listOfNodes.add(holderNode);
                }
            }

            //seeing if possible to add the edges
            for (Edge psicquicEdge : psicquicEdges) {
                listOfEdges.add(psicquicEdge);
            }
            psicquicAndGrninferCombinedJson = CreateCytoscapeJSON.convertListToJSON(new ArrayList<>(listOfNodes),
                    new ArrayList<>(listOfEdges), new ArrayList<PhenotypeIdentifier>());
            //adding reporting step
            ReportStep userUploadedCombinedCytoscape = new ReportStep(PossibleSteps.CYTOSCAPE_COMBINED_USER_UPLOADED);

            userUploadedCombinedCytoscape.setHasImage(true);
            ReportStepUtilities.addStepToCompleteListAndSelectedList(reportingSteps, selectedReportingSteps, userUploadedCombinedCytoscape);

        } catch (WorkflowException e) {
            FacesContext.getCurrentInstance().addMessage("ownReverseEngineeringMessages",
                    new FacesMessage("Error Uploading file", e.getMessage()));
        }

    }

    public StreamedContent getVennImageStream() {
        InputStream in = null;
        try {
            in = new FileInputStream(vennImageFile);
        } catch (FileNotFoundException e) {
            FacesContext.getCurrentInstance().addMessage("vennSelectionTableMessages",
                    new FacesMessage("Could not generate table", "Please try again."));
            return null;
        }
        return new DefaultStreamedContent(in, "image/png", "Venn.png");
    }

    public void handleGRNInferFileUpload(FileUploadEvent event) {
        File file = new File("grninferInput.txt");
        InputStream input = null;
        OutputStream output = null;
        try {
            input = event.getFile().getInputstream();
            output = new FileOutputStream(file);

            byte[] buffer = new byte[1024]; // Adjust if you want int bytesRead;
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0,
                        bytesRead);
            }
            System.out.println("Created Grninfer input file");
        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage("grninferMessages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Problem submitting script",
                    e.getMessage()));
            return;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    FacesContext.getCurrentInstance().addMessage("grninferMessages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Problem Submitting script",
                            e.getMessage()));
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    FacesContext.getCurrentInstance().addMessage("grninferMessages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Problem with file upload",
                            e.getMessage()));
                }
            }

        }
        try {
            grninferJob = GRNInfer.submitGRNInferJob(file);
            System.out.println("Submitted GRNInfer Job");
            jobHasBeenSubmitted = true;
            jobIsRunning = true;
        } catch (JobSubmissionException e) {
            FacesContext.getCurrentInstance().addMessage(
                    "grninferMessages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Job couldn't be submitted to iPlant",
                            e.getMessage()));
        }

    }

    public StreamedContent getReportStream() {
        return reportStream;
    }

    public void setReportStream(StreamedContent reportStream) {
        this.reportStream = reportStream;
    }

    /**
     * Gets the results of an Ensembl homology query and provides the homology
     * table with data and updates the picklist
     */
    public void generateHomologyTable() {
        try {
            if (sourceSpeciesForHomology.isEmpty() || targetSpeciesForHomology.isEmpty()) {
                throw new WorkflowException("Source and Target Species Cannot Be Empty");
            }
            if (genes.getTarget().isEmpty()) {
                throw new WorkflowException("Must pick genes from above.");
            }

            //conducting homolgy queries if the user selected homology
            //setting the homology Results list
            this.setHomologyResults((ArrayList<HomologyResult>) EnsemblQueries.queryHomologies(sourceSpeciesForHomology,
                    targetSpeciesForHomology, genes.getTarget()));

            //converting the ensemblTargetId to a genesymbol
            for (int i = 0; i < this.getHomologyResults().size(); i++) {
                HomologyResult holder = this.homologyResults.get(i);

                //Quering Ensembl for the gene symbol in various databases
                String targetGeneSymbol = EnsemblQueries.getGeneSymbolFromEnsemblId(holder.getTargetEnsemblId());
                if (targetGeneSymbol == null) {
                    System.out.println("made it here");
                    FacesContext.getCurrentInstance().addMessage("homologuesMessage", new FacesMessage(FacesMessage.SEVERITY_ERROR, "No ENSEBML Result",
                            "Could not find target gene for: " + holder.getSourceGeneSymbol()));
                }
                holder.setTargetGeneSymbol(targetGeneSymbol);
                this.homologyResults.set(i, holder);
            }

            //moving all previous genes to source and all target Genes to target
            List<String> targetGenes = new ArrayList<>();
            for (HomologyResult h : homologyResults) {
                //not adding null target genes to the gene list
                if (h.getTargetGeneSymbol() != null) {
                    //making sure source and target genes are not the same
                    if (!h.getSourceGeneSymbol().equals(h.getTargetGeneSymbol())) {
                        targetGenes.add(h.getTargetGeneSymbol());
                    }
                }
            }

            //adding genes if they aren't already present in the list
            for(String gene:targetGenes){
                if(!genes.getTarget().contains(gene)){
                    genes.getTarget().add(gene);
                }
            }
            
            //telling the user which genes were added
            StringBuilder addedGenes = new StringBuilder();
            for (int i = 0; i < targetGenes.size(); i++) {
                if (i != targetGenes.size() - 1) {
                    addedGenes.append(targetGenes.get(i) + ", ");
                } else {
                    addedGenes.append(targetGenes.get(i));
                }
            }

            FacesContext.getCurrentInstance().addMessage("growlForHomologs",
                    new FacesMessage("Genes added to \"Genes in Use\":", addedGenes.toString()));

        } catch (WorkflowException e) {
            FacesContext.getCurrentInstance().addMessage("homologuesMessage", new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
        }

    }

    public File getVennImageFile() {
        return vennImageFile;
    }

    public void setVennImageFile(File vennImageFile) {
        //if it is not null, deleting the old file
        if (vennImageFile != null) {
            vennImageFile.delete();
        }
        this.vennImageFile = vennImageFile;
        System.out.println(this.vennImageFile.getAbsolutePath());
    }

    public String getSpaceSeparatedGenes() {
        return spaceSeparatedGenes;
    }

    public void setSpaceSeparatedGenes(String spaceSeparatedGenes) {
        this.spaceSeparatedGenes = spaceSeparatedGenes;
    }

    public List<HomologyResult> getHomologyResults() {
        return homologyResults;
    }

    public void setHomologyResults(ArrayList<HomologyResult> homologyResults) {
        this.homologyResults = homologyResults;
    }

    public DualListModel<String> getGenes() {
        return genes;
    }

    public boolean isJobIsRunning() {
        return jobIsRunning;
    }

    public void setJobIsRunning(boolean jobIsRunning) {
        this.jobIsRunning = jobIsRunning;
    }

    public void setGenes(DualListModel<String> genes) {
        this.genes = genes;
    }

    public boolean isJobHasBeenSubmitted() {
        return jobHasBeenSubmitted;
    }

    public void setJobHasBeenSubmitted(boolean jobHasBeenSubmitted) {
        this.jobHasBeenSubmitted = jobHasBeenSubmitted;
    }

    public boolean isShowVenn() {
        return showVenn;
    }

    public void setShowVenn(boolean showVenn) {
        this.showVenn = showVenn;
    }

    public String getNewColor() {
        return newColor;
    }

    public void setNewColor(String newColor) {
        this.newColor = newColor;
    }

    public PhenotypeIdentifier getSelectedIdentifier() {
        return selectedIdentifier;
    }

    public void setSelectedIdentifier(PhenotypeIdentifier selectedIdentifier) {
        this.selectedIdentifier = selectedIdentifier;
    }

    public List<PhenotypeIdentifier> getPhenoIds() {
        return phenoIds;
    }

    public void setPhenoIds(List<PhenotypeIdentifier> phenoIds) {
        this.phenoIds = phenoIds;
    }

    public boolean isIncludeIndirectInteractions() {
        return includeIndirectInteractions;
    }

    public void setIncludeIndirectInteractions(boolean includeIndirectInteractions) {
        this.includeIndirectInteractions = includeIndirectInteractions;
    }

    public String getPsicquicAndGrninferCombinedJson() {
        return psicquicAndGrninferCombinedJson;
    }

    public void setPsicquicAndGrninferCombinedJson(String psicquicAndGrninferCombinedJson) {
        this.psicquicAndGrninferCombinedJson = psicquicAndGrninferCombinedJson;
    }

    public String getPhenoscapeInput() {
        return phenoscapeInput;
    }

    public void setPhenoscapeInput(String phenoscapeInput) {
        this.phenoscapeInput = phenoscapeInput;
    }

    public String getSourceSpeciesForHomology() {
        return sourceSpeciesForHomology;
    }

    public void setSourceSpeciesForHomology(String sourceSpeciesForHomology) {
        this.sourceSpeciesForHomology = sourceSpeciesForHomology;
    }

    public String getTargetSpeciesForHomology() {
        return targetSpeciesForHomology;
    }

    public void setTargetSpeciesForHomology(String targetSpeciesForHomology) {
        this.targetSpeciesForHomology = targetSpeciesForHomology;
    }

    public List<PsicquicResult> getPsicquicResultList() {
        return psicquicResultList;
    }

    public void setPsicquicResultList(List<PsicquicResult> psicquicResultList) {
        this.psicquicResultList = psicquicResultList;
    }

    public List<PsicquicResult> getFilteredPsicquicResults() {
        return filteredPsicquicResults;
    }

    public void setFilteredPsicquicResults(List<PsicquicResult> filteredPsicquicResults) {
        this.filteredPsicquicResults = filteredPsicquicResults;
    }

    public String getGrninferJson() {
        return grninferJson;
    }

    public void setGrninferJson(String grninferJson) {
        this.grninferJson = grninferJson;
    }

    public List<PhenotypeIdentifier> getCompleteVennList() {
        return completeVennList;
    }

    public void setCompleteVennList(List<PhenotypeIdentifier> completeVennList) {
        this.completeVennList = completeVennList;
    }

    public boolean isHomologues() {
        return homologues;
    }

    public void setHomologues(boolean homologues) {
        this.homologues = homologues;
    }

    public PsicquicResult[] getSelectedPsicquicResults() {
        return selectedPsicquicResults;
    }

    public void setSelectedPsicquicResults(PsicquicResult[] selectedPsicquicResults) {
        this.selectedPsicquicResults = selectedPsicquicResults;
    }

    public boolean isShowReportingPanel() {
        return showReportingPanel;
    }

    public void setShowReportingPanel(boolean showReportingPanel) {
        this.showReportingPanel = showReportingPanel;
    }

    public List<ReportStep> getReportingSteps() {
        return reportingSteps;
    }

    public void setReportingSteps(List<ReportStep> reportingSteps) {
        this.reportingSteps = reportingSteps;
    }

    public int getTotInteractNum() {
        return totInteractNum;
    }

    public void setTotInteractNum(int totInteractNum) {
        this.totInteractNum = totInteractNum;
    }

    public String getPsicquicJson() {
        return psicquicJson;
    }

    public List<ReportStep> getSelectedReportingSteps() {
        return selectedReportingSteps;
    }

    public void setSelectedReportingSteps(List<ReportStep> selectedReportingSteps) {
        this.selectedReportingSteps = selectedReportingSteps;
    }

    public void setPsicquicJson(String psicquicJson) {
        this.psicquicJson = psicquicJson;
    }

    public int getSizeOfPaginationList() {
        return paginationList.size();
    }

    public boolean isShowVennImage() {
        return showVennImage;
    }

    public void setShowVennImage(boolean showVennImage) {
        this.showVennImage = showVennImage;
    }
}

class PaginationStep {

    List<URLAndPosition> positionList = new ArrayList<>();

    public static PaginationStep deepCopy(PaginationStep pagStep) {
        List<URLAndPosition> positionList = new ArrayList<>();

        for (URLAndPosition urlAndPos : pagStep.positionList) {
            positionList.add(URLAndPosition.deepCopy(urlAndPos));
        }

        PaginationStep pageStep = new PaginationStep();
        pageStep.positionList = positionList;
        return pageStep;
    }
}

class URLAndPosition {

    String url;
    QueryPosition position;

    public URLAndPosition(String url, QueryPosition position) {
        this.url = url;
        this.position = position;
    }

    public static URLAndPosition deepCopy(URLAndPosition urlAndPos) {
        QueryPosition queryPos = new QueryPosition();
        queryPos.setLineNum(urlAndPos.position.getLineNum());
        queryPos.setQueryNum(urlAndPos.position.getQueryNum());

        return new URLAndPosition(urlAndPos.url, queryPos);
    }
}
