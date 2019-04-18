import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client extends P2PClientAbstract {
	
	private static String SERVER_ADDRESS;
	private static final int PORT_NUMBER  = 7734;
	
	public static void main(String args[]) throws Exception{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter Server Address");
		SERVER_ADDRESS = br.readLine();
		Socket client_to_server_conn = new Socket(SERVER_ADDRESS, PORT_NUMBER);
		BufferedReader input_server = new BufferedReader(new InputStreamReader(client_to_server_conn.getInputStream()));
		PrintWriter output_server = new PrintWriter(client_to_server_conn.getOutputStream(), true);
		
		System.out.println("Enter host name of client");
		String hostname = br.readLine();
		System.out.println("Enter upload port for client");
		Integer uploadPort = Integer.valueOf(br.readLine());
		ServerSocket clientUploadServer = new ServerSocket(uploadPort);
		Runnable p2pclient = new P2PClient(clientUploadServer,hostname);
		Thread thread = new Thread(p2pclient);
		thread.start();
		
		output_server.println(hostname);
		output_server.println(uploadPort);
		
		System.out.println(input_server.readLine());
		System.out.println(input_server.readLine());
		
		while(true) {
			output_server.flush();
			String input = br.readLine();
			if(input.startsWith("ADD")) {
				List<String> message = new ArrayList<>();
				message.add(input);
				message.add(br.readLine());
				message.add(br.readLine());
				message.add(br.readLine());
				
				output_server.println(message.get(0));
				output_server.println(message.get(1));
				output_server.println(message.get(2));
				output_server.println(message.get(3));
				
				String s;
				
				while (!(s = input_server.readLine()).equals(EOF))
					System.out.println(s);
			}
			else if(input.startsWith("LIST ALL") && input.split(" ").length == 3) {
				String message = input.split(" ")[2];
				if(!message.equals(VERSION)) {
					System.out.println(response(-1));
					continue;
				}
				
				System.out.println(response(1));
				output_server.println(input);
				Integer count = Integer.valueOf(input_server.readLine());
				if(count > 0) {
					for(int i = 0; i < count; i++) {
						System.out.println(input_server.readLine());
					}
				}
				else
					System.out.println("No Entries Found");
			}
			else if(input.startsWith("LOOKUP")) {
				output_server.println(input);
				output_server.println(br.readLine());
				output_server.println(br.readLine());
				
				if(!input.split(" ")[3].equals(VERSION)) {
					System.out.println(response(-1));
					continue;
				}
				
				System.out.println(response(1));
				String s;
				while (!(s = input_server.readLine()).equals(EOF))
					System.out.println(s);
				
			}
			else if(input.startsWith("GET")) {
				List<String> message = new ArrayList<>();
				message.add(input);
				message.add(br.readLine());
				message.add(br.readLine());
				message.add(br.readLine());
				
				if(!message.get(0).split(" ")[3].equals(VERSION)) {
					System.out.println(response(-1));
					continue;
				}
				
				System.out.println(response(1));
				Integer RFCNumber = Integer.valueOf(message.get(0).split(" ")[2]);
				output_server.println(input);
				
				output_server.println(message.get(1));
				output_server.println(RFCNumber);
				String p1 = input_server.readLine();
				
				if(p1 == null || p1.equals("")) {
					System.out.println("Port number not found in RFC list");
					continue;
				}
				else {
					Socket client_client_request = new Socket(message.get(1).split(" ")[1], Integer.valueOf(p1));
					BufferedReader input_from_client = new BufferedReader(new InputStreamReader(client_client_request.getInputStream()));
					PrintWriter output_to_client = new PrintWriter(client_client_request.getOutputStream(), true); 
					
					String filename = RFCNumber + ".txt"; 
					output_to_client.println(filename);
							
					File dir = new File(System.getProperty("user.dir") + "/" + hostname);
						if (!dir.exists()) 
							dir.mkdirs();
					
					File output_file = new File(System.getProperty("user.dir") + "/" + hostname + "/" + filename);
		            PrintWriter pw = new PrintWriter(output_file);
		            System.out.println(input_from_client.readLine());
		            System.out.println(input_from_client.readLine());
		            System.out.println(input_from_client.readLine());
		            System.out.println(input_from_client.readLine());
		            
		            String s;
		            
		            while (!(s = input_from_client.readLine()).equals(EOF))
		            	pw.println(s);
		            
		            if(pw != null)
		            	pw.close();
		            
		            System.out.println("File downloaded in directory: " + System.getProperty("user.dir") + "/" + hostname + "/" + filename);
		            
		            String first = message.get(0).replace("GET", "ADD");
					String second = new String("Host: " + hostname);
					String third = new String("Port: " + uploadPort);
					String forth = message.get(3);
					message = new ArrayList<>();
					
					message.add(first);
					message.add(second);
					message.add(third);
					message.add(forth);
					
					output_server.println(message.get(0));
					output_server.println(message.get(1));
					output_server.println(message.get(2));
					output_server.println(message.get(3));
					String i;
					while (!(i = input_server.readLine()).equals(EOF))
						System.out.println(i);
					
					client_client_request.close();
				}
				
			}
			else if(input.startsWith("END")) {
				output_server.println(input);
				output_server.println(hostname);
				String s;
				
				while (!(s = input_server.readLine()).equals(EOF))
					System.out.println(s);
				
				if(client_to_server_conn != null)
					client_to_server_conn.close();
				System.exit(1);
			}
			else {
				System.out.println("Invalid Command! Try Again.");
			}
		}
		
		
	}
}
