package com.surennatesan.www.election.message.sender;

import com.surennatesan.www.election.message.Message;

public interface MessageSender {

    void send(String target, Message message);
    void broadcast(Message message);
}
