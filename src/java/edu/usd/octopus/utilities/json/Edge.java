package edu.usd.octopus.utilities.json;

import java.util.Objects;

/**
 * Represents an Edge in the Cytoscape.js network. An edge links a source and
 * target node.
 */
public class Edge {

    /**
     * Name of the source node.
     */
    private String source = "";
    /**
     * Name of the target node.
     */
    private String target = "";

    public Edge() {
    }

    
    
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.source);
        hash = 37 * hash + Objects.hashCode(this.target);
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
        final Edge other = (Edge) obj;
        if (!Objects.equals(this.source, other.source)) {
            return false;
        }
        if (!Objects.equals(this.target, other.target)) {
            return false;
        }
        return true;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

}
