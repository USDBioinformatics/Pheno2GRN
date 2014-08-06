/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.usd.pheno2grn.restdatabases.psicquic;

import java.util.List;

/**
 *
 * @author Nick.Weinandt
 */
public class PsicquicUtilities {
    public static String generateTabDelimitedResults(List<PsicquicResult>psicquicResults){
        StringBuilder sb=new StringBuilder();   
        
        for(PsicquicResult result:psicquicResults){
            sb.append(result.getInteractorAID()+"\t");
            sb.append(result.getInteractorBID()+"\t");
            sb.append(result.getGeneNameA()+"\t");
            sb.append(result.getGeneNameB()+"\n");
        }
        
        return sb.toString();
    }
}
