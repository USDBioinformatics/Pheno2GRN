/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.usd.octopus.restdatabases.psicquic;

import edu.usd.octopus.exceptions.QueryException;
import java.util.List;

/**
 *
 * @author Nick.Weinandt
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

    @Override
    public void run() {
        try {
            PsicquicQuery.generateResults(geneQueryList, onlyDirectLinks, queryPosition, resultSet, databaseURL);
        } catch (QueryException e) {
            //do nothing
        }
    }

}
