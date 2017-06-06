import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.awt.*;

public class HostScanGUI extends JFrame {
	/*
	 * 设置基本参数
	 */
	private static final long serialVersionUID = 1L;
	Toolkit kit = Toolkit.getDefaultToolkit();
	Dimension screenSize = kit.getScreenSize();
	int screenHeight = screenSize.height;
	int screenWidth = screenSize.width;
	private static final int DEFAULTWIDTH = 450;
	private static final int DEFAULTHEIGHT = 350;
	
	/*
	 * 基本组件
	 */
	JTextField jHostField;
	JTextField jStatusField;
	JTextArea jInfoArea ;
	JButton scanButton;
	
	/*
	 * 基本信息
	 */
	String host ;
	boolean status;
	
	public HostScanGUI()  {
		//设置窗口基本参数
		setTitle("MyHostScanGUI");
		setSize(DEFAULTWIDTH, DEFAULTHEIGHT);
		setLocation((screenWidth - DEFAULTWIDTH) / 2, (screenHeight - DEFAULTHEIGHT) / 2);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//设置布局
		JPanel northPanel = initNorthPanel();
		JPanel mainPanel = initMainPanel();
		getContentPane().add(BorderLayout.NORTH,northPanel);
		getContentPane().add(BorderLayout.CENTER,mainPanel);

		setVisible(true);
	}
	
	/*
	 * 主机输入区、扫描按键、状态显示 
	 */
	private JPanel initNorthPanel() {
		JPanel  northPanel  = new JPanel();
		JLabel  jLabel1 = new JLabel("主 机 :");	
		JLabel  jLabel2 = new JLabel("状 态 :");	
		jHostField = new JTextField("127.0.0.1",10);
		jStatusField = new JTextField("",10);
		jStatusField.setEditable(false);
		
		scanButton = new JButton("扫 描");
		scanButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				jStatusField.setText("正在扫描中...");
				scan();
			}
		});
		
		northPanel.add(jLabel1);
		northPanel.add(jHostField);
		northPanel.add(scanButton);
		northPanel.add(jLabel2);
		northPanel.add(jStatusField);
		
		return northPanel;
	}


	/*
	 * 信息显示区
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
	 * scan操作：PING
	 */
	
	protected void scan() {
		host = jHostField.getText();
		Runtime runtime = Runtime.getRuntime();// 获取当前程序的运行进对象
		Process process = null; // 声明处理类对象
		BufferedReader in = null; //返回信息流
		String line = null; // 返回行信息
		status = false;
		try {
			process = runtime.exec("ping "+host);//ping操作
			in =new BufferedReader( new InputStreamReader(process.getInputStream()));//字节转换字符流
			while ((line = in.readLine()) != null) {
				jInfoArea.append(line+"\n");
			    if (line.contains("TTL")) {
			    	status = true;
			    }
			}
			in.close();
			if (status) {
				jStatusField.setText("目标主机开放");
			}
			else{
				jStatusField.setText("目标主机关闭");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
