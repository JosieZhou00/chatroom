/*
 * projectName: ChatRoom
 * fileName: ThreadClient.java        
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

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * @version: V1.0
 * @author: Siying Zhou
 * @className: ReadFromServerThread
 * @packageName: chatRoom
 * @description: This class is used to read the server information thread.
 * @date: 2021-01-16 
 **/
class ReadFromServerThread implements Runnable {

    private Socket client;

	/**
	* @description: non-parameter constructor
	* @param: client
	*/ 	
    public ReadFromServerThread(Socket client) {
        this.client = client;
    }
    
	/**
	* @methodsName: run
	* @description: This method is used to deal with the clients' input streams.
	* @param: Nothing.
	* @return: Nothing.
	* @throws: IOException
	*/
    @Override
    public void run() {
    	String Server = "";
    	String Group = "";
    	String Friends = "";
    	String temp = "";
    	try {
            //获取客户端输入流 Get client input stream
            Scanner in=new Scanner(client.getInputStream());
            in.useDelimiter("\n");
            while(true){
                if(in.hasNext()){
                	temp = in.next();
                	if(temp.startsWith("S:")) {
                		Server+=temp+'\n';
                		ThreadClient.from.setText(Server);
                	}
                	if(temp.startsWith("G:")) {
                		Group+=temp+'\n';
                		ThreadClient.message.setText(Group);
                	}
                	if(temp.startsWith("F:")) {
                		Friends=temp;
                		ThreadClient.friends.setText(Friends.replace(".", "\nF:"));
                		System.out.println(Friends);
                	}
                	if(temp.startsWith("E:")) {
                		in.close();
                		client.close();
                		System.exit(0);
                	}
                }              
                if(client.isClosed()){
                    System.out.println("客户端已经关闭");
                    break;
                }
            }
      
            in.close();
        } catch (IOException e) {
            System.err.println("客户端读线程异常，错误为 "+e);
        }
    }
}

/**
 * @version: V1.0
 * @author: Siying Zhou
 * @className: ThreadClient
 * @packageName: chatRoom
 * @description: This class is used to send information to the server thread
 * @date: 2021-01-16 
 **/
public class ThreadClient extends JFrame{
	static JFrame jf;
	static JTextArea from;
	static JTextArea to;
	static JTextArea friends;
	static JTextArea message;
	static JPanel panel;
	static JButton btn;
	static PrintStream out;

	/**
	* @description: non-parameter constructor
	*/ 		
	ThreadClient(){
		jf = new JFrame("客户端");
		 panel = new JPanel();
		 from = new JTextArea(15, 25);
		 
	     // 设置自动换行 Set auto line wrap
		 from.setLineWrap(true);
		 
	     // 添加到内容面板 Add to the content panel
		 JLabel jl1 = new JLabel("服务器信息窗口");
	 	 panel.add(jl1);
         panel.add(from);
         from.setText("这里显示服务端发来的信息");
         JTextArea to = new JTextArea(15, 25);
         to.setLineWrap(true);
         JLabel jl2 = new JLabel("客户端发送窗口");
	     panel.add(jl2);
         to.setText("这里显示客户端发送的信息");
         panel.add(to);
        
         // 创建一个提交按钮，点击按钮获取输入文本
         // Create a submit button and click the button to get the input text
         btn = new JButton("发送");
         btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("客户端: " + to.getText());
                out.println(to.getText());
                to.setText("发送成功！");
            }		
        });
        panel.add(btn);
        friends = new JTextArea(15, 25);
        
        // 设置自动换行 Set auto line wrap
	     friends.setLineWrap(true);
	     
        // 添加到内容面板 Add to the content panel
	     JLabel jl3 = new JLabel("好友列表显示栏");
	 	panel.add(jl3);
        panel.add(friends);
        message = new JTextArea(15, 25);
        
        // 设置自动换行 Set auto line wrap
	    message.setLineWrap(true);
	    
        // 添加到内容面板 Add to the content panel
	    JLabel jl4 = new JLabel("客户端消息窗口");
	 	panel.add(jl4);
        panel.add(message);
	    jf.setContentPane(panel);
	    panel.setLayout(new FlowLayout(FlowLayout.LEADING,20,20));
		jf.setBounds(200,220,950,700);
			
		//设置窗口可见 Set window visible
		jf.setVisible(true);
	
		//设置关闭方式 如果不设置的话 似乎关闭窗口之后不会退出程序
		//Set the closing method. If not set, it seems that the program will not exit after closing the window
		jf.setDefaultCloseOperation(DISPOSE_ON_CLOSE); 
}
	
    /**
	* @methodsName: main
	* @description: This method is used to connect, receive and send information to the server.
	* @param: args
	* @return: Nothing.
	* @throws: IOException
	*/
    public static void main(String[] args) {
        try {
            //连接服务器 connect to the server
            Socket client=new Socket(InetAddress.getLocalHost(),6666);
            //读取服务器消息 Read server message
            new ThreadClient();
            Thread readFromServer = new Thread(new
                    ReadFromServerThread(client));
            //向服务器发送消息 Send a message to the server
            out=new PrintStream(client.getOutputStream());
            readFromServer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


