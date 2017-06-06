import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.awt.*;

public class POPClientGUI extends JFrame {
	/*
	 * 设置基本参数
	 */
	private static final long serialVersionUID = 1L;
	Toolkit kit = Toolkit.getDefaultToolkit();
	Dimension screenSize = kit.getScreenSize();
	int screenHeight = screenSize.height;
	int screenWidth = screenSize.width;
	private static final int DEFAULTWIDTH = 700;
	private static final int DEFAULTHEIGHT = 500;
	
	/*
	 * 基本组件
	 */
	JTextField jServerField;
	JTextField jNameField;
	JPasswordField jPwdField;
	JTextArea jInfoArea ;
	JButton connectButton;
	JButton receiveButton;
	JButton quitButton;
	
	/*
	 * 基本信息
	 */
	String server ;
	String user ;
	String pwd ;
	Socket socket;
	BufferedReader in ;
	BufferedWriter out;
	StringBuilder cmd;
	
	public POPClientGUI()  {
		//设置窗口基本参数
		setTitle("MyPOPClientGUI");
		setSize(DEFAULTWIDTH, DEFAULTHEIGHT);
		setLocation((screenWidth - DEFAULTWIDTH) / 2, (screenHeight - DEFAULTHEIGHT) / 2);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//设置布局
		JPanel northPanel = initNorthPanel();
		JPanel mainPanel = initMainPanel();
		JPanel southPanel = initSouthPanel();
		getContentPane().add(BorderLayout.NORTH,northPanel);
		getContentPane().add(BorderLayout.CENTER,mainPanel);
		getContentPane().add(BorderLayout.SOUTH,southPanel);
		
		setVisible(true);
	}
	
	/*
	 * 服务器、用户名、密码输入区 
	 */
	private JPanel initNorthPanel() {
		JPanel  northPanel  = new JPanel();
		JLabel  jLabel1 = new JLabel("服务器:");
		JLabel  jLabel2 = new JLabel("用户名:");
		JLabel 	jLabel3 = new JLabel("密  码:");
		
		jServerField = new JTextField("pop3.163.com",10);
		jNameField = new JTextField("qq605917080@163.com",15);
		jPwdField = new JPasswordField("pop123456",15);
		
		northPanel.add(jLabel1);
		northPanel.add(jServerField);
		northPanel.add(jLabel2);
		northPanel.add(jNameField);
		northPanel.add(jLabel3);
		northPanel.add(jPwdField);
		
		return northPanel;
	}
	/*
	 * 信息接收区
	 */
	private JPanel initMainPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		jInfoArea  = new JTextArea();
		jInfoArea .setLineWrap(true);
		jInfoArea .setEditable(false);

		JScrollPane qScroller = new JScrollPane(jInfoArea);
		qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		mainPanel.add(qScroller,BorderLayout.CENTER);
		return mainPanel;
	}
	
	/*
	 * 操作区 
	 */
	private JPanel initSouthPanel() {
		JPanel southPanel = new JPanel();
		connectButton = new JButton("  Connect  ");
		receiveButton = new JButton("  Receive  ");
		quitButton = new JButton("   Quit     ");
		/*
		 * 点击“Connect”按钮，实现USER与PASS命令；
		 */
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				connectToServer();
			}
		} );
		/*
		 * 点击“Receive”按钮，实现STAT与RETR命令；
		 */
		receiveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				receiveEmail();
			}
		});		
		/*
		 * 点击“Quit”按钮，实现QUIT命令
		 */
		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quitPOPServer();
			}
		});
		southPanel.add(connectButton);
		southPanel.add(receiveButton);
		southPanel.add(quitButton);
		return southPanel;
	}

	/*
	 * 建立连接，绑定服务器的110端口
	 * 实现USER与PASS命令
	 */
	private void connectToServer() {
		server = jServerField.getText();
		user = jNameField.getText();
		pwd = new String(jPwdField.getPassword());
		
		try {
			socket = new Socket(server, 110);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			jInfoArea.append("Client:connect to "+server+"...\n");
			jInfoArea.append("Server:"+in.readLine()+"\n");//显示连接信息
			
			cmd = new StringBuilder();
			cmd.append("USER ").append(user);
			jInfoArea.append("Client:"+cmd.toString()+"/* 采用明文认证 */\n");
			sendCMD(cmd);
			
			cmd.append("PASS ").append(pwd);
			jInfoArea.append("Client:"+cmd.toString()+"/* 发送密码（POP授权码） */\n");
			sendCMD(cmd);
			
		} catch (Exception e) {
			e.getMessage();
			e.printStackTrace();
		}
		
		
	}
	
	/*
	 * 实现STAT与RETR命令；
	 */
	protected void receiveEmail() {
		try{
			cmd.append("STAT ");
			jInfoArea.append("Client:"+cmd.toString()+"/* 获取邮箱的统计资料（邮件总数和总字节数）*/\n");
			sendCMD(cmd);
			
			cmd.append("RETR ").append("1");
			jInfoArea.append("Client:"+cmd.toString()+"/* 获取第一封邮件的具体内容（大小和正文）*/\n");
			sendCMD(cmd);
			String mailDate ;
			while (!(mailDate = in.readLine()).equals(".")) {//注意结束条件
				jInfoArea.append(mailDate+"\n");
			}
			
		}
		catch (Exception e) {
			
		}
	}
	
	/*
	 * 实现QUIT命令
	 * 1) 如果服务器处于“处理”状态，将进入“更新”状态以删除任何标记为删除的邮件，并重返“认证”状态。
	 * 2) 如果服务器处于“认证”状态，则结束会话，退出连接
	 * 
	 */
	protected void quitPOPServer() {
		try {
			if(socket!=null){
				cmd.append("QUIT ");
				jInfoArea.append("Client:"+cmd.toString()+"/* 退出连接 */\n");
				sendCMD(cmd);
				socket=null;
				socket.close();
			}
			jInfoArea.append("Socket has been closed\n");
		} catch (Exception e) {
			
		}
		
	}
	/*
	 * 向服务器发送指令并获取服务器的响应
	 */
	private void sendCMD(StringBuilder cmd2)throws IOException {
		//发送指令
		out.write(cmd.toString());
		out.newLine();
		out.flush();
		cmd.delete(0, cmd.length());//清空命令
		//接收响应
		String respond = in.readLine();
		if (respond.startsWith("+OK")) {
			jInfoArea.append("Server:"+respond+"\n");
		}
		else {
			 jInfoArea.append("Server Error:"+ respond+"\n");
			 jInfoArea.append("Close socket\n");
			 socket.close();
		}
		
	}

}
