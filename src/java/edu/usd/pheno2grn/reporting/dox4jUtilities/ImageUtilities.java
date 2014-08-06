/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.usd.pheno2grn.reporting.dox4jUtilities;

import edu.usd.pheno2grn.exceptions.Docx4jException;
import edu.usd.pheno2grn.exceptions.ReportCreationException;
import java.io.IOException;
import java.io.InputStream;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;

/**
 *
 * @author Nick.Weinandt
 */
public class ImageUtilities {

    public static org.docx4j.wml.P newImage(WordprocessingMLPackage wordMLPackage,
            byte[] bytes, String filenameHint, String altText,
            int id1, int id2, long cx) throws Docx4jException {
        try {
            BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);

            Inline inline = imagePart.createImageInline(filenameHint, altText,
                    id1, id2, cx, false);

            // Now add the inline in w:p/w:r/w:drawing
            org.docx4j.wml.ObjectFactory factory = Context.getWmlObjectFactory();
            org.docx4j.wml.P p = factory.createP();
            org.docx4j.wml.R run = factory.createR();
            p.getContent().add(run);
            org.docx4j.wml.Drawing drawing = factory.createDrawing();
            run.getContent().add(drawing);
            drawing.getAnchorOrInline().add(inline);

            return p;
        } catch (Exception e) {
            System.out.println("_________________________________");
            e.printStackTrace();
            System.out.println("_________________________________");
            throw new Docx4jException("Could not add image to document");
        }
    }
    
    public static void addImage(WordprocessingMLPackage wordMLPackage, InputStream imageStream, long fileSize)
            throws ReportCreationException {

        // You cannot create an array using a long type.
        // It needs to be an int type.
        if (fileSize > Integer.MAX_VALUE) {
            throw new ReportCreationException("One of the images is too big for report.");
        }
        byte[] bytes = new byte[(int) fileSize];
        int offset = 0;
        int numRead = 0;
        try {
            while (offset < bytes.length && (numRead = imageStream.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
        } catch (IOException e) {
            throw new ReportCreationException("Could not read from image file.");
        }
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new ReportCreationException("Could not read entire image file");
        }
        
        String filenameHint = null;
        String altText = null;
        int id1 = 0;
        int id2 = 1;
        org.docx4j.wml.P p2;
        try {
            p2 = newImage(wordMLPackage, bytes,
                    filenameHint, altText,
                    id1, id2, 5000);
        } catch (Docx4jException e) {
            throw new ReportCreationException("Error adding image to document");
        }
        wordMLPackage.getMainDocumentPart().addObject(p2);
    }
}
