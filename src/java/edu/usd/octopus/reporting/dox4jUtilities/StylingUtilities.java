/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.usd.octopus.reporting.dox4jUtilities;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

/**
 *
 * @author Nick.Weinandt
 */
public class StylingUtilities {
    public static void addHeader3(WordprocessingMLPackage wordPackage,String headerText){
        //adding the summary heading 
        wordPackage.getMainDocumentPart().addStyledParagraphOfText("Heading3", headerText);
    }
}
