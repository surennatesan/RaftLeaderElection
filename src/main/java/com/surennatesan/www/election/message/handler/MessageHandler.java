package com.surennatesan.www.election.message.handler;

import com.surennatesan.www.election.ElectionResult;
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
            LOGGER.info(" Received heartbeat from Leader " + heartbeatMessage.getLeaderId() + " for term " + String.valueOf(heartbeatMessage.getTerm()));
            leaderElection.heartbeat(heartbeatMessage.getLeaderId(), heartbeatMessage.getTerm());
        } else if (message.getMessageType().equals(MessageType.VOTE_REQUEST)) {
            VoteRequestMessage voteRequestMessage = (VoteRequestMessage) message;
            LOGGER.info(" Received vote request from " + voteRequestMessage.getCandidateId() + " for term " + String.valueOf(voteRequestMessage.getTerm()));
            boolean response = leaderElection.requestVote(voteRequestMessage.getCandidateId(), voteRequestMessage.getTerm());
            if (response) {
                LOGGER.info(" Sending yes vote response to " + voteRequestMessage.getCandidateId() + " for term " + String.valueOf(voteRequestMessage.getTerm()));
                sender.send(voteRequestMessage.getCandidateId(), new VoteResponseMessage(response, leaderElection.nodeId(), voteRequestMessage.getTerm()));
            }
        } else if (message.getMessageType().equals(MessageType.VOTE_RESPONSE)) {
            VoteResponseMessage voteResponseMessage = (VoteResponseMessage) message;
            LOGGER.info(" Received vote response from " + voteResponseMessage.getVoterId() + " for term " + String.valueOf(voteResponseMessage.getTerm()));
            ElectionResult result = leaderElection.registerVote(voteResponseMessage.getVoterId(), voteResponseMessage.getTerm(), voteResponseMessage.isResponse(), leaderElection.nodeId());
            if (ElectionResult.SUCCESS.equals(result)) {
                Leader leader = leaderElection.getLeader();
                LOGGER.info("Sendign heartbeat. I am Elected Leader =>  " + leader.getNodeId() + " for term " + String.valueOf(leader.getTerm()));
                sender.broadcast(new HeartbeatMessage(leader.getNodeId(), leader.getTerm()));
            }
        }
    }
}
