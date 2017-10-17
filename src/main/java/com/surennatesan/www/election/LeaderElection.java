package com.surennatesan.www.election;

import java.util.logging.Logger;

public class LeaderElection {

    private static final long HEART_BEAT_TIMEOUT = 60 * 1000;
    private static final long ELECTION_TIMEOUT = 60 * 1000;
    private static final int QUOROM_MAJORITY_COUNT = 2;
    private String leaderNodeId;
    private String nodeId;
    private Election lastElection;
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
                lastElection = null;
            }
        }
    }

    public synchronized Leader getLeader() {
        if (leaderNodeId != null && (leaderNodeId.equals(nodeId) || !heartbeatTimedout())) {
            return new Leader(leaderNodeId, currentTerm);
        } else {
            return null;
        }
    }

    public synchronized boolean needElection() {
        return ((leaderNodeId == null && !isElectionOngoing()) ||
                (leaderNodeId == null && lastElectionTimedout())) ||
                (leaderNodeId != null && !leaderNodeId.equals(nodeId) && heartbeatTimedout() && !isElectionOngoing()) ||
                (leaderNodeId != null && !leaderNodeId.equals(nodeId) && heartbeatTimedout() && lastElectionTimedout());
    }

    public synchronized boolean requestVote(String nodeId, long term) {
        if (term > currentTerm) {
            lastElection = new Election(nodeId, System.currentTimeMillis(), term, QUOROM_MAJORITY_COUNT);
            currentTerm = term;
            if (isElectionOngoing()) {
                lastElection = null;
            }
            return true;
        }
        return false;
    }

    public synchronized Election startElection() {
        if (needElection()) {
            lastElection = new Election(nodeId, System.currentTimeMillis(), ++currentTerm, QUOROM_MAJORITY_COUNT);
            return lastElection;
        }
        return null;
    }

    public synchronized boolean registerVote(String votedNodeId, long electionTerm, boolean vote, String candidateId) {
        if (isElectionOngoing() && electionTerm == currentTerm) {
            ElectionResult result;
            if (vote) {
                result = lastElection.registerYay(votedNodeId, electionTerm);
            } else {
                result = lastElection.registerNah(votedNodeId, electionTerm);
            }
            if (result.equals(ElectionResult.FAILURE)) {
                lastElection = null;
                return false;
            } else if (result.equals(ElectionResult.SUCCESS)) {
                leaderNodeId = candidateId;
                lastElection = null;
                return true;
            }
        }
        return false;
    }


    public boolean isElectionOngoing() {
        return (lastElection != null && lastElection.getElectionTerm() == currentTerm);
    }

    private boolean lastElectionTimedout() {
        return (isElectionOngoing() && System.currentTimeMillis() - lastElection.getStartTime() > ELECTION_TIMEOUT);
    }

    private boolean heartbeatTimedout() {
        return (System.currentTimeMillis() - lastHeartbeatTimestamp > HEART_BEAT_TIMEOUT);
    }

    public String nodeId() {
      return nodeId;
    }

    public boolean amILeader() {
        return leaderNodeId != null && leaderNodeId.equals(nodeId);
    }

}
