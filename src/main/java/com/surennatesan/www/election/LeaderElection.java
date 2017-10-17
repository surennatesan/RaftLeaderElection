package com.surennatesan.www.election;

import java.util.logging.Logger;

public class LeaderElection {

    private static final long HEART_BEAT_TIMEOUT = 60 * 1000;
    private static final long ELECTION_TIMEOUT = 60 * 1000;
    private static final int QUOROM_MAJORITY_COUNT = 2;
    private String leaderNodeId;
    private String nodeId;
    private Election currentElection;
    private long lastHeartbeatTimestamp;
    private long currentTerm = -1;

    private final Logger LOGGER = Logger.getLogger(LeaderElection.class.getName());

    public LeaderElection(String nodeId) {
        this.nodeId = nodeId;
    }

    public synchronized void heartbeat(String leaderNodeId, long term) {
        if ((term == currentTerm && isElectionOngoing()) || term >= currentTerm) {
            this.leaderNodeId = leaderNodeId;
            this.lastHeartbeatTimestamp = System.currentTimeMillis();
            this.currentTerm = term;
            if (isElectionOngoing()) {
                currentElection = null;
            }
        }
    }

    public synchronized Leader getLeader() {
        if (leaderNodeId != null &&  !heartbeatTimedOut()) {
            return new Leader(leaderNodeId, currentTerm);
        } else {
            return null;
        }
    }

    public synchronized boolean needElection() {
        return ((leaderNodeId == null && !isElectionOngoing()) ||
                (leaderNodeId == null && currentElectionTimedOut())) ||
                (leaderNodeId != null && heartbeatTimedOut() && !isElectionOngoing()) ||
                (leaderNodeId != null && heartbeatTimedOut() && currentElectionTimedOut());
    }

    public synchronized boolean requestVote(String nodeId, long term) {
        if (term > currentTerm) {
            currentElection = new Election(nodeId, System.currentTimeMillis(), term, QUOROM_MAJORITY_COUNT);
            currentTerm = term;
            if (isElectionOngoing()) {
                currentElection = null;
            }
            return true;
        }
        return false;
    }

    public synchronized Election startElection() {
        if (needElection()) {
            currentElection = new Election(nodeId, System.currentTimeMillis(), ++currentTerm, QUOROM_MAJORITY_COUNT);
            return currentElection;
        }
        return null;
    }

    public synchronized ElectionResult registerVote(String votedNodeId, long electionTerm, boolean vote, String candidateId) {
        ElectionResult result = ElectionResult.UNKNOWN;
        if (isElectionOngoing() && electionTerm == currentTerm) {
            if (vote) {
                result = currentElection.registerAye(votedNodeId, electionTerm);
            } else {
                result = currentElection.registerNah(votedNodeId, electionTerm);
            }
            if (result.equals(ElectionResult.FAILURE)) {
                currentElection = null;
            } else if (result.equals(ElectionResult.SUCCESS)) {
                leaderNodeId = candidateId;
                lastHeartbeatTimestamp = System.currentTimeMillis();
                currentElection = null;
            }
        }
        return result;
    }


    public boolean isElectionOngoing() {
        return (currentElection != null && currentElection.getElectionTerm() == currentTerm);
    }

    private boolean currentElectionTimedOut() {
        return (isElectionOngoing() && System.currentTimeMillis() - currentElection.getStartTime() > ELECTION_TIMEOUT);
    }

    private boolean heartbeatTimedOut() {
        return (System.currentTimeMillis() - lastHeartbeatTimestamp > HEART_BEAT_TIMEOUT);
    }

    public String nodeId() {
      return nodeId;
    }

    public boolean amILeader() {
        return leaderNodeId != null && leaderNodeId.equals(nodeId);
    }

}
