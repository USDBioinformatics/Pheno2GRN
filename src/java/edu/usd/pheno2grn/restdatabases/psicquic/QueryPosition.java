package edu.usd.pheno2grn.restdatabases.psicquic;

/**
 * Class to represent a current position in a database result list.
 */
public class QueryPosition {

    /**
     * queryNum represents the position in the queries. It it is always a
     * multiple of PsicquicQuery.MAX_RESULTS_FROM_QUERY and appears in the query
     * as firstResult=queryNum. The lineNum dictates the line in the queryNum.
     * For example, if queryNum=300 and lineNum=3, the current line in a
     * database's results is 303.
     */
    private int queryNum = 0;
    private int lineNum = 0;

    /**
     * Reverts one database result in the query. If first line in the first
     * query, simply returning because can't go backwards.
     */
    public void revertOneLine() {
        //if the first line and query, can't go backwards, so simply returning without changing anything
        if (lineNum == 0 && queryNum == 0) {
            return;
        }
        if (lineNum > 0) {
            lineNum--;
        } else if (queryNum > 0) {
            queryNum = queryNum - PsicquicQuery.MAX_RESULTS_FROM_QUERY;
            lineNum = PsicquicQuery.MAX_RESULTS_FROM_QUERY - 1;
        }
    }

    public QueryPosition() {
    }

    /**
     * Copy constructor.
     * @param pos 
     */
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
