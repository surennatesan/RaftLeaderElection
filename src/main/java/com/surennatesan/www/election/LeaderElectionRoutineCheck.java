package com.surennatesan.www.election;

import com.surennatesan.www.election.message.HeartbeatMessage;
import com.surennatesan.www.election.message.VoteRequestMessage;
import com.surennatesan.www.election.message.sender.MessageSender;

import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LeaderElectionRoutineCheck {

    private LeaderElection leaderElection;

    private MessageSender sender;
    private long lastTimeHeartbeatSend = 0;

    private final static long HEARTBEAT_SENT_TIMEOUT = 30 * 1000;
    private final static int VOTING_START_RANDOM_MILLIS_MIN = 1 * 1000;
    private final static int VOTING_START_RANDOM_MILLIS_MAX = 10 * 1000;
    private final Logger LOGGER = Logger.getLogger(LeaderElectionRoutineCheck.class.getName());

    public LeaderElectionRoutineCheck(LeaderElection leaderElection, MessageSender sender) {
        this.leaderElection = leaderElection;
        this.sender = sender;
    }

    public void startRoutine() throws InterruptedException {
        while(true) {
            logCurrentLeader();
            try {
                if (leaderElection.amILeader() && System.currentTimeMillis() - lastTimeHeartbeatSend > HEARTBEAT_SENT_TIMEOUT) {
                    Leader leader = leaderElection.getLeader();
                    sender.broadcast(new HeartbeatMessage(leader.getNodeId(), leader.getTerm()));
                    LOGGER.info("Broadcasting heartbeat from leader");
                } else if (leaderElection.needElection()) {
                    LOGGER.info(" Election Needed ");
                    initiateElection();
                }
                Thread.sleep(20 * 1000);
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Encountered error while routine => ", ex);
            }
        }
    }

    public void initiateElection() {
        try {
            long randomWaitTime = ThreadLocalRandom.current().nextLong(VOTING_START_RANDOM_MILLIS_MIN, VOTING_START_RANDOM_MILLIS_MAX + 1);
            LOGGER.info("Random wait time started before starting election in millis =>" + randomWaitTime);
            Thread.sleep(randomWaitTime);
            Election election = leaderElection.startElection();
            if (election != null) {
                LOGGER.info("Started election with term " + election.getElectionTerm() + ". Broadcasting  vote request" );
                sender.broadcast(new VoteRequestMessage(election.getStartNode(), election.getElectionTerm()));
            }

        } catch (Exception ex) {
            LOGGER.warning(ex.getMessage());
        }
    }

    private void logCurrentLeader() {
        Leader leader = leaderElection.getLeader();
        if (leader != null) {
            LOGGER.info("Leader is " + leader.getNodeId() + " with term " + leader.getTerm());
        } else {
            LOGGER.info("No elected Leader");
        }
    }
}
