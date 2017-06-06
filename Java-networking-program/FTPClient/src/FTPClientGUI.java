import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;


public class FTPClientGUI extends JFrame{
	
	/*
	 * ���û�������
	 */
	private static final long serialVersionUID = 1L;
	Toolkit kit = Toolkit.getDefaultToolkit();
	Dimension screenSize = kit.getScreenSize();
	int screenHeight = screenSize.height;
	int screenWidth = screenSize.width;
	private static final int DEFAULTWIDTH = 700;
	private static final int DEFAULTHEIGHT = 500;
	
	/*
	 * �������
	 */
	JTextField jServerField;
	JTextField jNameField;
	JPasswordField jPwdField;
	JTextArea jInfoArea ;
	JButton connectButton;
	JButton listButton;
	JButton quitButton;
	
	/*
	 * ������Ϣ
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
		//���ô��ڻ�������
		setTitle("MyFTPClientGUI");
		setSize(DEFAULTWIDTH, DEFAULTHEIGHT);
		setLocation((screenWidth - DEFAULTWIDTH) / 2, (screenHeight - DEFAULTHEIGHT) / 2);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//���ò���
		JPanel northPanel = initNorthPanel();
		JPanel mainPanel = initMainPanel();
		JPanel southPanel = initSouthPanel();
		getContentPane().add(BorderLayout.NORTH,northPanel);
		getContentPane().add(BorderLayout.CENTER,mainPanel);
		getContentPane().add(BorderLayout.SOUTH,southPanel);
		
		setVisible(true);
	}
	
	/*
	 * ���������û��������������� 
	 */
	private JPanel initNorthPanel() {
		JPanel  northPanel  = new JPanel();
		JLabel  jLabel1 = new JLabel("������:");
		JLabel  jLabel2 = new JLabel("�û���:");
		JLabel 	jLabel3 = new JLabel("��  ��:");
		
		jServerField = new JTextField("192.168.1.107",10);
		jNameField = new JTextField("ftp",15);//�����û�
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
	 * ��Ϣ������
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
	 * ������ 
	 */
	private JPanel initSouthPanel() {
		JPanel southPanel = new JPanel();
		connectButton = new JButton("  Connect  ");
		listButton = new JButton("  List  ");
		quitButton = new JButton("   Quit     ");
		/*
		 * �����Connect����ť��ʵ��USER��PASS���
		 */
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				connectToServer();
			}
		} );
		/*
		 * �����List����ť��ʵ��LIST���
		 */
		listButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				listFile();
			}
		});		
		/*
		 * �����Quit����ť��ʵ��QUIT����
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
	 * �������ӣ���FTP��������21�˿�
	 * ʵ��USER��PASS����
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
			jInfoArea.append("Server :"+in.readLine()+"\n");//��ʾ������Ϣ
			
			cmd = new StringBuilder();
			cmd.append("USER ").append(user);
			jInfoArea.append("Client :"+cmd.toString()+"/* �����û���¼��ftp */\n");
			sendCMD(cmd);
			
			cmd.append("PASS ").append(pwd);
			jInfoArea.append("Client :"+cmd.toString()+"/* �������룺 �����û�����Ϊ�գ� */\n");
			sendCMD(cmd);
			
		} catch (Exception e) {
			e.getMessage();
			e.printStackTrace();
		}
		
		
	}
	
	/*
	 * ʵ��PASV��LIST���
	 */
	protected void listFile() {
		try{
			
			cmd.append("PASV ");//��Ӧ��ϢΪ 227 entering passive mode (h1,h2,h3,h4,p1,p2) �����ݶ˿�Ϊ p1*256+p2��ip ��ַΪ h1.h2.h3.h4
			jInfoArea.append("Client :"+cmd.toString()+"/* ��ȡ�����������ݴ���˿�p1*256+p2 */\n");
			sendCMD(cmd);
	
			Socket datasocket = new Socket(server, dataPort);		

			cmd.append("LIST ");
			jInfoArea.append("Client :"+cmd.toString()+"/* ��ȡ���������ļ��б� */\n");
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
	 * ʵ��QUIT����
	 */
	protected void quitFTPServer() {
		try {
			if(socket!=null){
				cmd.append("QUIT ");
				jInfoArea.append("Client :"+cmd.toString()+"/* �˳����� */\n");
				sendCMD(cmd);
				socket.close();
				socket=null;
			}
			jInfoArea.append("Socket has been closed\n");
		} catch (Exception e) {
			
		}
		
	}
	/*
	 * �����������ָ���ȡ����������Ӧ
	 */
	private void sendCMD(StringBuilder cmd2)throws IOException {
		//����ָ��
		out.write(cmd.toString());
		out.newLine();
		out.flush();
		cmd.delete(0, cmd.length());//�������
		//������Ӧ
		String respond = in.readLine();
		jInfoArea.append("Server: "+respond+"\n");
		if(respond.startsWith("227 ")) setDataPort(respond);
		
	}
	//����PASV��Ӧ�����������ݶ˿�
	private void setDataPort(String respond) {
		//���ַ�����ȡ�� p1��p2
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
