package com.surennatesan.www.election.message.listener;

import com.surennatesan.www.election.message.Message;
import com.surennatesan.www.election.message.handler.MessageHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPMessageListener extends Thread implements  MessageListener{

    private MessageHandler messageHandler;
    private final Logger LOGGER = Logger.getLogger(UDPMessageListener.class.getName());
    private int port;

    public UDPMessageListener(int port, MessageHandler messageHandler) {
        this.port = port;
        this.messageHandler = messageHandler;
    }

    @Override
    public void run() {
        try {
            DatagramSocket serverSocket = new DatagramSocket(port);
            byte[] receiveData = new byte[1024];
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                try {
                    serverSocket.receive(receivePacket);
                    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(receivePacket.getData()));
                    Message message = (Message)ois.readObject();
                    messageHandler.handleMessage(message);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Message Listener ignored message => ",  e);
                }
            }
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Message Listener stopped => ",  exception);
        }
    }

    @Override
    public void listen() {
        this.start();
    }
}
