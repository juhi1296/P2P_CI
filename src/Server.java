import java.util.List;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

public class Server {
	private static List<ActivePeer> activePeerL = Collections.synchronizedList(new ArrayList<ActivePeer>());
	private static List<RFCIndex> rfcIndexL = Collections.synchronizedList(new ArrayList<RFCIndex>());
	private static final int PORT_NUMBER = 7734;
	
	public static void main(String args[]) throws Exception{
		ServerSocket accept_socket = new ServerSocket(PORT_NUMBER);
		try {
			while(true) {
				Socket socket = accept_socket.accept();
				Runnable p2pServer = new P2PServer(activePeerL,rfcIndexL,socket);
				Thread thread = new Thread(p2pServer);
				thread.start();
				
			}
		}catch(Exception ex) {
			throw new Exception(ex.getMessage());
		}finally {
			accept_socket.close();
		}
	}
}