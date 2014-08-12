package edu.usd.pheno2grn.reporting.dox4jUtilities;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

/**
 * Contains word document styling functionality.
 */
public class StylingUtilities {

    /**
     * Adds header3 text to a word document package.
     *
     * @param wordPackage Document which the header will be added to.
     * 
     * @param headerText Text to be in the header.
     */
    public static void addHeader3(WordprocessingMLPackage wordPackage, String headerText) {
        //adding the summary heading 
        wordPackage.getMainDocumentPart().addStyledParagraphOfText("Heading3", headerText);
    }
}
