/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.usd.octopus.utilities.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Nick.Weinandt
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
