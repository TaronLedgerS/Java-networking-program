import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.awt.*;

public class POPClientGUI extends JFrame {
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
	JButton receiveButton;
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
	
	public POPClientGUI()  {
		//���ô��ڻ�������
		setTitle("MyPOPClientGUI");
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
		receiveButton = new JButton("  Receive  ");
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
		 * �����Receive����ť��ʵ��STAT��RETR���
		 */
		receiveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				receiveEmail();
			}
		});		
		/*
		 * �����Quit����ť��ʵ��QUIT����
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
	 * �������ӣ��󶨷�������110�˿�
	 * ʵ��USER��PASS����
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
			jInfoArea.append("Server:"+in.readLine()+"\n");//��ʾ������Ϣ
			
			cmd = new StringBuilder();
			cmd.append("USER ").append(user);
			jInfoArea.append("Client:"+cmd.toString()+"/* ����������֤ */\n");
			sendCMD(cmd);
			
			cmd.append("PASS ").append(pwd);
			jInfoArea.append("Client:"+cmd.toString()+"/* �������루POP��Ȩ�룩 */\n");
			sendCMD(cmd);
			
		} catch (Exception e) {
			e.getMessage();
			e.printStackTrace();
		}
		
		
	}
	
	/*
	 * ʵ��STAT��RETR���
	 */
	protected void receiveEmail() {
		try{
			cmd.append("STAT ");
			jInfoArea.append("Client:"+cmd.toString()+"/* ��ȡ�����ͳ�����ϣ��ʼ����������ֽ�����*/\n");
			sendCMD(cmd);
			
			cmd.append("RETR ").append("1");
			jInfoArea.append("Client:"+cmd.toString()+"/* ��ȡ��һ���ʼ��ľ������ݣ���С�����ģ�*/\n");
			sendCMD(cmd);
			String mailDate ;
			while (!(mailDate = in.readLine()).equals(".")) {//ע���������
				jInfoArea.append(mailDate+"\n");
			}
			
		}
		catch (Exception e) {
			
		}
	}
	
	/*
	 * ʵ��QUIT����
	 * 1) ������������ڡ�����״̬�������롰���¡�״̬��ɾ���κα��Ϊɾ�����ʼ������ط�����֤��״̬��
	 * 2) ������������ڡ���֤��״̬��������Ự���˳�����
	 * 
	 */
	protected void quitPOPServer() {
		try {
			if(socket!=null){
				cmd.append("QUIT ");
				jInfoArea.append("Client:"+cmd.toString()+"/* �˳����� */\n");
				sendCMD(cmd);
				socket=null;
				socket.close();
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
