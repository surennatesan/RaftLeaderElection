package com.surennatesan.www.election;

import com.surennatesan.www.election.message.listener.MessageListener;
import com.surennatesan.www.election.message.sender.UDPMessageSender;
import com.surennatesan.www.election.message.handler.MessageHandler;
import com.surennatesan.www.election.message.listener.UDPMessageListener;
import com.surennatesan.www.election.message.sender.MessageSender;

public class LeaderElectionMain {

    public static void main(String[] args) throws InterruptedException {
        String id = args[0];
        MessageSender sender = new UDPMessageSender(new String[]{"5000", "5001", "5002"});
        LeaderElection leaderElection = new LeaderElection(id);
        LeaderElectionRoutineCheck routine = new LeaderElectionRoutineCheck(leaderElection, sender);
        MessageHandler handler = new MessageHandler(leaderElection, sender);
        MessageListener listener = new UDPMessageListener(Integer.parseInt(id), handler);
        listener.listen();
        routine.startRoutine();
    }
}
