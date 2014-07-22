/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.usd.octopus.utilities.json;

import edu.usd.octopus.restdatabases.phenoscape.PhenotypeIdentifier;
import java.util.List;

/**
 *
 * @author Nick.Weinandt
 */
public class CreateCytoscapeJSON {

    public static String convertListToJSON(List<Node> nodes, List<Edge> edges, List<PhenotypeIdentifier> phenoIds) {
        String returnString = "nodes: [\n";


        //adding the list of nodes to the stringbuilder
        StringBuilder nodeString = new StringBuilder();
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            String vertexName = node.getNodeName();
            
            //if node belongs to only one phenotype id, getting the color for that id and setting
            if(node.getTaosBelongTo().size()==1){
                for(PhenotypeIdentifier id:phenoIds){
                    if(node.getTaosBelongTo().get(0).equals(id.id)){
                        node.setNodeColor(id.getColor().getColorValue());
                    }
                }
            }


            //seeing if node belongs to multiple phenotype identifiers
            if (node.getTaosBelongTo().size() > 1) {
                node.setNodeColor(GraphColors.BLACK);
            }

            if (i != nodes.size() - 1) {

                nodeString.append("{ data: { id: '" + vertexName + "', faveColor: '" + node.getNodeColor() + "', name: '" + vertexName + "' } },\n");
            } else {
                //node is the last one, not adding the comma at the end
                nodeString.append("{ data: { id: '" + vertexName + "', faveColor: '" + node.getNodeColor() + "', name: '" + vertexName + "' } }\n");
            }
        }

        StringBuilder edgeString = new StringBuilder();

        for (int i = 0; i < edges.size(); i++) {

            String sourceName = edges.get(i).getSource();
            String targetName = edges.get(i).getTarget();
            //if the last node in the list and last vertex connected to for that node, don't add the comma
            if (i == edges.size() - 1) {
                edgeString.append("{ data: { source: '" + sourceName + "', target: '" + targetName + "' } }\n");
            } else {
                edgeString.append("{ data: { source: '" + sourceName + "', target: '" + targetName + "' } },\n");
            }
        }

        //adding last part of nodes
        returnString += nodeString.toString() + "],\nedges: [\n" + edgeString.toString() + "]\n";
        System.out.println("Completed cytoscape JSON");
        return returnString;
    }
}
