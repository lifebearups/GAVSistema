/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.server;

import com.mellemhere.main.Controller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author aluno
 */
public class ClientListener extends Thread {

    private String area = "client listener";

    private Socket client;
    private SocketServer server;
    private PrintWriter out;
    private final Commands cmd;
    private SerialConnection serialConnection;
    private String clientID = "Esperando por ID";
    private Controller con;

    public ClientListener(SocketServer server, Socket client) {
        this.con = server.getCon();
        this.con.log(area, "Starting new client listener.. waiting for ID", null);
        this.server = server;
        this.client = client;
        this.cmd = new Commands(this);

        try {
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException ex) {
            this.con.log(area, "Could not start output service", ex);
        }

        //Start listening
        this.start();
    }

    public ClientListener(SocketServer server, SerialConnection con) {
        this.con = server.getCon();
        this.con.log(area, "Starting new client listener via serial.. waiting for ID", null);
        this.serialConnection = con;
        this.server = server;
        this.cmd = new Commands(this);
        //Start listening
        this.start();
    }


    public Socket getClient() {
        return client;
    }

    public SocketServer getServer() {
        return server;
    }

    @Override
    public void run() {
        this.server.getClients().add(this);
        if (serialConnection != null) {
            return;
        }
        try {

            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                /*
                 bye command closes the client down
                 */
                if (inputLine.equals("bye")) {
                    break;
                } else {
                    process(inputLine);
                }
            }

            this.con.log(area, "Client " + this.clientID + " disconected", null);
            out.close();
            in.close();
            client.close();
            this.server.getClients().remove(this);
        } catch (Exception ex) {
            this.con.log(area, "Error in listening to client", ex);
        }

    }

    public void process(String message) {
        if (message.startsWith(" ")) {
            return;
        }
        if (message.contains(";")) {
            String[] commandArgs = message.split(";");
            sendMessage(cmd.command(commandArgs[0], commandArgs[1]));
        } else {
            this.con.log(area, "Out of normal command recived: " + message, null);
        }
    }

    public String processWithResponce(String message) {
        if (message.startsWith(" ")) {
            return null;
        }
        String[] commandArgs = message.split(";");
        String responce = cmd.command(commandArgs[0], commandArgs[1]);
        sendMessage(responce);
        return responce;
    }

    public void sendMessage(String message) {
        if (message == null) {
            return;
        }
        this.con.log(area, "Sending message to client id(" + this.clientID + "): " + message, null);
        if (serialConnection != null) {  
            serialConnection.sendMessage(message);
        } else {
            out.println(message);
        }
    }

    public String getID() {
        return this.clientID;
    }

    public void setID(String id) {
        this.clientID = id;
        this.area += " - " + id;
    }

}
