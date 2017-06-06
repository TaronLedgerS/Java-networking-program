import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;


public class WebBrowserGUI extends JFrame{
	
	private static final long serialVersionUID = 1L;
	//获取屏幕大小
	Toolkit kit = Toolkit.getDefaultToolkit();
	Dimension screenSize = kit.getScreenSize();
	int screenHeight = screenSize.height;
	int screenWidth = screenSize.width;
	private static final int DEFAULTWIDTH = 800;
	private static final int DEFAULTHEIGHT = 1000;
	
	JTextArea documentTextArea;
	JTextField sendArea;
	JButton browserButton ;
	
	public WebBrowserGUI() {
		//设置窗口基本参数
		setTitle("MyWebBrowserGUI");
		setSize(DEFAULTWIDTH, DEFAULTHEIGHT);
		setLocation((screenWidth - DEFAULTWIDTH) / 2, (screenHeight - DEFAULTHEIGHT) / 2);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel mainPanel = initMainPanel();
		JPanel northPanel = initNorthPanel();
		getContentPane().add(BorderLayout.CENTER,mainPanel);
		getContentPane().add(BorderLayout.NORTH,northPanel);
		
		setVisible(true);
	}
	/*
	 * 网址跳转区
	 */
	private JPanel initNorthPanel() {
		JPanel southPanel = new JPanel();
		sendArea = new JTextField(60);
		browserButton = new JButton("Browser");
		browserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev){
				URL url;
				try {
					documentTextArea.setText(" ");
					//建立连接 
					url  = new URL(sendArea.getText());
					String host = url.getHost();
					String path = url.getPath(); 
					int port = 80;
					SocketAddress dest = new InetSocketAddress(host, port);
					Socket s = new Socket();
					
					System.out.println("Connecting to " + host + " on port " + port);
					s.connect(dest,1000);//超时时间10秒
					System.out.println("Just connected to " + s.getRemoteSocketAddress());
					
					//发送GET请求
					System.out.println("GET " + path + " HTTP/1.1\r\n");
					System.out.println("Host: " + host + "\r\n");
					BufferedWriter sendOutRequest = new BufferedWriter(new OutputStreamWriter(s.getOutputStream())); 
					sendOutRequest.write("GET " + url.getPath() + " HTTP/1.1\r\n");  
					sendOutRequest.write("Host: " + host + "\r\n");  
					sendOutRequest.write("\r\n");  
					sendOutRequest.flush(); 
					
					//从服务器接收信息 
			        BufferedReader getMessage = new BufferedReader(new InputStreamReader(s.getInputStream()));  
			        //存到文档中去
			        BufferedWriter writerToFile = new BufferedWriter(new FileWriter("data.html"));
				    String line;
				    while ((line = getMessage.readLine()) != null) {
				    	documentTextArea.append(line+"\n");
				    	writerToFile.write(line);
				    	writerToFile.newLine();
				    }
				    getMessage.close();
				    writerToFile.close();
				    s.close();
				    System.out.println("Done!");
				} catch (Exception e) {
					e.printStackTrace();
				}
			    
			}
		});
		southPanel.add(sendArea);
		southPanel.add(browserButton);
		return southPanel;
	}

	/*
	 * 浏览器显示区
	 */
	private JPanel initMainPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		documentTextArea = new JTextArea();
		documentTextArea.setLineWrap(true);
		documentTextArea.setEditable(false);

		JScrollPane qScroller = new JScrollPane(documentTextArea);
		qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		mainPanel.add(qScroller,BorderLayout.CENTER);
		return mainPanel;
	}

	
}
