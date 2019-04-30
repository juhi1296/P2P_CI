import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class P2PClient extends P2PClientAbstract implements Runnable {

	public P2PClient(ServerSocket clientUploadServer, String clientName) {
		// TODO Auto-generated constructor stub
		this.clientName = clientName;
		this.clientUploadServer = clientUploadServer;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Socket other_client_request = null;
		
		while(true) {
			try {
				other_client_request = clientUploadServer.accept();
				BufferedReader input_from_client = new BufferedReader(new InputStreamReader(other_client_request.getInputStream()));
				PrintWriter output_to_client = new PrintWriter(other_client_request.getOutputStream(),true);
				String file = input_from_client.readLine();
				
				File dir = new File(System.getProperty("user.dir") + "/" + this.clientName);
				if(!dir.exists()) {
					dir.mkdirs();
				}
				
				boolean exist = new File(System.getProperty("user.dir")+ "/" + this.clientName + "/" + file).exists();
				if(!exist) {
					output_to_client.println("Requested RFC file doesn't exist");
					output_to_client.println(EOF);
					continue;
				}
				
				File f = new File(System.getProperty("user.dir")+ "/" + clientName + "/" + file);
				output_to_client.println("Date: "+ (new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss")).format(new Date(0)) + " GMT");
				output_to_client.println("OS: " + System.getProperty("os.name"));
				output_to_client.println("Last Modified: " + (new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss")).format(new Date(f.lastModified())) +" GMT");
				output_to_client.println("Content-Length: " + f.length() + "\nContent-Type: text/text \n");
				
				BufferedReader br = new BufferedReader(new FileReader(f));
				String input;
				while((input = br.readLine()) != null) {
					output_to_client.println(input);

				}
				
				output_to_client.println(EOF);
				if(br != null) {
					br.close();
				}
					
			}catch(IOException ex) {
				ex.printStackTrace();
			}finally {
				if(other_client_request != null)
					try {
						other_client_request.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}
	}

}
