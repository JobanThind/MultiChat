package com.multichat;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class ChatClient  {
   private final int port;
   private final String servername;
   private static Socket socket;
   private static InputStream serverin;
   private OutputStream serverout;
   private BufferedReader reader;

   private ArrayList<UserStatus> userstatuses=new ArrayList<>();
   private ArrayList<MessageListener> messages=new ArrayList<>();

   public ChatClient(String servername, int port) {
      this.servername = servername;
      this.port = port;
   }

   public static void main(String args[]) throws IOException {
      ChatClient client = new ChatClient("localhost", 88);

      //f you use new with an interface, you have to override the methods providerd by the interface in the { }
      //new UserStatus() { is not instantiating an interface, it is declaring an anonymous inner class. basically an anonymous class
      // (a class implementing the interface UserStatus) which doesn't have a name per se.

      client.adduserstatus(new UserStatus() {
         @Override
         public void online(String login) {
            System.out.println("Online " + login);

         }

         @Override
         public void offline(String login) {
            System.out.println("Offline " + login);

         }
      });
      client.addmessagelistener(new MessageListener() {
         @Override
         public void onMessage(String from, String msgbody) {
            System.out.println("You got message from "+from+" :"+msgbody);
         }
      });

      if (client.connect()) {                            //connects to server and we get streams to read write
         System.out.println("Connected");
         client.login("g1", "p");
         client.msg("g2","new message");
//         client.logoff();

      }
      else {
         System.out.println("Failed");
      }
   }

   public void msg(String sendto, String msgbody) throws IOException {
      String cmd="msg "+sendto+" "+msgbody+"\n";
      serverout.write(cmd.getBytes());
   }

   public void adduserstatus(UserStatus user){
      userstatuses.add(user);
   }
   public void removeuserstatus(UserStatus user){
      userstatuses.remove(user);
   }

   public void addmessagelistener(MessageListener user){
      messages.add(user);
   }
   public void removemessagelistener(MessageListener user){
      messages.remove(user);
   }

   public boolean connect() {
      try {
//         InetAddress add=InetAddress.getByName("localhost");
//         this.socket=new Socket(servername,port,add,1234);     // by this we can bind client to a specific port number
//basically can be done without this too

         this.socket = new Socket(servername, port);        //this will directly connect the client to server but if no argument is passed then
         //we have to use socket.connect() method.The port to client is automatically assigned by OS
         this.serverout = socket.getOutputStream();            //get streams for this socket or we can say client and name them serverout and serverin
         this.serverin = socket.getInputStream();
         this.reader = new BufferedReader(new InputStreamReader(serverin));

         return true;
      } catch (Exception e) {
         e.printStackTrace();
      }
      return false;
   }


   public boolean login(String g1, String p) throws IOException {
      String cmd = "login" + " " + g1 + " " + p + "\n";
      serverout.write(cmd.getBytes());


      String response=reader.readLine();

      System.out.println("response "+response);
      if (response.equalsIgnoreCase("Login Sucessful")) {
         startMessageReader();
         return true;
      }
      else {
         return false;
      }
   }
   public void logoff() throws IOException {
      String cmd = "quit\n";
      serverout.write(cmd.getBytes());
   }

   private void startMessageReader() {
      Thread t=new Thread(){
         public  void run(){
            readMessageLoop();      //overriding run method
         }
     };
      t.start();           //will run readmessageloop

   }


public void readMessageLoop()  {
      try{
      String line;
         while((line=reader.readLine())!=null){
//            System.out.print(line);

         String[] tokens = line.split(" ");                          //breaking the input to tokens
         if (tokens.length > 0) {
            String cmd = tokens[0];
            if(cmd.equalsIgnoreCase("online")){
               handleOnline(tokens);
            }
            else if(cmd.equalsIgnoreCase("offline")){
               handleOffline(tokens);
            }
            else if(cmd.equalsIgnoreCase("msg")){
               String[] tokenmsg=line.split(" ",3);
               handlemsg(tokenmsg);
            }
         }
      }
   }
      catch (Exception e){
         e.printStackTrace();
         try {
            socket.close();                                         //to close the connection
         }
         catch (Exception ex) {
            ex.printStackTrace();
         }
      }
}

   private void handlemsg(String[] tokenmsg) {
      String login =tokenmsg[1];
      String msg=tokenmsg[2];
      for(MessageListener listener:messages){
         listener.onMessage(login,msg);
      }
   }

   private void handleOffline(String[] tokens) {
      String login=tokens[1];
      for(UserStatus user:userstatuses){
         user.offline(login);
      }
   }

   private void handleOnline(String[] tokens) {
      String login=tokens[1];
      for(UserStatus user:userstatuses){
         user.online(login);
      }
   }

}
