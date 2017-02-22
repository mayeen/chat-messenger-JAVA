package tcpserver;
import java.net.*;
import java.io.*;
import java.util.Arrays;

public class TCPServer implements Runnable
{  private ChatServerThread clients[] = new ChatServerThread[50];
   private ServerSocket server = null;
   private Thread       thread = null;
   private int clientCount = 0;
  
   
   private String[][] chatHistory = new String[100][1];
   private int chatIndex;
   private String[][] grp_chat_id = new String[100][1];
   private int grp_chat_index = 0 , grp_chat_flag = 0;
   private String[][] friend_list = new String[100][2];

   public TCPServer (int port)
   {  try
      {  System.out.println("Binding to port " + port + ", please wait  ...");
         server = new ServerSocket(port);  
         System.out.println("Server started: " + server);
         start(); }
      catch(IOException ioe)
      {  System.out.println("Can not bind to port " + port + ": " + ioe.getMessage()); }
   }
@Override
   public void run()
   {  while (thread != null)
      {  try
         {  System.out.println("Waiting for a client ..."); 
           addThread(server.accept()); 
         
           for(int i=0;i<clientCount;i++){
                friend_list[i][0] = String.valueOf(clients[i].getID());
            }
         }
        catch(IOException ioe)
        {  System.out.println("Server accept error: " + ioe); stop(); }
      }
  }
  
