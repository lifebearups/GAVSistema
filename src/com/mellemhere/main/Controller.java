/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.main;

import com.mellemhere.server.WebServer;


/**
 *
 * @author aluno
 */
public class Controller {

   
    /*
    
    
    
    
    SH1T C0D3 :)
    
    
    */
    
    public long startTime;
    private WebServer web;
    
   
    
    private boolean trombadinhaMode = false;

    public boolean isTrombadinhaMode() {
        return trombadinhaMode;
    }

    public void setTrombadinhaMode(boolean trombadinhaMode) {
        this.trombadinhaMode = trombadinhaMode;
    }
    
    
    public Controller() {
        this.start();
        this.startServers();
    }

    public void start() {
        this.startTime = System.currentTimeMillis();
    }

    
    
    private void startServers() {
        this.web = new WebServer(this);
    }

    public void log(String area, String starting_webserver, Object object) {
        System.out.println(starting_webserver);
    }


    
    
    

   
}
