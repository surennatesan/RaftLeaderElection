package com.surennatesan.www.election.message;

public class VoteResponseMessage extends Message {

    private boolean response;
    private String voterId;
    private long term;

    public VoteResponseMessage(boolean response, String voterId, long term) {
        super(MessageType.VOTE_RESPONSE);
        this.response = response;
        this.voterId = voterId;
        this.term = term;
    }

    public boolean isResponse() {
        return response;
    }

    public String getVoterId() {
        return voterId;
    }

    public long getTerm() {
        return term;
    }
}
