/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.usd.pheno2grn.primefacesfix;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.primefaces.component.fileupload.FileUploadRenderer;

/**
 *
 * @author Nick.Weinandt
 */
public class MyFileUploadRenderer extends FileUploadRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        if (context.getExternalContext().getRequestContentType().toLowerCase().startsWith("multipart/")) {
            super.decode(context, component);
        }
    }
}
