import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class P2PServer extends P2PServerAbstract implements Runnable {

	public P2PServer(List<ActivePeer> activePeerL, List<RFCIndex> rfcIndexL, Socket socket) {
		this.activePeerL = activePeerL;
		this.rfcIndexL = rfcIndexL;
		this.socket = socket;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter pw = new PrintWriter(socket.getOutputStream(),true);
			
			String input;
			List<String> message = new ArrayList<>();
			message.add(br.readLine());
			message.add(br.readLine());
			
			
			// provide hostname and port number of client
			String hostName = message.get(0);
			int portNumber = Integer.valueOf(message.get(1));
			ActivePeer ap = new ActivePeer(hostName,portNumber);
			activePeerL.add(ap);
			pw.println(ap.getHostName() + " added to the active list");
			pw.println("ENTER ADD, LIST ALL, LOOKUP, GET or END requests");
			
			while(true) {
				message = new ArrayList<>();
				input = br.readLine();
				if(input.startsWith("ADD")) {
					message.add(input); //ADD RFC 123 P2P-CI/1.0
					message.add(br.readLine());
					message.add(br.readLine());
					message.add(br.readLine());
					
					System.out.println("*****Add Request From Peer*****");
					
					for(int i = 0; i < message.size(); i++) {
						System.out.println(message.get(i));
					}
					
					int code = addRFC(message,pw);
					if(code != 1) {
						pw.println(response(code));
					}
					pw.println(EOF);
				}
				else if(input.startsWith("LIST ALL")) {
					message.add(input); // LIST ALL P2P-CI/1
					
					System.out.println("*****List All Requests From Peer*****");
					for(int i = 0; i < message.size(); i++) {
						System.out.println(message.get(i));
					}
					
					pw.println(rfcIndexL.size());
					
					for(RFCIndex rfci: rfcIndexL) {
						for(ActivePeer apeer: activePeerL) {
							if(apeer.getHostName().equals(rfci.getRFCHostName())) {
								pw.println(rfci.getRFCNumber() + " " + rfci.getRFCTitle() + " " + rfci.getRFCHostName() + " " + apeer.getPortNumber());
								
							}
						}
					}
				}
				else if(input.startsWith("GET")) {
					System.out.println("*****GET request from peer*****");
					message.add(input);
					message.add(br.readLine());
					message.add(br.readLine());
					String clientRequestHostName = message.get(1).split(" ")[1];
					Integer RFCNumber = Integer.valueOf(message.get(2));
					Integer portN = null;
					for(RFCIndex rfci : rfcIndexL) {
						for(ActivePeer apeer: activePeerL) {
							if(rfci.getRFCHostName().equals(clientRequestHostName) && apeer.getHostName().equals(rfci.getRFCHostName()) && RFCNumber.equals(rfci.getRFCNumber())) {
								portN = apeer.getPortNumber();
							}
						}
					}
					
					pw.println(String.valueOf(portN));
				}
				else if(input.startsWith("LOOKUP")) {
					int code = -2; //not found 
					Integer portN = null;
					Set<String> set = new HashSet<>();
					message.add(input); //LOOKUP RFC 123 P2P-CI/1
					message.add(br.readLine());
					message.add(br.readLine());
					
					System.out.println("*****Lookup Request From Peer*****");
					for(int i = 0; i < message.size(); i++) {
						System.out.println(message.get(i));
					}
					
					Integer clientRequestRFCNumber = Integer.valueOf(message.get(0).split(" ")[2]);
					String clientRequestHostName = message.get(1).split(" ")[1];
					String clientRequestTitle = message.get(2).split(": ")[1];
					
					for(RFCIndex rfci: rfcIndexL) {
						for(ActivePeer apeer: activePeerL) {
							if(apeer.getHostName().equals(rfci.getRFCHostName()) && clientRequestRFCNumber.equals(rfci.getRFCNumber())) {
								portN = apeer.getPortNumber();
							}
						}
						
						set.add("Host: " + rfci.getRFCHostName() + "\nPort: " + String.valueOf(portN));
						code = 1;
					}
					
					if(code == 1 && portN != null) {
						for(String str: set) {
							pw.println(str);
						}
					}
					else if(portN == null) {
						pw.println("No such entry found");
					}else {
						pw.println(response(code));
					}
					
					pw.println(EOF);
				}
				else if(input.startsWith("END")) {
					String hostname = br.readLine();
					if(activePeerL != null) {
						Iterator<ActivePeer> iterator = activePeerL.iterator();
						while(iterator.hasNext()) {
							if(iterator.next().getHostName().equals(hostname)) {
								iterator.remove();
							}
						}
					}
					if(rfcIndexL != null) {
						Iterator<RFCIndex> iterator = rfcIndexL.iterator();
						while(iterator.hasNext()) {
							if(iterator.next().getRFCHostName().equals(hostname)) {
								iterator.remove();
							}
						}
					}
					
					System.out.println("*****End Request From Peer. Connection Closed From Peer*****");
					pw.println("Connection Closed With Server");
					pw.println(EOF);
					socket.close();
					break;
				}
				else {
					System.out.println("*****Invalid Request*****");
					pw.println("Invalid Request");
				}
				
				pw.flush();
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}

	}

	@Override
	public int addRFC(List<String> message, PrintWriter output) {
		// TODO Auto-generated method stub
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(socket.getOutputStream(),true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Integer RFCNumber = null;
		Integer portN = null;
		String title = null;
		String RFCHostName = null;
		
		for(int i = 0; i < message.size(); i++) {
			String input = message.get(i);
			if(input.startsWith("ADD")) {
				String s[] = input.split(" ");
				try {
					if(s[1].equals("RFC")) {
						RFCNumber = Integer.parseInt(s[2]);
					}
					if(!s[1].equals("RFC")) {
						return 0;
					}
					if(!s[3].equals(VERSION)) {
						return -1;
					}
				}catch(Exception ex) {
					return 0;
				}
			}else if(input.startsWith("Host")) {
				RFCHostName = input.split(" ")[1];
			}else if(input.startsWith("Port")) {
				try {
					portN = Integer.parseInt(input.split(" ")[1]);
				}catch(Exception ex) {
					return 0;
				}
			}else if(input.startsWith("Title")) {
				title = input.split(": ")[1];
			}else {
				return 0;
			}
		}
		
		RFCIndex rfci = new RFCIndex(RFCNumber,title,RFCHostName);
		rfcIndexL.add(rfci);
		pw.println(response(1) + "\nRFC " + RFCNumber + " " + title + " " + RFCHostName + " " + portN);
		return 1;
	
	}

	
}
