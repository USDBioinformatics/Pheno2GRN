/*
 * This class is used to represent a file stored in irods which can be downloaded.
 */
package edu.usd.octopus.iplant;

import java.text.DecimalFormat;

/**
 *
 * @author Nick.Weinandt
 */
public class DownloadFile {

    String fileName;
    String fileSize;
    boolean ableToDownload = false;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    /**
     * Sets file size by calculating the size of the file and appending B, KB,
     * MB, or GB to the end. The size will be to two decimal places.
     *
     * @param fileSize Size of the file in bytes
     */
    public void setFileSize(Long fileSize) {
        //Seeing if file is less than 1,000 bytes
        if (fileSize < 1000) {
            this.fileSize = String.valueOf(fileSize) + " B";
            return;
        }

        //converting file size to double so division works properly
        double fileSizeDouble = (double) fileSize;

        //dividing bytes by 1024 to get to KB
        fileSizeDouble = fileSizeDouble / 1024;
        DecimalFormat dec = new DecimalFormat("#.##");

        //Checking if file size is less than 1000 KB
        if (fileSizeDouble < 1000) {
            this.fileSize = dec.format(fileSizeDouble) + " KB";
            return;
        }

        fileSizeDouble = fileSizeDouble / 1024;
        //Checking if file size is less than 1000 MB
        if (fileSizeDouble < 1000) {
            this.fileSize = dec.format(fileSizeDouble) + " MB";
            return;
        }

        fileSizeDouble = fileSizeDouble / 1024;
        //Setting file size in GB
        this.fileSize = dec.format(fileSizeDouble) + " GB";

    }

    public boolean isAbleToDownload() {
        return ableToDownload;
    }

    public void setAbleToDownload(boolean ableToDownload) {
        this.ableToDownload = ableToDownload;
    }
}
