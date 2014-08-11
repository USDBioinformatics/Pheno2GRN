package edu.usd.pheno2grn.reporting.dox4jUtilities;

import edu.usd.pheno2grn.exceptions.ReportCreationException;
import edu.usd.pheno2grn.reporting.PossibleSteps;
import edu.usd.pheno2grn.reporting.ReportStep;
import edu.usd.pheno2grn.restdatabases.phenoscape.PhenotypeIdentifier;
import edu.usd.pheno2grn.restdatabases.psicquic.PsicquicResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.primefaces.model.UploadedFile;

/**
 * Creates a word document of the report.
 */
public class ReportCreation {

    private static final ObjectFactory factory = Context.getWmlObjectFactory();
    final static Logger LOG = Logger.getLogger(ReportCreation.class.getName());

    /**
     * Creates the report of the steps completed in the workflow.
     * 
     * @param reportingSteps
     * @param phenoIds
     * @param phenoscapeQueryString
     * @param psicquicResults
     * @param vennImageFile
     * @return
     * @throws ReportCreationException 
     */
    public static WordprocessingMLPackage createReport(List<ReportStep> reportingSteps, List<PhenotypeIdentifier> phenoIds,
            String phenoscapeQueryString, List<PsicquicResult> psicquicResults, File vennImageFile)
            throws ReportCreationException {
        WordprocessingMLPackage wordMLPackage;
        try {
            wordMLPackage = WordprocessingMLPackage.createPackage();
        } catch (InvalidFormatException e) {
            throw new ReportCreationException("Could not create document.");
        }
        //adding header
        createTopHeader(wordMLPackage);

        //adding summary
        addSummary(wordMLPackage, phenoscapeQueryString);

        //adding methods
        addMethodsSection(wordMLPackage, reportingSteps);

        for (ReportStep step : reportingSteps) {
            //adding the step name as a header to the report
            StylingUtilities.addHeader3(wordMLPackage, step.getStepName());

            //Adding the user supplied label to the report
            if (step.getEditableLabel() != null && !step.getEditableLabel().isEmpty()) {
                wordMLPackage.getMainDocumentPart().addParagraphOfText(step.getEditableLabel());
            }

            //adding an image to the report if a user uploaded one
            UploadedFile upFile = step.getImageFile();
            if (upFile != null) {
                InputStream in = null;
                try {
                    in = upFile.getInputstream();
                    ImageUtilities.addImage(wordMLPackage, in, upFile.getSize());
                } catch (IOException e) {
                    LOG.finest("Couldn't pass the input stream to image creation method");
                } finally {
                    IOUtils.closeQuietly(in);
                }
            }

            //if the step is the phenoscape query, addding the data table
            if (step.getStepName().equals(PossibleSteps.PHENOSCAPE_QUERY)) {
                addPhenoscapeTable(wordMLPackage, phenoIds);
            }

            //if the step is the PSICQUIC query, adding the psicquic table
            if (step.getStepName().equals(PossibleSteps.PSICQUIC_QUERY)) {
                addPSICQUICTable(wordMLPackage, psicquicResults);
            }

            //if step is the Venn diagram, adding the image file
            if (step.getStepName().equals(PossibleSteps.VENN_DIAGRAM)) {
                try {
                    InputStream in = new FileInputStream(vennImageFile);
                    ImageUtilities.addImage(wordMLPackage, in, vennImageFile.length());

                    IOUtils.closeQuietly(in);

                } catch (FileNotFoundException e) {
                    //doing nothing if can't add the VennImage file
                }
            }
        }

        //adding the references
        StylingUtilities.addHeader3(wordMLPackage, "References");

        StringBuilder referenceString = new StringBuilder();
        referenceString.append("[" + 1 + "] " + "Mesirov, J. P. (2010). Computer science. Accessible reproducible research. Science (New York, N.Y.), 327(5964), 415–6. doi:10.1126/science.1179653\n\n");

        int referenceCount = 2;
        for (int i = 0; i < reportingSteps.size(); i++) {
            //adding the references (if step has any)
            List<String> references = ReportConstants.mapStepToReferences(reportingSteps.get(i));
            if (!references.isEmpty()) {
                for (String reference : references) {
                    referenceString.append("[" + referenceCount++ + "] " + reference + "\n\n");
                }
            }

        }

        //converting string to paragraph and adding to document
        P paragraph = Alignment.addLineBreaks(referenceString.toString());
        wordMLPackage.getMainDocumentPart().addObject(paragraph);

        return wordMLPackage;
    }

    private static void addSummary(WordprocessingMLPackage wordPackage, String phenoscapeQueryString) {
        //adding the summary heading 
        StylingUtilities.addHeader3(wordPackage, "Summary");

        //seeing if adding phenotype or user inputed genes
        String phenotype;
        if (phenoscapeQueryString == null || phenoscapeQueryString.isEmpty()) {
            phenotype = "user inputted genes";
        } else {
            phenotype = phenoscapeQueryString + " phenotype";
        }

        //Adding summary to document
        String summaryContents = ReportConstants.firstSummaryText + phenotype + ReportConstants.secondSummaryText;
        wordPackage.getMainDocumentPart().addParagraphOfText(summaryContents);
    }

