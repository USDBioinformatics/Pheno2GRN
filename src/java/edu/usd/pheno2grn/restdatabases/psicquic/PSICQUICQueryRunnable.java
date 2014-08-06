package edu.usd.pheno2grn.restdatabases.psicquic;

import edu.usd.pheno2grn.exceptions.QueryException;
import java.util.List;

/**
 * Runnable class which queries one PSICQUIC database at a time.
 */
public class PSICQUICQueryRunnable implements Runnable {

    private String databaseURL;
    private PsicquicSynchronizedResultSet resultSet;
    private QueryPosition queryPosition;
    private List<String> geneQueryList;
    boolean onlyDirectLinks;

    public PSICQUICQueryRunnable(String databaseURL, PsicquicSynchronizedResultSet resultSet,
            QueryPosition queryPosition, List<String> geneQueryList, boolean onlyDirectLinks) {
        this.databaseURL = databaseURL;
        this.resultSet = resultSet;
        this.queryPosition = queryPosition;
        this.geneQueryList = geneQueryList;
        this.onlyDirectLinks = onlyDirectLinks;
    }

    /**
     * Runs the PSICQUIC query for the specific parameters. These parameters
     * will be updated by the PsicquicQuery.generateResults method.
     */
    @Override
    public void run() {
        try {
            PsicquicQuery.generateResults(geneQueryList, onlyDirectLinks, queryPosition, resultSet, databaseURL);
        } catch (QueryException e) {
            //do nothing
        }
    }

}
