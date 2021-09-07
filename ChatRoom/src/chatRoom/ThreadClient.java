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
            //��ȡ�ͻ��������� Get client input stream
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
                    System.out.println("�ͻ����Ѿ��ر�");
                    break;
                }
            }
      
            in.close();
        } catch (IOException e) {
            System.err.println("�ͻ��˶��߳��쳣������Ϊ "+e);
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
		jf = new JFrame("�ͻ���");
		 panel = new JPanel();
		 from = new JTextArea(15, 25);
		 
	     // �����Զ����� Set auto line wrap
		 from.setLineWrap(true);
		 
	     // ��ӵ�������� Add to the content panel
		 JLabel jl1 = new JLabel("��������Ϣ����");
	 	 panel.add(jl1);
         panel.add(from);
         from.setText("������ʾ����˷�������Ϣ");
         JTextArea to = new JTextArea(15, 25);
         to.setLineWrap(true);
         JLabel jl2 = new JLabel("�ͻ��˷��ʹ���");
	     panel.add(jl2);
         to.setText("������ʾ�ͻ��˷��͵���Ϣ");
         panel.add(to);
        
         // ����һ���ύ��ť�������ť��ȡ�����ı�
         // Create a submit button and click the button to get the input text
         btn = new JButton("����");
         btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("�ͻ���: " + to.getText());
                out.println(to.getText());
                to.setText("���ͳɹ���");
            }		
        });
        panel.add(btn);
        friends = new JTextArea(15, 25);
        
        // �����Զ����� Set auto line wrap
	     friends.setLineWrap(true);
	     
        // ��ӵ�������� Add to the content panel
	     JLabel jl3 = new JLabel("�����б���ʾ��");
	 	panel.add(jl3);
        panel.add(friends);
        message = new JTextArea(15, 25);
        
        // �����Զ����� Set auto line wrap
	    message.setLineWrap(true);
	    
        // ��ӵ�������� Add to the content panel
	    JLabel jl4 = new JLabel("�ͻ�����Ϣ����");
	 	panel.add(jl4);
        panel.add(message);
	    jf.setContentPane(panel);
	    panel.setLayout(new FlowLayout(FlowLayout.LEADING,20,20));
		jf.setBounds(200,220,950,700);
			
		//���ô��ڿɼ� Set window visible
		jf.setVisible(true);
	
		//���ùرշ�ʽ ��������õĻ� �ƺ��رմ���֮�󲻻��˳�����
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
            //���ӷ����� connect to the server
            Socket client=new Socket(InetAddress.getLocalHost(),6666);
            //��ȡ��������Ϣ Read server message
            new ThreadClient();
            Thread readFromServer = new Thread(new
                    ReadFromServerThread(client));
            //�������������Ϣ Send a message to the server
            out=new PrintStream(client.getOutputStream());
            readFromServer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


