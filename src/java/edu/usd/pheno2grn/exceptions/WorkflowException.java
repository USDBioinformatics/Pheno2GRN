/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.usd.pheno2grn.exceptions;

/**
 *
 * @author Nick.Weinandt
 */
public class WorkflowException extends Exception{
    public WorkflowException(String msg){
        super(msg);
    }
}
