package edu.usd.pheno2grn.iplant;

import edu.usd.pheno2grn.exceptions.IPlantException;
import java.io.File;
import org.iplant.foundation_api.io.IO_Impl;

/**
 * Contains iPlant functionality for downloading files.
 */
public class IplantFunctionality {
    
    /**
     * Downloads a output of grninfer from iPlant.
     * @param path Path of the file in iplant
     * @return
     * @throws IPlantException 
     */
    public static File downloadGRNInferDotFile(String path)throws IPlantException{
        //downloading the .dot file so it can be visualized
        File returnFile = new File("GRNinferOutput.dot");
        try {
            returnFile.createNewFile();
            IO_Impl iplantIO=new IO_Impl();
            //downloading the file
            iplantIO.downloadFile(IplantCredentials.USERNAME, IplantCredentials.PASSWORD,
                   path , returnFile.getName());
                 
        return returnFile;
        } catch (Exception e) {
            throw new IPlantException("Could not retrieve output from iPlant");
        }   
    }
}
