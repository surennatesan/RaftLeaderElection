package com.surennatesan.www.election;

import java.util.HashSet;
import java.util.Set;

public class Election {

    private String startNode;
    private long startTime;
    private long electionTerm;
    private Set<String> yays = new HashSet<>();
    private Set<String> nahs = new HashSet<>();
    private int majorityCount;

    public Election(String startNode, long startTime, long electionTerm, int majorityCount) {
        this.startNode = startNode;
        this.startTime = startTime;
        this.electionTerm = electionTerm;
        this.majorityCount = majorityCount;
        yays.add(startNode);
    }

    public ElectionResult registerYay(String nodeId, long term) {
        if (term == electionTerm) {
            yays.add(nodeId);
            if (yays.size() >= majorityCount) {
                return ElectionResult.SUCCESS;
            } else {
                return ElectionResult.UNKNOWN;
            }
        }
        return ElectionResult.UNKNOWN;
    }

    public ElectionResult registerNah(String nodeId, long term) {
        if (term == electionTerm) {
            nahs.add(nodeId);
            if (nahs.size() >= majorityCount) {
                return ElectionResult.FAILURE;
            } else {
                return ElectionResult.UNKNOWN;
            }
        }
        return ElectionResult.UNKNOWN;
    }

    public String getStartNode() {
        return startNode;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getElectionTerm() {
        return electionTerm;
    }
}

