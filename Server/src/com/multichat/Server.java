package com.multichat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
   private final int port;
   private ArrayList<ServerHelper> helpers=new ArrayList<>();

   public Server (int port){
      this.port = port;
   }

   public List<ServerHelper> getList(){
      return helpers;
   }
   public void run(){
      try {
         ServerSocket servsoc = new ServerSocket(port);                       //serversocket is used by server to obtain a port to listen the client requests
         while (true) {
            Socket clientsoc = servsoc.accept();                              //program will wait on this line until a requests comes and return back obj of socket
            System.out.println("Request accepted from " + clientsoc);
            ServerHelper helper = new ServerHelper(this,clientsoc);           //to accept multiple clients we delegate this req to new thread while main thread is free to accept more req from more clients
            helpers.add(helper);
            helper.start();                                              //basically since serverhelper extends thread so helper is kinda of type thread
         }                                                              //so new thread goes to handle the client and main thread go up in while condition to accept new client
      } catch (IOException e) {                                        //so agian if new client comes new thread again created of name helper and process repeats
         e.printStackTrace();
      }
   }
}
