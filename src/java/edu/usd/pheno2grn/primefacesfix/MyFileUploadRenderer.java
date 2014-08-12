package edu.usd.pheno2grn.primefacesfix;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.primefaces.component.fileupload.FileUploadRenderer;

/**
 * Fixes a primefaces bug associated with uploading files.
 */
public class MyFileUploadRenderer extends FileUploadRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        if (context.getExternalContext().getRequestContentType().toLowerCase().startsWith("multipart/")) {
            super.decode(context, component);
        }
    }
}
