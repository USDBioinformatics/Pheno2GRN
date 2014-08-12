package edu.usd.pheno2grn.exceptions;

public class NoResultQueryException extends Exception{
    public NoResultQueryException(String msg){
        super(msg);
    }
    public NoResultQueryException(){
    }
}
