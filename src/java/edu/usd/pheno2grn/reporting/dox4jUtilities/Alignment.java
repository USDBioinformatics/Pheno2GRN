package edu.usd.pheno2grn.reporting.dox4jUtilities;

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
 * Contains functionality for centering paragraphs and adding line breaks in a
 * paragraph.
 */
public class Alignment {

    private static final ObjectFactory factory = Context.getWmlObjectFactory();

    /**
     * Centers a paragraph.
     *
     * @param paragraph Paragraph to be centered.
     */
    public static void centerParagraph(P paragraph) {
        PPr paragraphProperties = factory.createPPr();

        //creating the alignment
        Jc justification = factory.createJc();
        justification.setVal(JcEnumeration.CENTER);
        paragraphProperties.setJc(justification);

        //centering the paragraph
        paragraph.setPPr(paragraphProperties);
    }

    /**
     * Adds line breaks in between each line (excluding last line).
     *
     * @param s String to be used to create a line break paragraph.
     * @return A paragraph containing line breaks instead of newline characters.
     */
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
