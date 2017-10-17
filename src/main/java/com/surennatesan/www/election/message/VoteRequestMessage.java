package com.surennatesan.www.election.message;

public class VoteRequestMessage extends Message {

    private String candidateId;
    private long term;

    public VoteRequestMessage(String candidateId, long term) {
        super(MessageType.VOTE_REQUEST);
        this.candidateId = candidateId;
        this.term = term;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public long getTerm() {
        return term;
    }
}
