/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.usd.octopus.restdatabases.ensembl;

import edu.usd.octopus.exceptions.QueryException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Nick.Weinandt
 */
public class EnsemblQueries {

    public static String getGeneSymbolFromEnsemblId(String ensemblId) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            //making the thread sleep so the a HTTP 429 isn't retured (too many queries in a small amount of time)
            Thread.sleep(1000);
            Document doc = dBuilder.parse("http://beta.rest.ensembl.org/xrefs/id/" + ensemblId + "?content-type=text/xml");

            doc.getDocumentElement().normalize();

            //getting the database list
            NodeList databases = doc.getElementsByTagName("data");
            String geneSymbol = "";
            for (int i = 0; i < databases.getLength(); i++) {
                Element element = (Element) databases.item(i);
                geneSymbol = element.getAttribute("display_id");
            }

            return geneSymbol.toLowerCase();
        } catch (Exception e) {
            System.out.println("http://beta.rest.ensembl.org/xrefs/id/" + ensemblId + "?content-type=text/xml");
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error querying Ensembl", "Could not retrieve gene symbol from Ensebl Id."));
            return null;
        }
    }

    
    public static List<HomologyResult> queryHomologies(String sourceSpecies, String targetSpecies, List<String> geneSymbols){
        List<HomologyResult> homologyResults = new ArrayList();

        for (String geneSymbol : geneSymbols) {
            //Setting the target and source species and genesymbol
            HomologyResult homologyResult = new HomologyResult();
            homologyResult.setSourceGeneSymbol(geneSymbol);
            homologyResult.setTargetSpecies(targetSpecies);
            homologyResult.setSourceSpecies(sourceSpecies);

            try {

                //Parsing the ensembl uri
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

                Document doc = dBuilder.parse("http://beta.rest.ensembl.org/homology/symbol/" + sourceSpecies + "/" + geneSymbol + "?target_species=" + targetSpecies
                        + ";content-type=text/xml");
                doc.getDocumentElement().normalize();

                //assuming the number of sources and targets is the same and taking the first source and target
                NodeList sourceList = doc.getElementsByTagName("source");
                NodeList targetList = doc.getElementsByTagName("target");

                //setting the ensemblId of the source and target of the homology result
                Element sourceElement = (Element) sourceList.item(0);
                homologyResult.setSourceEnsemblId(sourceElement.getAttribute("id"));
                Element targetElement = (Element) targetList.item(0);
                homologyResult.setTargetEnsemblId(targetElement.getAttribute("id"));

                //adding the homology result to the homology list
                homologyResults.add(homologyResult);

            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage("homologuesMessage", new FacesMessage(FacesMessage.SEVERITY_ERROR, "No ENSEBML Result",
                            "Could not find target gene for: " + homologyResult.getSourceGeneSymbol()));
            }
        }
        return homologyResults;
    }
}
