import java.io.IOException;
import java.net.Socket;


public class PortScannerByIP {
    
    public static boolean scan(String host,int port){
        boolean flag=true;
        Socket socket=null;
        try {
            socket=new Socket(host,port);
            return flag;
        } catch (IOException e) {
            flag=false;
            return flag;
        }finally{
            try {
                if(socket!=null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}