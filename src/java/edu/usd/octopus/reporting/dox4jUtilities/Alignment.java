/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.usd.octopus.reporting.dox4jUtilities;

import java.util.Scanner;
import org.docx4j.jaxb.Context;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.R;
import org.docx4j.wml.Text;

/**
 *
 * @author Nick.Weinandt
 */
public class Alignment {
    private static final ObjectFactory factory = Context.getWmlObjectFactory();
    public  static void centerParagraph(P paragraph) {
        PPr paragraphProperties = factory.createPPr();

        //creating the alignment
        Jc justification = factory.createJc();
        justification.setVal(JcEnumeration.CENTER);
        paragraphProperties.setJc(justification);

        //centering the paragraph
        paragraph.setPPr(paragraphProperties);
    }
    
    public static P addLineBreaks(String s) {
        
        R run = factory.createR();
        
        Scanner lineScanner = new Scanner(s);
        while (lineScanner.hasNextLine()) {
            Text t = factory.createText();
            t.setValue(lineScanner.nextLine());
            run.getContent().add(t);

            //seeing if the last line (if it is, adding not adding break)
            if (lineScanner.hasNextLine()) {
                run.getContent().add(factory.createBr());
            }
        }

        //converting to paragraph
        P returnParagraph = factory.createP();
        returnParagraph.getContent().add(run);
        return returnParagraph;
    }
}
