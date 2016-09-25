package com.mellemhere.server;


import com.corundumstudio.socketio.*;
import com.mellemhere.main.Controller;
import com.mellemhere.server.mObjects.LogObject;
import java.util.Timer;
import java.util.TimerTask;

public class WebSocket {

    private final Controller con;
    private final Configuration config;
    private final int port = 6968;
    final SocketIOServer server;
    private boolean isOnline = false;

    public WebSocket(Controller con) {
        this.con = con;
        this.config = new Configuration();
        config.setPort(port);
        this.server = new SocketIOServer(config);
        this.start();
    }

    public void start() {
        this.addLogic();
        server.start();
        this.isOnline = true;
    }

    public void stop() {
        server.stop();
    }

    public SocketIOServer getServer() {
        return this.server;
    }

    public void broadcast(String event, Object message) {
        if (isOnline) {
            server.getBroadcastOperations().sendEvent(event, message);
        } else {
            System.out.println("Foi tentado emitir uma mensagem sem nenhum cliente!");
        }
    }


   

    private void addLogic() {
        this.server.addConnectListener(e -> {
            System.out.println("Novo usuario conectado ao painel! :)");
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    e.sendEvent("state", new LogObject("ok"));
                }
            }, 0, 1000);
        });

    }

}
