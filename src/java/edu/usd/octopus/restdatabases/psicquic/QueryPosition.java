/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.usd.octopus.restdatabases.psicquic;

/**
 *
 * @author Nick.Weinandt
 */
public class QueryPosition {

    private int queryNum = 0;
    private int lineNum = 0;

    public void revertOneLine(){
        if(lineNum>0){
            lineNum--;
        }else if(queryNum>0){
            queryNum=queryNum-PsicquicQuery.MAX_RESULTS_FROM_QUERY;
            lineNum=PsicquicQuery.MAX_RESULTS_FROM_QUERY-1;
        }
    }
    
    public QueryPosition() {
    }

    public QueryPosition(QueryPosition pos) {
        this.queryNum = pos.queryNum;
        this.lineNum = pos.lineNum;
    }

    @Override
    public String toString() {
        return "   QueryNum: " + queryNum + "  LineNum: " + lineNum;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + this.queryNum;
        hash = 79 * hash + this.lineNum;
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
        final QueryPosition other = (QueryPosition) obj;
      
        if (this.queryNum != other.queryNum) {
            return false;
        }
        if (this.lineNum != other.lineNum) {
            return false;
        }
        return true;
    }

    public int getQueryNum() {
        return queryNum;
    }

    public void setQueryNum(int queryNum) {
        this.queryNum = queryNum;
    }

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

}
