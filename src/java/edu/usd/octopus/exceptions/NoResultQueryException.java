/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.usd.octopus.exceptions;

/**
 *
 * @author Nick.Weinandt
 */
public class NoResultQueryException extends Exception{
    public NoResultQueryException(String msg){
        super(msg);
    }
    public NoResultQueryException(){
    }
}
