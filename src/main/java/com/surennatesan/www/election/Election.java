package com.surennatesan.www.election;

import java.util.HashSet;
import java.util.Set;

public class Election {

    private String startNode;
    private long startTime;
    private long electionTerm;
    private Set<String> ayes = new HashSet<>();
    private Set<String> nahs = new HashSet<>();
    private int majorityRequired;

    public Election(String startNode, long startTime, long electionTerm, int majorityRequired) {
        this.startNode = startNode;
        this.startTime = startTime;
        this.electionTerm = electionTerm;
        this.majorityRequired = majorityRequired;
        ayes.add(startNode); // add the candidate as aye voter
    }

    public ElectionResult registerAye(String nodeId, long term) {
        if (term == electionTerm) {
            ayes.add(nodeId);
            if (ayes.size() >= majorityRequired) {
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
            if (nahs.size() >= majorityRequired) {
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

