/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.server;

import com.mellemhere.main.Controller;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.json.JSONObject;

/**
 *
 * @author MellemHere
 */
public class WebServer {

    private HttpServer server;
    private final Controller con;
    private final int port = 8989;
    private final String area = "WEBSERVER";
    public String usersTmp;
    HashSet<String> ips = new HashSet<>();

    long lastLight = 0;

    public WebServer(Controller con) {
        this.con = con;
        this.con.log(area, "Starting webserver", null);
        try {
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
            System.out.println(this.server.getAddress());
            this.server.createContext("/", (final HttpExchange exchange) -> {
                String urli = exchange.getRequestURI().toString().replaceFirst("/", "");

                String ip = exchange.getRemoteAddress().getHostName();
                System.out.println("Cliente conectou!");
            });
            
            this.server.setExecutor(null); // creates a default executor
            this.server.start();
        } catch (Exception ex) {
            this.con.log(area, "ERROR", ex);
        }
    }

    public HttpServer getServer() {
        return this.server;
    }

    public String getHTML(String fileName) {
        InputStream in = getClass().getResourceAsStream("/com/mellemhere/painelweb/" + fileName + ".html");
        return new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\r\n"));
    }

    public void sendCustom(HttpExchange t, String name) throws IOException {
        InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();

        InputStream in = getClass().getResourceAsStream("/com/mellemhere/painelweb/" + name);
        String responce = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));

        t.sendResponseHeaders(200, responce.length());
        OutputStream os = t.getResponseBody();
        os.write(responce.getBytes());
        os.close();
    }

    public void sendText(HttpExchange t, String text) throws IOException {
        String response = text;

        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public void sendContent(HttpExchange t, String name) throws IOException {
        String response = getHTML(name);

        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    
    public boolean containsOnlyNumbers(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}
