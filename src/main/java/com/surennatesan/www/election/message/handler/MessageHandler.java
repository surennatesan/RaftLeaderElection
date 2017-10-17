package com.surennatesan.www.election.message.handler;

import com.surennatesan.www.election.Leader;
import com.surennatesan.www.election.LeaderElection;
import com.surennatesan.www.election.message.*;
import com.surennatesan.www.election.message.sender.MessageSender;

import java.util.logging.Logger;

public class MessageHandler {

    private final Logger LOGGER = Logger.getLogger(MessageHandler.class.getName());

    private MessageSender sender;
    private LeaderElection leaderElection;

    public MessageHandler(LeaderElection leaderElection, MessageSender sender) {
        this.leaderElection = leaderElection;
        this.sender = sender;
    }


    public void handleMessage(Message message) {
        if (message.getMessageType().equals(MessageType.HEARTBEAT)) {
            HeartbeatMessage heartbeatMessage = (HeartbeatMessage)message;
            LOGGER.info(" Received heartbeat from " + heartbeatMessage.getLeaderId() + " for term " + String.valueOf(heartbeatMessage.getTerm()));
            leaderElection.heartbeat(heartbeatMessage.getLeaderId(), heartbeatMessage.getTerm());
        } else if (message.getMessageType().equals(MessageType.VOTE_REQUEST)) {
            VoteRequestMessage voteRequestMessage = (VoteRequestMessage) message;
            LOGGER.info(" Received vote request from " + voteRequestMessage.getCandidateId() + " for term " + String.valueOf(voteRequestMessage.getTerm()));
            boolean response = leaderElection.requestVote(voteRequestMessage.getCandidateId(), voteRequestMessage.getTerm());
            if (response) {
                LOGGER.info(" Sending yes vote request to " + voteRequestMessage.getCandidateId() + " for term " + String.valueOf(voteRequestMessage.getTerm()));
                sender.send(voteRequestMessage.getCandidateId(), new VoteResponseMessage(response, leaderElection.nodeId(), voteRequestMessage.getTerm()));
            }
        } else if (message.getMessageType().equals(MessageType.VOTE_RESPONSE)) {
            VoteResponseMessage voteResponseMessage = (VoteResponseMessage) message;
            LOGGER.info(" Received vote request from " + voteResponseMessage.getVoterId() + " for term " + String.valueOf(voteResponseMessage.getTerm()));
            boolean response = leaderElection.registerVote(voteResponseMessage.getVoterId(), voteResponseMessage.getTerm(), voteResponseMessage.isResponse(), leaderElection.nodeId());
            if (response) {
                Leader leader = leaderElection.getLeader();
                if (leader != null) {
                    LOGGER.info("Broadcasting heartbeat with " + leader.getNodeId() + " for term " + String.valueOf(leader.getTerm()));
                    sender.broadcast(new HeartbeatMessage(leader.getNodeId(), leader.getTerm()));
                }
            }
        }
    }
}