   public void start()  { if (thread == null)
      {  thread = new Thread(this); 
         thread.start();
      }
   }
   public void stop()   { 
   if (thread != null)
      {  thread.stop(); 
         thread = null;
      }
   }
   private int findClient(int ID)  //id input nibe,array position bole dibe
   {  for (int i = 0; i < clientCount; i++)
         if (clients[i].getID() == ID)
            return i;
      return -1;
   }
   public synchronized void handle(int ID, String input)
   {  if (input.equals(".bye"))
      {  clients[findClient(ID)].send(".bye");
         remove(ID); 
      chatHistory[chatIndex++][0] = ID+": "+input;
      }
//   else if(input.equals(".history")){
//       clients[findClient(ID)].send(".history");
//        remove(ID);
//   }
//      else
//         for (int i = 0; i < clientCount; i++)
//            clients[i].send(ID + ": " + input); //send kortese string
//            //System.out.println(input);


if (input.startsWith("unicast"))
         {  
             String messge[]=input.split(",");
             int client_id=Integer.parseInt(messge[1]);
             for (int i = 0; i < clientCount; i++){       
                  if( clients[i].getID()==client_id)     {
                      clients[i]. send(ID + ": " + messge[2]);
                      chatHistory[chatIndex++][0] = ID+": "+messge[2];
                  }               

             }
         }

        else if(input.startsWith("multicast"))
        {       
         String messge[]=input.split("-");
 
         String toClient=messge[1];
         String client_msg[]=toClient.split(";");
         String client_list=client_msg[0];

         String all_clients_id[]=client_list.split(",");
         int client_id=0;

         for (int i = 0; i < all_clients_id.length; i++){          
                client_id=Integer.parseInt(all_clients_id[i]);
                for(int j=0;j<clientCount;j++){
                if( clients[j].getID()==client_id)
                    clients[j]. send(ID + ": " +client_msg[1]);  

                 }
         }      
        chatHistory[chatIndex++][0] = ID+": "+client_msg[1];
       }
        else if (input.startsWith("regGroup"))
        {  
           
            grp_chat_id[grp_chat_index++][0] = ""+ID;
            for (int i = 0; i < clientCount; i++){       
                    if( clients[i].getID()==ID)     {
                        if( ID==Integer.parseInt(grp_chat_id[0][0]))
                        clients[i]. send("You created the group as Admin");
                        else
                        clients[i]. send("Successfully Registerted for Group Chat");
                    } 
            }
            

            for(int j =0;j<grp_chat_index;j++)
            {
                if( ID==Integer.parseInt(grp_chat_id[0][0]))
                    System.out.println(grp_chat_id[j][0] + " created a group chat");
                else
                    System.out.println(grp_chat_id[j][0] + " joined a group chat");
            }
        }
        else if(input.startsWith("grp")){
            String messge[]=input.split("-");
            
            for(int i =0;i<grp_chat_index;i++){
                if(Integer.parseInt(grp_chat_id[i][0]) == ID)
                    grp_chat_flag = 1;                  
            }
            
            if(grp_chat_flag==1){
                for(int i =0;i<clientCount;i++){
                    for(int j =0;j<grp_chat_index;j++){
                        if( clients[i].getID()==Integer.parseInt(grp_chat_id[j][0]))
                            clients[i]. send(ID + ": " +messge[1]); 
                    }
                }
                chatHistory[chatIndex++][0] = ID+": "+messge[1];
            }
            else {
                for (int i = 0; i < clientCount; i++){       
                    if( clients[i].getID()==ID)     {
                        clients[i]. send("You are not in the group");
                        
                    } 
                }
            }
        }
        
        else if (input.startsWith("kick"))
        {  
            String messge[]=input.split("-");
            
            if( ID==Integer.parseInt(grp_chat_id[0][0])){
                for(int i=1;i<grp_chat_index;i++){
                    if(grp_chat_id[i][0].equals(messge[1]))
                    {
                        grp_chat_id[i][0] = "0";
                        for (i = 0; i < clientCount; i++){       
                            if( (clients[i].getID()==ID))     {
                            clients[i]. send(messge[1]+" has been removed from the chat group");   
                    } 
                }
                       
                    }
                    else
                    {
                        for (i = 0; i < clientCount; i++)
                        {
                            if( (clients[i].getID()==ID))
                                {    
                                    clients[i]. send(messge[1]+" Not found in the Chat Group");   
                                }
                        }
                    }
                    
                }
                

            }
            else {
                for (int i = 0; i < clientCount; i++){       
                    if( clients[i].getID()==ID)     {
                        clients[i]. send("Access Denied");
                        
                    } 
                }
            }
        }
        
        else if (input.startsWith("add"))
        {  
            String messge[]=input.split("-");
            for(int i =0;i<clientCount;i++){
                if(friend_list[i][0].equals(String.valueOf(ID))){
                    clients[i]. send(" Friend added");
                    friend_list[i][1] = messge[1]+",";
                    
                }
            }
        }
        
        else if (input.startsWith("friends"))
        {  
            for(int i =0;i<clientCount;i++){
                if(friend_list[i][0].equals(String.valueOf(ID))){
                    if(friend_list[i][1]==null)
                        clients[i]. send(" No Friend added");
                    else
                    clients[i]. send(friend_list[i][1]);
                }
            }
        }
        
        else{            
            for (int i = 0; i < clientCount; i++)  {
                clients[i].send(ID + ": " + input);
                chatHistory[chatIndex++][0] = ID+": "+input;
            } 
        } 


        try{
            PrintWriter writer = new PrintWriter("Chathistory.txt", "UTF-8");
            for(int i =0 ; i <chatIndex ; i++){
                writer.println(Arrays.toString(chatHistory[i]));
            }
            writer.close();
        }catch (IOException e) {
           
        }



   }
   public synchronized void remove(int ID)
   {  int pos = findClient(ID);
      if (pos >= 0)
      {  ChatServerThread toTerminate = clients[pos];
         System.out.println("Removing client thread " + ID + " at " + pos);
         if (pos < clientCount-1)
            for (int i = pos+1; i < clientCount; i++)
               clients[i-1] = clients[i];
         clientCount--;
         try
         {  toTerminate.close(); }
         catch(IOException ioe)
         {  System.out.println("Error closing thread: " + ioe); }
         toTerminate.stop(); }
   }
   private void addThread(Socket socket)
   {  if (clientCount < clients.length)
      {  System.out.println("Client accepted: " + socket);
         clients[clientCount] = new ChatServerThread(this, socket);
         try
         {  clients[clientCount].open(); 
            clients[clientCount].start();  
            clientCount++; }
         catch(IOException ioe)
         {  System.out.println("Error opening thread: " + ioe); } }
      else
         System.out.println("Client refused: maximum " + clients.length + " reached.");
   }
   public static void main(String args[]) { 
       TCPServer server = null;
         server = new TCPServer(2000); }
}