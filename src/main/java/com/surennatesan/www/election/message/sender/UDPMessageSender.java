package com.surennatesan.www.election.message.sender;


import com.surennatesan.www.election.message.Message;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPMessageSender implements MessageSender {

    private String[] broadcastTargets = null;

    public UDPMessageSender(String[] broadcastTargets) {
        this.broadcastTargets = broadcastTargets;
    }

    private final Logger LOGGER = Logger.getLogger(UDPMessageSender.class.getName());

    @Override
    public void send(String target, Message message) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            int targetport = Integer.parseInt(target);

            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(message);
            out.flush();
            byte[] sendData = bos.toByteArray();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("localhost"), targetport);
            DatagramSocket clientSocket = new DatagramSocket();
            clientSocket.send(sendPacket);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Erroe sending message  => ", ex);
        } finally {
            try {
                bos.close();
            } catch (Exception ex) {
                LOGGER.info("Error closing the stream");
            }
        }
    }

    @Override
    public void broadcast(Message message) {
        for (String broadcastTarget : broadcastTargets) {
            send(broadcastTarget, message);
        }
    }
}
