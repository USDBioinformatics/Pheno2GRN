/*
 * Exception which is thrown if creating output directories, creating input directories,
 * or job submission fails
 */
package edu.usd.octopus.exceptions;

/**
 *
 * @author nick.weinandt
 */
public class JobSubmissionException extends Exception{
    public JobSubmissionException(String msg) {
        super(msg);
    }
}
