import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class PortScannerByUDP {
	public static boolean scan(String host,int port){
		 boolean flag=true;
	        DatagramSocket socket=null;
	        /*用UDP扫描本机的端口1024-3000
	        for (int port2 = 1024; port2 <= 3000; port2++)
            {
                try
                {
                    // 如果在端口上已有服务，就捕获异常。
                    //新建DatagramSocket对象
                    DatagramSocket server = new DatagramSocket(port2);
                    //关闭服务
                    server.close();
                }
                catch (Exception e)
                {
                    System.out.println("在 " + port2 + "端口上已有服务。");
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
