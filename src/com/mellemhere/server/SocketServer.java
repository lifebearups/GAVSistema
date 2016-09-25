/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.server;

import com.mellemhere.main.Controller;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;

/**
 *
 * @author aluno
 */
public class SocketServer extends Thread {

    private final String area = "Socket Server";

    private final int port = 8989;

    private ServerSocket serverSocket;
    private Controller con;

    private boolean running = false;

    private HashSet<ClientListener> clients = new HashSet<>();

    /*
     */
    public SocketServer(Controller con) {
        try {
            this.con = con;
            this.serverSocket = new ServerSocket(port);
            this.con.log(area, "Started socket server, on: " + this.serverSocket.getLocalSocketAddress(), null);
        } catch (IOException ex) {
            this.con.log(area, "Can't listen to port: " + port, ex);
        }
    }

    public Controller getCon() {
        return con;
    }

    public HashSet<ClientListener> getClients() {
        return this.clients;
    }

    public void startServer() {
        this.running = true;
        this.start();
    }

    public void stopServer() {
        this.running = false;
    }

    /*
        Main loop to listen to new connection
     */
    @Override
    public void run() {
        while (running) {
            this.con.log(area, "Waiting for new connection from client", null);
            try {
                new ClientListener(this, serverSocket.accept());
            } catch (IOException ex) {
                this.con.log(area, "Could not accept client connection - ", ex);
            }
        }
    }

    /*
        Propagate a message to all clients
     */
    public void sendMessageToAll(String message) {
        clients.forEach(e -> {
            e.sendMessage(message);
        });
    }

}
