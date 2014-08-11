package edu.usd.pheno2grn.restdatabases.psicquic;

import edu.usd.pheno2grn.exceptions.PackageFileReadException;
import edu.usd.pheno2grn.exceptions.QueryException;
import edu.usd.pheno2grn.exceptions.QueryParsingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 * Contains functionality for counting and querying PSICQUIC results.The url for
 * the documentation on how to query PSICQUIC:
 * https://code.google.com/p/psicquic/wiki/PsicquicSpec_1_3_Rest
 */
@ManagedBean(eager = true, name = "PsicquicController")
@RequestScoped
public class PsicquicQuery implements Serializable {

    private static final String COUNT_PARM = "?format=count";
    public static final int MAX_RESULTS_FROM_QUERY = 300;
    public static final int MAX_RESULTS_RETURNED = 100;
    static final Logger LOG=Logger.getLogger(PsicquicQuery.class.getName());

    /**
     * Queries a PSICQUIC database and saves the current location in the
     * records.
     *
     * @param geneQueryList List of genes to concatenate to the database url.
     * @param onlyDirectLinks If true, only interactions with at least one gene
     * from the gene list will appear in the results.
     * @param curPos Current position in the database query (essentialy row
     * number in the result table)
     * @param resultSet Synchronized set to add data to.
     * @param psicquicQueryURL Url of the database.
     * @throws QueryException
     */
    public static void generateResults(List<String> geneQueryList, boolean onlyDirectLinks,
            QueryPosition curPos, PsicquicSynchronizedResultSet resultSet, String psicquicQueryURL) throws QueryException {
        //creating the gene list string which will be concatenated to the database url
        String genePartOfUrl = generateUrlGenePart(geneQueryList);

        //creating a HashSet of genes to see if direct decendents
        HashSet<String> geneHashSet = new HashSet<>(geneQueryList);

        //This tracking position used to track current query lineNumber and QueryNumber
        QueryPosition trackingPos = new QueryPosition();

        int firstResultNumber = curPos.getQueryNum();
        int lineCounter = curPos.getLineNum();

        String url = psicquicQueryURL + "interactor/" + genePartOfUrl;

        //getting number of results at that url
        int numberOfResults = getCount(url);

        //seeing if the number of results is less than queryNumber+lineNumber
        //if it is less than, just returning as no more results are availible
        if (numberOfResults < firstResultNumber + lineCounter || numberOfResults == 0) {
            return;
        }

        queryLoop:
        while (firstResultNumber <= numberOfResults) {
            //submitting queries, MAX_RESULTS_FROM_QUERY at a time
            String formatParameters = "?firstResult=" + firstResultNumber + "&maxResults=" + MAX_RESULTS_FROM_QUERY;
           

            Scanner lineScanner = null;
            try {
                InputStream urlStream = (new URL(url + formatParameters)).openStream();

                lineScanner = new Scanner(new BufferedReader(new InputStreamReader(urlStream)));

                //seing if first time through loop
                if (firstResultNumber != curPos.getQueryNum()) {
                    lineCounter = 0;
                }

                resultLoop:
                while (lineScanner.hasNextLine()) {
                    if (curPos.getQueryNum() != firstResultNumber) {
                        lineCounter++;
                    } else {
                        if (lineCounter < curPos.getLineNum()) {
                            lineCounter++;
                            lineScanner.nextLine();
                            continue;
                        } else {
                            lineCounter++;
                        }
                    }
                    Scanner tab = new Scanner(lineScanner.nextLine());
                    tab.useDelimiter("\t");

                    PsicquicResult result = new PsicquicResult();

                    //keeping track of what column on
                    int columnTracker = 1;
                    while (tab.hasNext() && columnTracker < 7) {
                        String columnContents = tab.next();
                        //adding unique interaction id
                        if (columnTracker == 1) {
                            result.setInteractorAID(columnContents);
                        } else if (columnTracker == 2) {
                            result.setInteractorBID(columnContents);
                        } else if (columnTracker == 5) {
                            //adding the gene name A to the result
                            try {
                                result.setGeneNameA(extractGeneNameFromCell(columnContents, geneHashSet));
                            } catch (QueryParsingException e) {
                                continue resultLoop;
                            }
                        } else if (columnTracker == 6) {
                            //adding the gene name B to the result
                            try {
                                result.setGeneNameB(extractGeneNameFromCell(columnContents, geneHashSet));
                            } catch (QueryParsingException e) {
                                continue resultLoop;
                            }
                        }
                        columnTracker++;
                    }
                    //closing tab scanner
                    tab.close();

                    //adding the result to the result list
                    //if onlyDirectLinks is true, only adding it if one of the genes is a direct link
                    if (onlyDirectLinks) {
                        boolean shouldAdd = false;
                        if (result.getGeneNameA() != null) {
                            if (geneHashSet.contains(result.getGeneNameA())) {
                                shouldAdd = true;
                            }
                        } else if (result.getGeneNameB() != null) {
                            if (geneHashSet.contains(result.getGeneNameB())) {
                                shouldAdd = true;
                            }
                        }
                        if (shouldAdd) {
                            synchronized (resultSet.lock) {
                                //if the result Set is already full, reverting one line, and not adding result
                                if (resultSet.resultSet.size() != MAX_RESULTS_RETURNED) {
                                    resultSet.resultSet.add(result);
                                } else {
                                    trackingPos.setLineNum(lineCounter);
                                    trackingPos.setQueryNum(firstResultNumber);
                                    trackingPos.revertOneLine();
                                    break queryLoop;
                                }
                            }
                        }
                    } else {
                        synchronized (resultSet.lock) {
                            //if the result Set is already full, reverting one line, and not adding result
                            if (resultSet.resultSet.size() != MAX_RESULTS_RETURNED) {
                                resultSet.resultSet.add(result);

                            } else {
                                trackingPos.setLineNum(lineCounter);
                                trackingPos.setQueryNum(firstResultNumber);
                                trackingPos.revertOneLine();
                                break queryLoop;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                LOG.finest("Problem querying url");
            } finally {
                if (lineScanner != null) {
                    lineScanner.close();
                }

                firstResultNumber = firstResultNumber + MAX_RESULTS_FROM_QUERY;
                trackingPos.setLineNum(0);
                trackingPos.setQueryNum(firstResultNumber);
            }
        }

        curPos.setLineNum(trackingPos.getLineNum());
        curPos.setQueryNum(trackingPos.getQueryNum());
    }

    /**
     * Reads the PSICQUIC_Registry.txt to create a list of query URLS.
     *
     * @return List of query urls of the form: url/webservices/current/search/
     * @throws PackageFileReadException Thrown if problem reading the
     * PSICQUIC_Registry.txt file found in the
     * edu.usd.octopus.restdatabases.psicquic package
     */
    public static List<String> getListOfPsicquicUrls() throws PackageFileReadException {
        //looping throught the  list of REST services implementing PSICQUIC
        InputStream in = PsicquicQuery.class.getResourceAsStream("PSICQUIC_Registry.txt");
        BufferedReader bufReader = new BufferedReader(new InputStreamReader(in));
        String urlLine;
        List<String> listOfUrls = new ArrayList<>();
        try {
            while ((urlLine = bufReader.readLine()) != null) {
                listOfUrls.add(urlLine);
            }
            bufReader.close();
        } catch (IOException e) {
            throw new PackageFileReadException();
        }
        return listOfUrls;
    }

    /**
     * Generates the part of the url containing genes. It is properly encoded
     * for http requests.
     *
     * @param geneQueryList List of genes to be used in the query.
     * @return Properly encoded gene part of the url (not the complete url)
     */
    private static String generateUrlGenePart(List<String> geneQueryList) {
        //appending all the genes together with spaces (in url syntax %20) and or's
        StringBuilder geneStringBuilder = new StringBuilder();
        for (int i = 0; i < geneQueryList.size(); i++) {
            //only adding "OR" if not the last one
            if (i != geneQueryList.size() - 1) {
                geneStringBuilder.append(geneQueryList.get(i));
                // %20 is http encoding of a space
                geneStringBuilder.append("%20OR%20");
            } else {
                geneStringBuilder.append(geneQueryList.get(i));
            }
        }

        return geneStringBuilder.toString();
    }

    private static String extractGeneNameFromCell(String cellContents, HashSet<String> geneHashSet)
            throws QueryParsingException {
        //if column just contains - (means empty throwing exception
        if (cellContents.equals("-")) {
            throw new QueryParsingException("Cell is empty");
        }

        //iterating through | seperations and trying to match gene name
        Scanner columnScanner = new Scanner(cellContents);
        //using "|" as a delimiter
        columnScanner.useDelimiter("\\|");

        while (columnScanner.hasNext()) {
            //getting possible gene name
            String posGeneName = columnScanner.next();

            //making sure the content contains ":"
            if (!posGeneName.contains(":")) {
                continue;
            }

            //getting index of "("  (may not contain it)
            int indexOfParen = posGeneName.indexOf("(");

            //getting the substring becuase in a form such as:  "database:geneName(gene_synonym)"
            int indexOfColon = posGeneName.indexOf(":");
            if (indexOfParen != -1) {
                posGeneName = posGeneName.substring(indexOfColon + 1, indexOfParen);
            } else {
                posGeneName = posGeneName.substring(indexOfColon + 1);
            }

            posGeneName = posGeneName.toLowerCase();
            //Seeing if in gene list
            if (geneHashSet.contains(posGeneName)) {
                return posGeneName;
            }

        }

        /*
         If contents don't contain one of the inputted genes, looking for  gene name, gene name synonym, 
         shortLabel, shortlabel, display_short, display_long
         */
        String geneName = "(gene name)";
        if (cellContents.contains(geneName)) {
            int index = cellContents.indexOf(geneName);
            return cellContents.substring(cellContents.lastIndexOf(":", index) + 1, index).toLowerCase();
        }
        String display_short = "(display_short)";
        if (cellContents.contains(display_short)) {
            int index = cellContents.indexOf(display_short);
            return cellContents.substring(cellContents.lastIndexOf(":", index) + 1, index).toLowerCase();
        }

        String geneNameSynonym = "(gene name synonym)";
        if (cellContents.contains(geneNameSynonym)) {
            int index = cellContents.indexOf(geneNameSynonym);
            return cellContents.substring(cellContents.lastIndexOf(":", index) + 1, index).toLowerCase();
        }
        String shortLabel = "(shortLabel)";
        if (cellContents.contains(shortLabel)) {
            int index = cellContents.indexOf(shortLabel);
            return cellContents.substring(cellContents.lastIndexOf(":", index) + 1, index).toLowerCase();
        }
        String shortlabel = "(shortlabel)";
        if (cellContents.contains(shortlabel)) {
            int index = cellContents.indexOf(shortlabel);
            return cellContents.substring(cellContents.lastIndexOf(":", index) + 1, index).toLowerCase();
        }
        String display_long = "(display_long)";
        if (cellContents.contains(display_long)) {
            int index = cellContents.indexOf(display_long);
            return cellContents.substring(cellContents.lastIndexOf(":", index) + 1, index).toLowerCase();
        }

        //just returning the first item if no direct match to geneHashSet was found
        //getting the substring becuase in a form such as:  "database:geneName(gene_synonym)"
        columnScanner = new Scanner(cellContents);
        columnScanner.useDelimiter("\\|");

        String firstOne = columnScanner.next();
        int indexOfColon = firstOne.indexOf(":");
        int indexOfParen = firstOne.indexOf("(");
        if (indexOfParen != -1) {
            firstOne = firstOne.substring(indexOfColon + 1, indexOfParen);
        } else {
            firstOne = firstOne.substring(indexOfColon + 1);
        }
        return firstOne.toLowerCase();
    }

    /**
     * Counts the number of interactions in every psicquic database.
     *
     * @param geneQueryList List of genes to be used in interaction query.
     * @return Aggregated count of interactions in every Psicquic registry
     * database.
     * @throws QueryException Thrown if there is an issue reading from
     * PSICQUIC_Registry.txt
     */
    public static int countAll(List<String> geneQueryList) throws QueryException {
        String genePartOfUrl = generateUrlGenePart(geneQueryList);

        List<String> listOfUrls;
        try {
            listOfUrls = getListOfPsicquicUrls();
        } catch (PackageFileReadException e) {
            throw new QueryException("Could not read Psicquic Url List");
        }

        SynchronizedCounter synCounter = new SynchronizedCounter();
        //Getting count for each Psicquic Query url
        List<Thread> threads = new ArrayList<>();
        for (String psicquicUrl : listOfUrls) {
            String url = psicquicUrl + "interactor/" + genePartOfUrl;

            //starting a counting thread
            Thread thread = new Thread(new CountQueryThread(url, synCounter));
            threads.add(thread);
            thread.start();
        }

        //waiting for all the threads to finish before returning the value
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                LOG.finest("Thread join was Interruputed");
            }
        }

        return synCounter.getValue();
    }

    /**
     * Gets the number of interaction results for a specific PSICQUIC Url
     *
     * @param urlString Already contains the geneList with proper encoding.
     * @return Number of results of a the urlString
     * @throws QueryException Thrown if can't connect to rest service.
     */
    protected static int getCount(String urlString) throws QueryException {
        //appending the formatURL count to the urlString
        urlString += COUNT_PARM;

        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            throw new QueryException("Could not connect to: " + urlString);
        }

        InputStream input = null;
        try {
            input = url.openStream();

            /*
             Reading data from the url. Not using a buffered reader because only
             executing a couple of read operations.
             */
            int c;
            String numberHolder = "";
            while ((c = input.read()) != -1) {
                numberHolder += (char) c;
            }

            return new Integer(numberHolder);
        } catch (IOException e) {
            
            throw new QueryException("Problem getting content from url.");

        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    throw new QueryException("Couldn't close inputStream.");
                }
            }
        }
    }

    /**
     * Returns the Max number of results for use as a controller because xhtml
     * can't access static fields.
     *
     * @return
     */
    public int getNumResults() {
        return MAX_RESULTS_RETURNED;
    }

}

class SynchronizedCounter {

    private int value = 0;

    public int getValue() {
        return value;
    }

    public void setValue(int val) {
        this.value = val;
    }

}

class CountQueryThread implements Runnable {

    String urlString;
    SynchronizedCounter counter;

    public CountQueryThread(String urlString, SynchronizedCounter counter) {
        this.urlString = urlString;
        this.counter = counter;
    }

    @Override
    public void run() {
        try {
            int result = PsicquicQuery.getCount(urlString);

            //locking the counter to avoid update collisions
            synchronized (counter) {
                counter.setValue(result + counter.getValue());
            }

        } catch (QueryException e) {
            //Queries may bomb if databases are down.
        }
    }
}
