/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpclient;

import java.net.*;
import java.io.*;

public class ChatClientThread extends Thread
{  private Socket           socket   = null;
   private TCPclient       client   = null;
   private DataInputStream  streamIn = null;

   public ChatClientThread(TCPclient  _client, Socket _socket)
   {  client   = _client;
      socket   = _socket;
      open();  
      start();
   }
   public void open()
   {  try
      {  streamIn  = new DataInputStream(socket.getInputStream());
      }
      catch(IOException ioe)
      {  System.out.println("Error getting input stream: " + ioe);
         client.stop();
      }
   }
   public void close()
   {  try
      {  if (streamIn != null) streamIn.close();
      }
      catch(IOException ioe)
      {  System.out.println("Error closing input stream: " + ioe);
      }
   }
   public void run()
   {  while (true)
      {  try
         {  client.handle(streamIn.readUTF());//read kore stream theke
         }
         catch(IOException ioe)
         {  System.out.println("Listening error: " + ioe.getMessage());
            client.stop();
         }
      }
   }
}