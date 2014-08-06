package edu.usd.pheno2grn.utilities.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a Node in the Cytoscape.js network visualization.
 */
public class Node {

    private List<String> taosBelongTo = new ArrayList<>();
    private String nodeName = "";
    //default to grey
    private String nodeColor = GraphColors.GREY;
    private List<String> nodesConnectedTo = new ArrayList<>();

    public List<String> getNodesConnectedTo() {
        return nodesConnectedTo;
    }

    public void setNodesConnectedTo(List<String> nodesConnectedTo) {
        this.nodesConnectedTo = nodesConnectedTo;
    }

    public String getNodeColor() {
        return nodeColor;
    }

    public void setNodeColor(String nodeColor) {
        this.nodeColor = nodeColor;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.nodeName);
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
        final Node other = (Node) obj;
        if (!Objects.equals(this.nodeName, other.nodeName)) {
            return false;
        }
        return true;
    }

  

    public List<String> getTaosBelongTo() {
        return taosBelongTo;
    }

    public void setTaosBelongTo(List<String> taosBelongTo) {
        this.taosBelongTo = taosBelongTo;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

}
