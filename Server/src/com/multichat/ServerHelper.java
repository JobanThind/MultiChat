package com.multichat;

import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;

public class ServerHelper extends Thread {         //used to make multiple threads
   private Socket clientsoc;                       //Socket class represents the socket that both the client and the server use to communicate with each other. The client obtains a
                                                    // Socket object by instantiating one, whereas the server obtains a Socket object from the return value of the accept() method.
   private String login = null;                                  //username
   private Server server;
   private OutputStream outstream;                              //to send data to client
   private HashSet<String> groups = new HashSet<>();             //groups or topics for group chats

   public ServerHelper(Server server, Socket clientsoc) {      //server obj is used to to call getlist method and use it
      this.server = server;
      this.clientsoc = clientsoc;
   }


   public String getLogin() {
      return login;
   }


   public void run() {                  //overiding run method of thread
      try {
         handle();
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   private void handle() throws IOException, InterruptedIOException {
      InputStream inputStream = clientsoc.getInputStream();             //to get data from client
      this.outstream = clientsoc.getOutputStream();                    //to send data to client
//      To convert stream to string
//      Instantiate an InputStreamReader class by passing your InputStream object as parameter.
//      Then, create a BufferedReader, by passing above obtained InputStreamReader object as a parameter.
//      Now, read each line from this reader using the readLine() method

      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));  //inputstreamreader It reads bytes and decodes them into characters
                                                                                        //bufferedreader reads text from char iput stream
      String line;
      while ((line = reader.readLine()) != null) {                          //this reads the line from client //basically keeps on taking input
         String[] tokens = line.split(" ");                          //breaking the input to tokens
        if(tokens.length>0){
         String cmd = tokens[0];                                           //first word is command
         if (cmd.equalsIgnoreCase("quit") || cmd.equalsIgnoreCase("logout")) {
            handlelogout();
            break;
         } else if (cmd.equalsIgnoreCase("login")) {
            handlelogin(tokens, outstream);
         } else if (cmd.equalsIgnoreCase("msg")) {
            String[] message = line.split(" ", 3);                      //breaks line into max of 3 tokens
            handlemsg(message);
         } else if (cmd.equalsIgnoreCase("join")) {
            handlejoin(tokens);
         } else if (cmd.equalsIgnoreCase("leave")) {
            handleleave(tokens);
         } else {
            String msg = "unknown " + cmd + "\n";
            outstream.write(msg.getBytes());
         }
        }
      }
      clientsoc.close();
   }


   private void notify(String s) throws IOException {
      if (login != null)                     //condition to send message to online user who is logged in
         outstream.write(s.getBytes());            //outstream depends on who is calling notify method
   }


   private void handlelogin(String[] tokens, OutputStream outstream) throws IOException {
      if (tokens.length == 3) {
         String login = tokens[1];
         String password = tokens[2];

         if (login.equalsIgnoreCase("g1") && password.equals("p") ||
                 login.equalsIgnoreCase("g2") && password.equals("p")) {
            String msg = "Login sucessful\n";
            outstream.write(msg.getBytes());
            this.login = login;                                                  //initialize login variable
            System.out.println("User logged in successfully "+login);

            List<ServerHelper> helperList = server.getList();

            //send current users all other online logins
            for (ServerHelper helper : helperList) {
               if (helper != this && helper.getLogin() != null)
                  notify("Online " + helper.getLogin() + "\n");     //this writes in stream of this user
            }
            //send other online users status of current user
            for (ServerHelper helper : helperList) {
               if (helper != this)                                                                  //message should not be send to itself
                  helper.notify("Online " + this.getLogin()  + "\n");      //this writes in other users stream
            }

         } else {
            String msg = "Login failed \n";
            System.out.println(msg);
            outstream.write(msg.getBytes());
         }
      }
   }


   private void handlelogout() throws IOException {
      List<ServerHelper> helperList = server.getList();
      helperList.remove(this);
      //send other online users status of current user
      for (ServerHelper helper : helperList) {
         if (helper != this)
            helper.notify("Offline " + this.getLogin() + "\n");      //this writes in other users stream
      }
      System.out.println(this.login+" logged out");
      clientsoc.close();
   }


   // format msg receipient txtmsg
   //format msg #topic txtmsg
   private void handlemsg(String[] tokens) throws IOException {
      String sendto = tokens[1];
      String body = tokens[2];
      boolean istopic = (sendto.charAt(0) == '#');                          //if hash then mssg is being sent to group
      List<ServerHelper> helperList = server.getList();
      for (ServerHelper helper : helperList) {
         if (istopic) {                                                                     //if group mssg
            if (helper.isMember(sendto)) {
               helper.notify("msg "+login + " in " + sendto + ":" + body + "\n");

            }
         } else {                                                                   //if individual msg
            if (sendto.equalsIgnoreCase(helper.login)) {
               helper.notify("msg "+login + " : " + body + "\n");
            }
         }
      }
   }


   public boolean isMember(String topic) {
      return groups.contains(topic);
   }


   private void handlejoin(String[] tokens) {
      if (tokens.length > 1) {                               //to check if topic is provided to join
         String topic = tokens[1];
         groups.add(topic);
      }
   }


   private void handleleave(String[] tokens) {
      if (tokens.length > 1) {                            //to check if topic is provided to join
         String topic = tokens[1];
         groups.remove(topic);
      }
   }


}

