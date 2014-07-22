/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.usd.octopus.restdatabases.psicquic;

import java.util.Objects;

/**
 *
 * @author nick.weinandt
 */
public class PsicquicResult {

    private String interactorAID;
    private String interactorBID;
    private String geneNameA;
    private String geneNameB;

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.geneNameA);
        hash = 97 * hash + Objects.hashCode(this.geneNameB);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PsicquicResult other = (PsicquicResult) obj;
        if (!Objects.equals(this.geneNameA, other.geneNameA)) {
            return false;
        }
        if (!Objects.equals(this.geneNameB, other.geneNameB)) {
            return false;
        }
        return true;
    }

    public String getInteractorAID() {
        return interactorAID;
    }

    public void setInteractorAID(String interactorAID) {
        this.interactorAID = interactorAID;
    }

    public String getInteractorBID() {
        return interactorBID;
    }

    public void setInteractorBID(String interactorBID) {
        this.interactorBID = interactorBID;
    }

    public String getGeneNameA() {
        return geneNameA;
    }

    public void setGeneNameA(String geneNameA) {
        this.geneNameA = geneNameA;
    }

    public String getGeneNameB() {
        return geneNameB;
    }

    public void setGeneNameB(String geneNameB) {
        this.geneNameB = geneNameB;
    }

}
