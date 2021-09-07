/*
 * projectName: ChatRoom
 * fileName: ThreadServer.java        
 * packageName: chatRoom
 * date: 2021/01/16
 * 
 * created by Siying Zhou
 * All rights reserved.
 *
 * This program is use socket to realize online chat, 
 * swing to accomplish the GUI ,and thread pool to implement multithreading.
 */

package chatRoom;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;



/**
 * @version: V1.0
 * @author: Siying Zhou
 * @className: ThreadServer
 * @packageName: chatRoom
 * @description: This class is used to manage the server.
 * @date: 2021-01-16 
 **/
public class ThreadServer extends JFrame{
    // 存储所有注册的客户端 Stores all registered clients
	static JFrame jf;
	static JTextArea users;
	static JTextArea group;
	static JPanel panel;
	static JButton btn;
	static PrintStream out;
	static String user = "";
	static String groupmessage=  "";
	
	/**
	* @description: non-parameter constructor
	*/ 	
	ThreadServer(){
		 jf = new JFrame("");
		 panel = new JPanel();
		 users = new JTextArea(15, 25);
	     
		 // 设置自动换行 Set auto line wrap
		 users.setLineWrap(true);

		 // 添加到内容面板 Add to the content panel
		 JLabel jl1 = new JLabel("在线用户窗口");
	 	 panel.add(jl1);
         panel.add(users);
         JTextArea group = new JTextArea(15, 25);
         group.setLineWrap(true);
         JLabel jl2 = new JLabel("群发窗口");
	 	 panel.add(jl2);
         panel.add(group);
         
         // Create a submit button and click the button to get the input text
         btn = new JButton("群发消息");
         btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	String s = javax.swing.JOptionPane.showInputDialog("请输入群发消息:");
            	groupmessage =  groupmessage+"Server:"+s+'\n';
            	group.setText(groupmessage);
            	 Set<Map.Entry<String,Socket>> clientSet = clientMap.entrySet();
                 for (Map.Entry<String,Socket> entry : clientSet) {
                     try {
                         Socket socket = entry.getValue();
                         // 取得每个客户端的输出流 Get the output stream of each client
                         PrintStream out = new
                                 PrintStream(socket.getOutputStream(),
                                 true,"UTF-8");
                         out.println("G:from Server to everyone "+s);
                         
                     }catch (IOException ee) {
                         System.err.println("群聊异常，错误为 "+e);
                     }
                 }
            }
         });
	        panel.add(btn);
	        btn = new JButton("下线用户");
	        btn.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	String client = javax.swing.JOptionPane.showInputDialog("请输入下线用户的用户名:");

	            	String temp = "";
	            	String userName="";	     
	            	for (String keyName : clientMap.keySet()) {

                        if (keyName.equals(client)) {
                            userName = keyName;
                            continue;
                        }
                    }
	            	user = user.replace(userName,"********(someone got off)");
	            	System.out.println("user:"+user);
	            	users.setText(user);
	            	String tp = user;
	            	 Set<Map.Entry<String,Socket>> clientSet = clientMap.entrySet();
	                 for (Map.Entry<String,Socket> entry : clientSet) {
	                     try {
	                         Socket socket = entry.getValue();
	                         // 取得每个客户端的输出流 Get the output stream of each client
	                         PrintStream out = new
	                                 PrintStream(socket.getOutputStream(),
	                                 true,"UTF-8");	                        
	                         out.println("F:"+tp.replaceAll("\n", "."));
	                     }catch (IOException eeee) {
	                         System.err.println("群聊异常，错误为 "+e);
	                     }
	                 }
	                 Socket privateSocket = clientMap.get(userName);
	                 try {
	                     PrintStream out = new
	                             PrintStream(privateSocket.getOutputStream(),
	                             true,"UTF-8");
	                     out.println("E:end");
	                 }catch (IOException eeee) {
	                     System.err.println("私聊异常，错误为"+e);
	                 }
                    clientMap.remove(userName);
	            }	
	        });
	        panel.add(btn);
	    jf.setContentPane(panel);
		jf.setBounds(200,220,300,700);
		
		//设置窗口可见 Set window visible
		jf.setVisible(true);
		
		//设置关闭方式 如果不设置的话 似乎关闭窗口之后不会退出程序
		//Set the closing method. If not set, it seems that the program will not exit after closing the window
		jf.setDefaultCloseOperation(DISPOSE_ON_CLOSE); 
			}
    private static Map<String, Socket> clientMap = new
            ConcurrentHashMap<String, Socket>();

    /**
     * @version: V1.0
     * @author: Siying Zhou
     * @className: ExecuteClient
     * @packageName: chatRoom
     * @description: This class is used to specify the inner classes that communicate with each client
     * @date: 2021-01-16 
     **/
    private static class ExecuteClient implements Runnable {
        private Socket client;
        public ExecuteClient(Socket client) {
            this.client = client;
        }
        
    	/**
    	* @methodsName: run
    	* @description: This method is used to deal with the clients' operations.
    	* @param: Nothing.
    	* @return: Nothing.
    	* @throws: IOException
    	*/
        @Override
        public void run() {
            try {
                // 获取客户端输入流 Get client input stream
                Scanner in = new Scanner(client.getInputStream());
                String strFromClient;
                while (true) {
                    if (in.hasNextLine()) {
                        strFromClient = in.nextLine();
                        
                        // windows下将默认换⾏/r/n中的/r替换为空字符串
                        //Under windows, replace the /r in the default /r/n with an empty string
                        System.out.println( strFromClient);
                        Pattern pattern = Pattern.compile("\r");
                        Matcher matcher = pattern.matcher(strFromClient);
                        strFromClient = matcher.replaceAll("");
                        
                        // 注册流程 registration process
                        if (strFromClient.startsWith("userName")) {
                            String userName = strFromClient.split("\\:")[1];
                            registerUser(userName,client);
                            continue;
                        }
                        // 群聊流程 Group chat process
                        if (strFromClient.startsWith("G")) {
                            String msg = strFromClient.split("\\:")[1];
                            groupChat(msg);
                            continue;
                        }
                        // 私聊流程 Private chat process
                        if (strFromClient.startsWith("P")) {
                            String userName = strFromClient.split("\\:")[1];     
                            String msg = strFromClient.split("\\:")[2];
                            privateChat(userName,msg);
                        }
                        // 客户退出 Customer exit
                        if (strFromClient.contains("byebye")) {
                            String userName = null;
                            // 根据Socket找到userName Find userName according to Socket
                            for (String keyName : clientMap.keySet()) {
                                if (clientMap.get(keyName).equals(client)) {
                                    userName = keyName;
                                }
                            }
                            System.out.println("G:user "+userName+" has got off line!");
                            clientMap.remove(userName);
                            continue;
                        }
                    }
                }
            }catch (IOException e) {
                System.err.println("服务器通信异常，错误为 "+e);
            }
        }
       
    	/**
    	* @methodsName: registerUser
    	* @description: This method is used to register.
    	* @param: userName, client
    	* @return: Nothing.
    	* @throws: IOException
    	*/
        private void registerUser(String userName,Socket client) {
        	String tp = "";
            // 将客户信息保存到map中 Save customer information to a Map
            clientMap.put(userName,client);
            try {
                PrintStream out = new PrintStream(client.getOutputStream());
                // 告知客户注册成功 Inform the client of successful registration
                out.println("S:login successfully!");
                user+=userName;
                users.setText(user);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Set<Map.Entry<String,Socket>> clientSet = clientMap.entrySet();
            for (Map.Entry<String,Socket> entry : clientSet) {
                try {
                    Socket socket = entry.getValue();
                    // 取得每个客户端的输出流 Get the output stream of each client
                    PrintStream out = new
                            PrintStream(socket.getOutputStream(),
                            true,"UTF-8");
                    out.println("G:the user:"+userName+" has already logged in!");
                    out.println("G:Now the number of users is: "+(clientMap.size()));
                    tp = user;
                    out.println("F:"+tp.replaceAll("\n", "."));
                }catch (IOException e) {
                    System.err.println("群聊异常，错误为 "+e);
                }
            }
            user+='\n';
        }

    	/**
    	* @methodsName: groupChat
    	* @description: This method is used for group chat.
    	* @param: msg
    	* @return: Nothing.
    	* @throws: IOException
    	*/
        private void groupChat(String msg) {
            // 取出clientMap中所有Entry遍历发送群聊信息 Take out all Entry in clientMap and send group chat information
            Set<Map.Entry<String,Socket>> clientSet = clientMap.entrySet();
            for (Map.Entry<String,Socket> entry : clientSet) {
                try {
                    Socket socket = entry.getValue();
                    // 取得每个客户端的输出流 Get the output stream of each client
                    PrintStream out = new
                            PrintStream(socket.getOutputStream(),
                            true,"UTF-8");
                    out.println("G:"+msg);
                }catch (IOException e) {
                    System.err.println("群聊异常，错误为 "+e);
                }
            }
        }

    	/**
    	* @methodsName: privateChat
    	* @description: This method is used for private chat.
    	* @param: userName, msg
    	* @return: Nothing.
    	* @throws: IOException
    	*/
        private void privateChat(String userName,String msg) {
            Socket privateSocket = clientMap.get(userName);
            try {
                PrintStream out = new
                        PrintStream(privateSocket.getOutputStream(),
                        true,"UTF-8");
                out.println("S:privatechat:"+msg);
            }catch (IOException e) {
                System.err.println("私聊异常，错误为"+e);
            }
        }
    }
    
    /**
	* @methodsName: main
	* @description: This method is used for initialization and shutdown.
	* @param: args
	* @return: Nothing.
	* @throws: Nothing.
	*/
    public static void main(String[] args) throws Exception{
    	new ThreadServer();
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        ServerSocket serverSocket = new ServerSocket(6666);
        for (int i = 0 ; i < 20 ; i++) {
            System.out.println("等待客户端连接...");
            Socket client = serverSocket.accept();
            System.out.println("有新的客户端连接，端⼝号为: "+client.getPort());
            executorService.submit(new ExecuteClient(client));
        }
        executorService.shutdown();
        serverSocket.close();
    }
}
