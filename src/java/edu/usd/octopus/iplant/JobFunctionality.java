/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.usd.octopus.iplant;

import edu.usd.octopus.exceptions.IPlantException;
import org.iplant.foundation_api.job.InvalidJobException;
import org.iplant.foundation_api.job.Job;
import org.iplant.foundation_api.job.JobManagerImpl;
import org.iplant.foundation_api.utility.Status;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author nick.weinandt
 */
public class JobFunctionality {

    private static String checkJobStatus(Job job) throws IPlantException {
        JobManagerImpl jobManager = new JobManagerImpl();
        Status status;
        try {
            status = jobManager.getJobStatus(IplantCredentials.USERNAME, IplantCredentials.PASSWORD, job.getId());
        } catch (InvalidJobException e) {
            throw new IPlantException("Could not retrieve job on iPlant");
        }
        try {
            JSONObject jobResponse = new JSONObject(status.getMessage());
            return jobResponse.getString("status");

        } catch (JSONException e) {
            throw new IPlantException("Could not parse response from iPlant");
        }
    }

    public static boolean isJobStillRunning(Job job) throws IPlantException {
        String status = checkJobStatus(job);
        
        
        return status.equals("PENDING")||status.equals("STAGING_INPUTS")||status.equals("CLEANING_UP")
                ||status.equals("ARCHIVING")||status.equals("STAGING_JOB")||status.equals("RUNNING")
                ||status.equals("QUEUED")||status.equals("SUBMITTING")||status.equals("STAGED")
                ||status.equals("PROCESSING_INPUTS");
    }
    
    public static boolean didJobFail(Job job)throws IPlantException{
        String status=checkJobStatus(job);
        
        return status.equals("KILLED")||status.equals("FAILED")||status.equals("STOPPED")
                ||status.equals("ARCHIVING_FAILED")||status.equals("PAUSED");
    }
}
