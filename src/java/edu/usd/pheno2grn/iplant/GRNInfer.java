/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.usd.pheno2grn.iplant;

import edu.usd.pheno2grn.exceptions.JobSubmissionException;
import java.io.File;
import org.iplant.foundation_api.io.IO_Impl;
import org.iplant.foundation_api.io.InvalidIOException;
import org.iplant.foundation_api.job.InvalidJobException;
import org.iplant.foundation_api.job.Job;
import org.iplant.foundation_api.job.JobManagerImpl;
import org.iplant.foundation_api.job.JobParameter;

/**
 *
 * @author Nick.Weinandt
 */
public class GRNInfer {
    
    public static Job submitGRNInferJob(File grnInferInput)throws JobSubmissionException{
        //creating the tempFiles folder and the output folder
      
        String tempFilesPath = JobPathCreateAndDelete.createPath("tempFiles");
        String outputFilesPath = JobPathCreateAndDelete.createPath("output");

        //uploading the input File to iPlant
        IO_Impl iplantIO = new IO_Impl();
        try {
            iplantIO.uploadFile(IplantCredentials.USERNAME, IplantCredentials.PASSWORD, grnInferInput.getName(), "TEXT-0", grnInferInput.getName(), tempFilesPath.substring(19));
        } catch (InvalidIOException e) {
            throw new JobSubmissionException("Iplant is down.");
        }

        //creating the job 
        Job job = new Job();
        job.setApplication("grninfer-1756");
        job.setJobName("GRNInfer from Octopus");
        job.setArchiveOutput(true);
        job.setArchivePath(outputFilesPath);
        job.setRequestedTime(60);
        JobParameter parm = new JobParameter();
        parm.setKey("__infile");
        parm.setValue(tempFilesPath + grnInferInput.getName());
        try {
            job.addParameter(parm);

            //Running the job
            JobManagerImpl jobManager = new JobManagerImpl();
            return jobManager.runJob(IplantCredentials.USERNAME, IplantCredentials.PASSWORD, job);

        } catch(InvalidJobException e){
            throw new JobSubmissionException("Could not submit to iPlant");
        }
    }
    
}
