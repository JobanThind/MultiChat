package com.multichat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MessagePane extends JPanel implements MessageListener {
   private final ChatClient client;
   private final String login;

   private DefaultListModel<String> listModel = new DefaultListModel<>();
   private JList<String> messageList = new JList<>(listModel);
   private JTextField inputfield = new JTextField();

   public MessagePane(ChatClient client, String login) {
      this.client = client;
      this.login = login;
      client.addmessagelistener(this);
      setLayout(new BorderLayout());
      add(new JScrollPane(messageList), BorderLayout.CENTER);      //message list needs to be scroll able
      add(inputfield, BorderLayout.SOUTH);
      inputfield.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            String text = inputfield.getText();
            try {
               client.msg(login, text);
               listModel.addElement("You :" + text);
               inputfield.setText("");
            } catch (IOException ex) {
               ex.printStackTrace();
            }

         }
      });

   }

   @Override
   public void onMessage(String from, String msgbody) {
      if (login.equalsIgnoreCase(from)) {
         String line = from + ": " + msgbody;
         listModel.addElement(line);
      }
   }
}
