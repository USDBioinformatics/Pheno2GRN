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
public class QueryParsingException extends Exception{
    public QueryParsingException(String msg){
        super(msg);
    }
}
