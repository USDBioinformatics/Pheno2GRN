/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.usd.pheno2grn.fileconversion;

import java.util.ArrayList;
import java.util.Scanner;
import edu.usd.pheno2grn.utilities.json.GraphColors;
import edu.usd.pheno2grn.utilities.json.Node;

/**
 *
 * @author nick.weinandt
 */
public class DOTConverter {

    public static ArrayList<Node> convertDotToList(String s) {
        ArrayList<Node> listOfNodes = new ArrayList<>();

        //Declaring a scanner to read though input and find gene pairs
        Scanner sc = new Scanner(s);

        //Reading through the string and finding gene pairs
        while (sc.hasNextLine()) {
            Scanner lineScan = new Scanner(sc.nextLine());    //declaring a scanner for each line

            //declaring strings for first middle and last because lines with genes will always contain 
            //desired information in the first middle and last lines
            String first, middle, last;
            first = last = middle = "";
            //setting the values for the first middle and last values
            if (lineScan.hasNext()) {
                first = lineScan.next();
                if (lineScan.hasNext()) {
                    middle = lineScan.next();
                    if (lineScan.hasNext()) {
                        last = lineScan.next();
                    }
                }
            }

            //checking to see if the middle string contains the arrow (if it does there was a gene pair on the line
            if (middle.equals("->")) {
                //converting gene names to lowerCase
                first=first.toLowerCase();
                last=last.toLowerCase();
                //checking to see if first node was already added
                Node firstNode = new Node();
                firstNode.setNodeName(first);

                int posOfFirstNode = listOfNodes.indexOf(firstNode);

                System.out.println(posOfFirstNode);

                if (posOfFirstNode == -1) {
                    firstNode.getNodesConnectedTo().add(last);
                    //because grninfer, making the nodes blue
                    firstNode.setNodeColor(GraphColors.BLUE);
                    listOfNodes.add(firstNode);
                } else {
                    //Seeing if already added that edge
                    boolean foundEdge = false;
                    for (String nodeConnectedTo : listOfNodes.get(posOfFirstNode).getNodesConnectedTo()) {
                        if (nodeConnectedTo.equals(last)) {
                            foundEdge = true;
                            break;
                        }
                    }
                    if (!foundEdge) {
                        listOfNodes.get(posOfFirstNode).getNodesConnectedTo().add(last);
                    }
                }

                Node lastNode = new Node();
                lastNode.setNodeName(last);

                int posOfLastNode = listOfNodes.indexOf(lastNode);

                if (posOfLastNode == -1) {
                    //because grninfer, making the nodes blue
                    lastNode.setNodeColor(GraphColors.BLUE);
                    listOfNodes.add(lastNode);
                }

            }

        }
        return listOfNodes;
    }

  

}
