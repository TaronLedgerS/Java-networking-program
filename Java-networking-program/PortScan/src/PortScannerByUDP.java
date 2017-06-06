import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class PortScannerByUDP {
	public static boolean scan(String host,int port){
		 boolean flag=true;
	        DatagramSocket socket=null;
	        /*��UDPɨ�豾���Ķ˿�1024-3000
	        for (int port2 = 1024; port2 <= 3000; port2++)
            {
                try
                {
                    // ����ڶ˿������з��񣬾Ͳ����쳣��
                    //�½�DatagramSocket����
                    DatagramSocket server = new DatagramSocket(port2);
                    //�رշ���
                    server.close();
                }
                catch (Exception e)
                {
                    System.out.println("�� " + port2 + "�˿������з���");
                }
            }*/
	        
	        try {
	        	InetAddress newhost = InetAddress.getByName(host);
	            socket=new DatagramSocket(port,newhost );
	            return flag;
	        } catch (IOException e) {
	            flag=false;
	            return flag;
	        }finally{
	            if(socket!=null)
				    socket.close();
	        }


	}
}
