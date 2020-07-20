package com.multichat;


//It usually goes like this:
// First,the Server opens a ServerSocket on a well known port and waits for input.
// Meanwhile the Client opens a(client)Socket with the servers hostname and this well known port address.
// It sends a request message to the server to// initialize a communication session.
//  The server receives the message,spawns a worker thread,which opens another ServerSocket on a different port and
//  the server sends a response, where it tells the client this port number.
// Now the client closes the actual connection and creates a new Socket,now with the port number he has just been told.
// This way,the server can handle more then one client at a time,because each client gets his individual'connection'(port).

public class ServerMain {
   public static void main(String[] args) {
      int port = 88;                      //port on which server is hosted
      Server server = new Server(port);
      server.start();                           //server class makes new threads and its handled by server helper if we want we
                                                //can just only extend thread in serve helper class only
   }

}
