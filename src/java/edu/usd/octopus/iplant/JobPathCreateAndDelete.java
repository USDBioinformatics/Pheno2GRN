/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.usd.octopus.iplant;

import edu.usd.octopus.exceptions.JobSubmissionException;
import java.util.List;
import org.iplant.foundation_api.io.IO_Impl;
import org.iplant.foundation_api.io.InvalidIOException;
import org.iplant.foundation_api.utility.FileSpecifications;

/**
 *
 * @author nick.weinandt
 */
public class JobPathCreateAndDelete {

    /**
     * Creates a folder in irods which resides in the folder name passed to this method
     * 
     * @param parentFolder  Name of the folder wanting to upload to (ex: "output" and "tempFiles")
     * 
     * @return  The exact path of the new folder (ex: "/usdbioinformatics/output/job1")
     * @throws JobSubmissionException If error during the upload process
     */
    public static String createPath(String parentFolder) throws JobSubmissionException {
        IO_Impl fileUp = new IO_Impl();

        //getting the list of paths in the output folder to make certain a NEW path is made
        List<FileSpecifications> fileList;
        try {
            //getting the subFolder list for the parentFolder
            fileList = fileUp.listDirectory(IplantCredentials.USERNAME, IplantCredentials.PASSWORD, parentFolder);            
        } catch (InvalidIOException e) {
            throw new JobSubmissionException("Could not find list of parentFolder subfolders");
        }
        
        //getting a path that doesn't currently exist
        String filePath = "job";
        
        for (int i = 1; i < fileList.size()+3; i++) {
            boolean found = false;
            for (FileSpecifications dirs : fileList) {
                //if the number of digits on the end of the path is the same as i, choose different i
                String name = dirs.getName();
                if (name.length() > 3 && name.substring(3).equals("" + i)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                filePath = filePath + i;
                break;
            }
        }

        //trying to upload the archive folder to irods
        try {
            fileUp.createDirectory(IplantCredentials.USERNAME, IplantCredentials.PASSWORD, filePath, parentFolder + "/");
        } catch (InvalidIOException e) {
            throw new JobSubmissionException("Wasn't able to upload new folder");
        }

        //returning the path of the folder 
        return "/" + IplantCredentials.USERNAME + "/" + parentFolder + "/" + filePath + "/";
    }

    /**
     * Deletes the path of temporary directory
     * @param path  Must be exact path (ex: "/usdbioinformatis/output/tempFiles/Job1")
     * @throws JobSubmissionException If error during deletion process
     */
    public static void deletePath(String path) throws JobSubmissionException {
        IO_Impl deletePath = new IO_Impl();
        try {
            //deleting the archive path
            deletePath.delete(IplantCredentials.USERNAME, IplantCredentials.PASSWORD, path);
        } catch (InvalidIOException e) {
            throw new JobSubmissionException("Could not delete the following path: " + path);
        }
    }
}
