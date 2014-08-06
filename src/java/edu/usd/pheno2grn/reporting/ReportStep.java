/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.usd.pheno2grn.reporting;

import java.io.File;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Nick.Weinandt
 */
public class ReportStep {

    private String stepName;
    private boolean hasImage;
    private boolean hasData;
    private boolean hasFiles;
    private String editableLabel;
    private UploadedFile imageFile;
    private String fileName;

    public boolean isHasFiles() {
        return hasFiles;
    }

    public void setHasFiles(boolean hasFiles) {
        this.hasFiles = hasFiles;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        this.hasFiles=true;
    }

    public UploadedFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(UploadedFile imageFile) {
        System.out.println("Setting image file");
        this.imageFile = imageFile;
    }

    public String getEditableLabel() {
        return editableLabel;
    }

    public void setEditableLabel(String editableLabel) {
        this.editableLabel = editableLabel;
    }

    public ReportStep(String stepName) {
        this.stepName = stepName;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ReportStep)) {
            return false;
        }
        if (obj == null) {
            return false;
        }
        return this.stepName.equals(((ReportStep) obj).stepName);
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public boolean isHasData() {
        return hasData;
    }

    public void setHasData(boolean hasData) {
        this.hasData = hasData;
    }

}
