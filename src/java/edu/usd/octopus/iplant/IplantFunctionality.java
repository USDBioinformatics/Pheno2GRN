/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.usd.octopus.iplant;

import edu.usd.octopus.exceptions.IPlantException;
import java.io.File;
import org.iplant.foundation_api.io.IO_Impl;

/**
 *
 * @author Nick.Weinandt
 */
public class IplantFunctionality {
    public static File downloadFile(String path)throws IPlantException{
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
