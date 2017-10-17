package com.surennatesan.www.election.message;

public class HeartbeatMessage extends Message {

    private String leaderId;
    private long term;

    public HeartbeatMessage(String leaderId, long term) {
        super(MessageType.HEARTBEAT);
        this.leaderId = leaderId;
        this.term = term;
    }

    public String getLeaderId() {
        return leaderId;
    }

    public long getTerm() {
        return term;
    }
}
