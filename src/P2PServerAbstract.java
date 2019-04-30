import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public abstract class P2PServerAbstract {
	
	Map<String, Integer> hostToPost;
	Map<Integer, String> rfcNumberToTitle;
	List<ActivePeer> activePeerL;
	List<RFCIndex> rfcIndexL;
	
	Socket socket;
	
	public final String VERSION = "P2P-CI/1.0";
	public final String BAD_REQUEST = "400 Bad Request";
	public final String BAD_VERSION = "505" + VERSION + "Version Not Supported";
	public final String NOT_FOUND = "404 Not Found";
	public final String OK_STATUS = VERSION + "200 OK";
	public final String EOF = "EOF";
	
	public String response(int status_code) {
		if(status_code == 0)
			return BAD_REQUEST;
		else if(status_code == -1)
			return BAD_VERSION;
		else if(status_code == 1)
			return OK_STATUS;
		else if(status_code == -2)
			return NOT_FOUND;
		else
			return new String("Error with response codes");
	}
	
	public abstract int addRFC(List<String> message, PrintWriter output);
}
