/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.usd.pheno2grn.restdatabases.phenoscape;

import edu.usd.pheno2grn.exceptions.NoResultQueryException;
import edu.usd.pheno2grn.exceptions.QueryException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Nick.Weinandt
 */
public class PhenoscapeQuery {

    public static List<String> getGenesByTao(String taoID) throws QueryException,NoResultQueryException {
        String URL = "http://kb.phenoscape.org/OBD-WS/gene/annotated?query=%7B%22phenotype%22:%5B%7B%22entity%22:%7B%22id%22:%22"
                + taoID + "%22,%22including_parts%22:false%7D%7D%5D,%22match_all_phenotypes%22:false%7D?media=json";
        URL queryURL;
        try {
            queryURL = new URL(URL);
        } catch (MalformedURLException e) {
            throw new QueryException("Could not query Phenoscape");
        }

        InputStream in = null;
        ArrayList<String> geneResults = new ArrayList<>();
        try {
            in = queryURL.openStream();

            String queryResults = IOUtils.toString(in, "UTF-8");
            
            //if found not results, report to user
            if (queryResults.length() == 0) {
                throw new NoResultQueryException();
            }

            //Phenoscape results are tab delimited in the second column, so 
            //iterating through the lines and getting the second column;
            Scanner sc = new Scanner(queryResults);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();

                Scanner sc2 = new Scanner(line);
                //adding the second column to the geneResults. If columns not
                //aligned properly, will throw exception
                if (sc2.hasNext()) {
                    sc2.next();
                } else {
                    throw new QueryException("Could not query Phenoscape");
                }
                if (sc2.hasNext()) {
                    String result = sc2.next();
                    geneResults.add(result);
                } else {
                    throw new QueryException("Could not query Phenoscape");
                }

            }
        } catch (IOException e) {
            throw new QueryException("Could not query Phenoscape");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    throw new QueryException("Could not query Phenoscape");
                }
            }
        }
        return geneResults;
    }
}
