package edu.usd.pheno2grn.iplant;

import edu.usd.pheno2grn.exceptions.IPlantException;
import org.iplant.foundation_api.job.InvalidJobException;
import org.iplant.foundation_api.job.Job;
import org.iplant.foundation_api.job.JobManagerImpl;
import org.iplant.foundation_api.utility.Status;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Functionality to tell if jobs are finished/running.
 */
public class JobFunctionality {

    /**
     * Gets the status of job on iPlant.
     *
     * @param job Job to find on iPlant and get the status of.
     * @return String of the status of the job.
     * @throws IPlantException Thrown if issue finding information about iPlant
     * or if iPlant is down.
     */
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

    /**
     * Checks if a job is running.
     *
     * @param job Job to check.
     * @return True if job is still running or false if failed or completed.
     * @throws IPlantException Thrown if error on iPlant.
     */
    public static boolean isJobStillRunning(Job job) throws IPlantException {
        String status = checkJobStatus(job);

        return status.equals("PENDING") || status.equals("STAGING_INPUTS") || status.equals("CLEANING_UP")
                || status.equals("ARCHIVING") || status.equals("STAGING_JOB") || status.equals("RUNNING")
                || status.equals("QUEUED") || status.equals("SUBMITTING") || status.equals("STAGED")
                || status.equals("PROCESSING_INPUTS");
    }

    /**
     * Checks if job failed.
     * 
     * @param job Job for which the status will be checked.
     * 
     * @return  True of job failed, false otherwise.
     * @throws IPlantException 
     */
    public static boolean didJobFail(Job job) throws IPlantException {
        String status = checkJobStatus(job);

        return status.equals("KILLED") || status.equals("FAILED") || status.equals("STOPPED")
                || status.equals("ARCHIVING_FAILED") || status.equals("PAUSED");
    }
}
