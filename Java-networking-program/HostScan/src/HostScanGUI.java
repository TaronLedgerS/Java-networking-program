import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.awt.*;

public class HostScanGUI extends JFrame {
	/*
	 * ���û�������
	 */
	private static final long serialVersionUID = 1L;
	Toolkit kit = Toolkit.getDefaultToolkit();
	Dimension screenSize = kit.getScreenSize();
	int screenHeight = screenSize.height;
	int screenWidth = screenSize.width;
	private static final int DEFAULTWIDTH = 450;
	private static final int DEFAULTHEIGHT = 350;
	
	/*
	 * �������
	 */
	JTextField jHostField;
	JTextField jStatusField;
	JTextArea jInfoArea ;
	JButton scanButton;
	
	/*
	 * ������Ϣ
	 */
	String host ;
	boolean status;
	
	public HostScanGUI()  {
		//���ô��ڻ�������
		setTitle("MyHostScanGUI");
		setSize(DEFAULTWIDTH, DEFAULTHEIGHT);
		setLocation((screenWidth - DEFAULTWIDTH) / 2, (screenHeight - DEFAULTHEIGHT) / 2);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//���ò���
		JPanel northPanel = initNorthPanel();
		JPanel mainPanel = initMainPanel();
		getContentPane().add(BorderLayout.NORTH,northPanel);
		getContentPane().add(BorderLayout.CENTER,mainPanel);

		setVisible(true);
	}
	
	/*
	 * ������������ɨ�谴����״̬��ʾ 
	 */
	private JPanel initNorthPanel() {
		JPanel  northPanel  = new JPanel();
		JLabel  jLabel1 = new JLabel("�� �� :");	
		JLabel  jLabel2 = new JLabel("״ ̬ :");	
		jHostField = new JTextField("127.0.0.1",10);
		jStatusField = new JTextField("",10);
		jStatusField.setEditable(false);
		
		scanButton = new JButton("ɨ ��");
		scanButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				jStatusField.setText("����ɨ����...");
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
	 * ��Ϣ��ʾ��
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
	 * scan������PING
	 */
	
	protected void scan() {
		host = jHostField.getText();
		Runtime runtime = Runtime.getRuntime();// ��ȡ��ǰ��������н�����
		Process process = null; // �������������
		BufferedReader in = null; //������Ϣ��
		String line = null; // ��������Ϣ
		status = false;
		try {
			process = runtime.exec("ping "+host);//ping����
			in =new BufferedReader( new InputStreamReader(process.getInputStream()));//�ֽ�ת���ַ���
			while ((line = in.readLine()) != null) {
				jInfoArea.append(line+"\n");
			    if (line.contains("TTL")) {
			    	status = true;
			    }
			}
			in.close();
			if (status) {
				jStatusField.setText("Ŀ����������");
			}
			else{
				jStatusField.setText("Ŀ�������ر�");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