    private static void createTopHeader(WordprocessingMLPackage wordMLPackage) {
        P paragraph = Alignment.addLineBreaks(ReportConstants.reportHeader);
        Alignment.centerParagraph(paragraph);
        wordMLPackage.getMainDocumentPart().addObject(paragraph);
    }

    private static void addMethodsSection(WordprocessingMLPackage wordPackage, List<ReportStep> reportingSteps)
            throws ReportCreationException {
        //adding the summary heading 
        StylingUtilities.addHeader3(wordPackage, "Methods");

        //adding the paragraph of text describing the methods
        wordPackage.getMainDocumentPart().addParagraphOfText(ReportConstants.methodsContents);

        //getting the size of the image to be added to the workflow
        InputStream sizeIn = ReportCreation.class.getResourceAsStream("Pheno2GRNWorkflow.png");

        long counter = 0;
        try {
            while (sizeIn.read() != -1) {
                counter++;
            }
        } catch (IOException e) {
            LOG.finest("Could not read the Pheno2GRNworkflow.png");
        } finally {
            IOUtils.closeQuietly(sizeIn);
        }

        //Getting a new image input stream and passing the file size in
        InputStream pictureStream = ReportCreation.class.getResourceAsStream("Pheno2GRNWorkflow.png");
        try {
            //adding the image to the document
            ImageUtilities.addImage(wordPackage, pictureStream, counter);

            //closing the input stream
        } finally {
            IOUtils.closeQuietly(pictureStream);
        }

        //adding the the step description list
        StringBuilder sb = new StringBuilder();
        int referenceNumberCounter = 2;
        for (int i = 0; i < reportingSteps.size(); i++) {
            //adding the step number
            sb.append(i + 1 + ". ");

            //adding the description
            sb.append(ReportConstants.mapStepToDescription(reportingSteps.get(i)));

            //adding the references (if step has any)
            List<String> references = ReportConstants.mapStepToReferences(reportingSteps.get(i));
            if (!references.isEmpty()) {
                for (String reference : references) {
                    sb.append("[" + referenceNumberCounter + "]");
                    referenceNumberCounter++;
                }
            }

            //as long as not last reporting step, adding a newline character
            if (i != reportingSteps.size() - 1) {
                sb.append("\n");
            }
        }

        //converting string to paragraph and adding to document
        P paragraph = Alignment.addLineBreaks(sb.toString());
        wordPackage.getMainDocumentPart().addObject(paragraph);
    }

    private static void addPhenoscapeTable(WordprocessingMLPackage wordMLPackage, List<PhenotypeIdentifier> phenoIds) {
        //creating column header list
        List<String> headers = new ArrayList<>();
        headers.add("Phenotype ID");
        headers.add("Description");
        headers.add("Gene List");

        Tr headerRow = TableUtilities.createTableRowFromList(headers, wordMLPackage);

        List<Tr> rowList = new ArrayList<>();
        rowList.add(headerRow);

        //adding the phenotype identifier data
        for (PhenotypeIdentifier phenoId : phenoIds) {
            List<String> rowContents = new ArrayList<String>();
            rowContents.add(phenoId.getId());
            rowContents.add(phenoId.getPhenotypeDescription());

            //concatenating gene list together with comma seperation
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < phenoId.getGenesAssociatedWith().size(); i++) {
                //not adding comma for the last one
                if (i != phenoId.getGenesAssociatedWith().size() - 1) {
                    sb.append(phenoId.getGenesAssociatedWith().get(i) + ", ");
                } else {
                    sb.append(phenoId.getGenesAssociatedWith().get(i));
                }
            }

            rowContents.add(sb.toString());

            Tr row = TableUtilities.createTableRowFromList(rowContents, wordMLPackage);
            rowList.add(row);
        }

        Tbl table = TableUtilities.createTableFromRowList(rowList);
        wordMLPackage.getMainDocumentPart().addObject(table);

    }

    private static void addPSICQUICTable(WordprocessingMLPackage wordMLPackage, List<PsicquicResult> psicquicResults) {
        //creating column header list
        List<String> headers = new ArrayList<>();
        headers.add("Interactor A ID");
        headers.add("Interactor B ID");
        headers.add("Alias A");
        headers.add("Alias B");

        Tr headerRow = TableUtilities.createTableRowFromList(headers, wordMLPackage);

        List<Tr> rowList = new ArrayList<>();
        rowList.add(headerRow);

        //adding the phenotype identifier data
        for (PsicquicResult psicquicResult : psicquicResults) {
            List<String> rowContents = new ArrayList<String>();
            rowContents.add(psicquicResult.getInteractorAID());
            rowContents.add(psicquicResult.getInteractorBID());
            rowContents.add(psicquicResult.getGeneNameA());
            rowContents.add(psicquicResult.getGeneNameB());

            Tr row = TableUtilities.createTableRowFromList(rowContents, wordMLPackage);
            rowList.add(row);
        }

        Tbl table = TableUtilities.createTableFromRowList(rowList);
        wordMLPackage.getMainDocumentPart().addObject(table);

    }

}
