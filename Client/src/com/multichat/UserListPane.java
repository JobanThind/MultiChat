package com.multichat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class UserListPane extends JPanel implements UserStatus {

   private final ChatClient client;
   private JList<String> userListUI;
   private DefaultListModel<String> userListModel;       //DefaultListModel provides a simple implementation of a list model, which can be used to manage items displayed by a JList

   public UserListPane(ChatClient client) {
      this.client = client;
      client.adduserstatus(this);                       //here this works because class implements userstatus and also we have t o overide the methods
      userListModel = new DefaultListModel<>();
      userListUI = new JList<>(userListModel);                      //creates a JList that displays elements from the specified, non-null, model.(DefaultListmodel here)
      setLayout(new BorderLayout());                                  //  BorderLayout arranges the components in the five regions.
      // Four sides are referred to as north, south, east, and west. The middle part is called the center.
      add(new JScrollPane(userListUI), BorderLayout.CENTER);             //jscrollpane The  parameter, when present, sets the scroll pane's client
      //we donot use frame.add() here because Normally you are not adding any new functionality to the frame so the
      //   preferred approach is to create an instance directly and use
      //JFrame extends Container and Container has an add method.
      userListUI.addMouseListener(new MouseAdapter() {               //if something is clicked in the list this method runs and we overide its mouseclicked method also it have other methods too
         @Override                                                    //like mousehold mouse moved etc
         public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() > 1) {                                                             //double click
               String login = userListUI.getSelectedValue();
               MessagePane messagepane = new MessagePane(client, login);
               JFrame f = new JFrame("Message " + login);
               f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);        // only this frame gets closed
               f.setSize(500, 500);
               f.getContentPane().add(messagepane, BorderLayout.CENTER);
               f.setVisible(true);

            }
         }
      });

   }

   public static void main(String args[]) {
      ChatClient client = new ChatClient("localhost", 88);                       //we get the socket for client
      UserListPane userlistpane = new UserListPane(client);                                      //initialize variables through constructor
      JFrame frame = new JFrame("User List");                                    //creates a window with title
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);                      //if this is closed all other frames are closed too
      frame.setSize(400, 600);
      frame.getContentPane().add(userlistpane, BorderLayout.CENTER);                   //Same as jframe.add().
      // Thus, both methods have the same basic behaviour, besides the fact that using getContentPane().add(...) is more explicit.
      frame.setVisible(true);
      if (client.connect()) {
         try {
            client.login("g1", "p");                 //when login method is called it will call userstatus.online() method and we will run its implementation in this class
            //which add login(name) to the userlistmodel which is further passed to jlist
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   @Override
   public void online(String login) {
      userListModel.addElement(login);
   }

   @Override
   public void offline(String login) {
      userListModel.removeElement(login);
   }
}
