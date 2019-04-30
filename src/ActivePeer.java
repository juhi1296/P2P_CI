
public class ActivePeer {
	
	private String hostName;
	private Integer portNumber;
	
	public ActivePeer(String hostName, Integer portNumber) {
		this.hostName = hostName;
		this.portNumber = portNumber;
	}
	
	public String getHostName() {
		return hostName;
	}
	
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	
	public Integer getPortNumber() {
		return portNumber;
	}
	
	public void setPortNumber(Integer portNumber) {
		this.portNumber = portNumber;
	}
}
