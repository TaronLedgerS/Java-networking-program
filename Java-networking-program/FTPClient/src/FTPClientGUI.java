import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;


public class FTPClientGUI extends JFrame{
	
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
	JButton listButton;
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
	int dataPort;
	
	public FTPClientGUI()  {
		//设置窗口基本参数
		setTitle("MyFTPClientGUI");
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
		
		jServerField = new JTextField("192.168.1.107",10);
		jNameField = new JTextField("ftp",15);//匿名用户
		jPwdField = new JPasswordField("",15);
		
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
		listButton = new JButton("  List  ");
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
		 * 点击“List”按钮，实现LIST命令；
		 */
		listButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				listFile();
			}
		});		
		/*
		 * 点击“Quit”按钮，实现QUIT命令
		 */
		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quitFTPServer();
			}
		});
		southPanel.add(connectButton);
		southPanel.add(listButton);
		southPanel.add(quitButton);
		return southPanel;
	}

	/*
	 * 建立连接，绑定FTP服务器的21端口
	 * 实现USER与PASS命令
	 */
	private void connectToServer() {
		server = jServerField.getText();
		user = jNameField.getText();
		pwd = new String(jPwdField.getPassword());
		
		try {
			socket = new Socket(server, 21);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			jInfoArea.append("Client :connect to "+server+"...\n");
			jInfoArea.append("Server :"+in.readLine()+"\n");//显示连接信息
			
			cmd = new StringBuilder();
			cmd.append("USER ").append(user);
			jInfoArea.append("Client :"+cmd.toString()+"/* 匿名用户登录：ftp */\n");
			sendCMD(cmd);
			
			cmd.append("PASS ").append(pwd);
			jInfoArea.append("Client :"+cmd.toString()+"/* 发送密码： 匿名用户密码为空） */\n");
			sendCMD(cmd);
			
		} catch (Exception e) {
			e.getMessage();
			e.printStackTrace();
		}
		
		
	}
	
	/*
	 * 实现PASV与LIST命令；
	 */
	protected void listFile() {
		try{
			
			cmd.append("PASV ");//响应信息为 227 entering passive mode (h1,h2,h3,h4,p1,p2) ；数据端口为 p1*256+p2，ip 地址为 h1.h2.h3.h4
			jInfoArea.append("Client :"+cmd.toString()+"/* 获取服务器的数据传输端口p1*256+p2 */\n");
			sendCMD(cmd);
	
			Socket datasocket = new Socket(server, dataPort);		

			cmd.append("LIST ");
			jInfoArea.append("Client :"+cmd.toString()+"/* 获取服务器的文件列表 */\n");
			sendCMD(cmd);

		 	BufferedReader datain = new BufferedReader(new InputStreamReader(datasocket.getInputStream()));
			String fileInfo ;
			while ((fileInfo = datain.readLine())!=null) {
				jInfoArea.append(fileInfo+"\n");
			}
			datasocket.close();
			jInfoArea.append("Server: "+in.readLine()+"\n");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 实现QUIT命令
	 */
	protected void quitFTPServer() {
		try {
			if(socket!=null){
				cmd.append("QUIT ");
				jInfoArea.append("Client :"+cmd.toString()+"/* 退出连接 */\n");
				sendCMD(cmd);
				socket.close();
				socket=null;
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
		jInfoArea.append("Server: "+respond+"\n");
		if(respond.startsWith("227 ")) setDataPort(respond);
		
	}
	//解析PASV响应传过来的数据端口
	private void setDataPort(String respond) {
		//从字符串中取出 p1和p2
		int cnt = 0, p1 = 0, p2 = 0;
		for(int i = 0; i < respond.length(); i++){
			char c = respond.charAt(i);
			if(cnt == 4 && c != ','){
				p1 = p1*10+c-'0';
			}
			if(cnt == 5 && c != ',' && c != ')'){
			//	System.out.println(c);
				p2 = p2*10+c-'0';
			}
			if(c == ','||c==')') cnt++;
		}
		//System.out.println("p1: " + p1 + " p2: " + p2);
		dataPort = p1*256+p2;
		
	}
}
