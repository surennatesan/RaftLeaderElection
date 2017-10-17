package com.surennatesan.www.election;

public class Leader {

    private String nodeId;
    private long term;

    public Leader(String nodeId, long term) {
        this.nodeId = nodeId;
        this.term = term;
    }

    public String getNodeId() {
        return nodeId;
    }

    public long getTerm() {
        return term;
    }
}
