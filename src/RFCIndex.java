
public class RFCIndex {
	
	private Integer RFCNumber;
	private String RFCTitle;
	private String RFCHostName;
	
	public RFCIndex(Integer RFCNumber, String RFCTitle, String RFCHostName) {
		this.RFCNumber = RFCNumber;
		this.RFCTitle = RFCTitle;
		this.RFCHostName = RFCHostName;
	}
	
	public Integer getRFCNumber(){
		return RFCNumber;
	}
	
	public void setRFCNumber() {
		this.RFCNumber = RFCNumber;
	}
	
	public String getRFCTitle(){
		return RFCTitle;
	}
	
	public void setRFCTitle() {
		this.RFCTitle = RFCTitle;
	}
	
	public String getRFCHostName(){
		return RFCHostName;
	}
	
	public void setRFCHostName() {
		this.RFCHostName = RFCHostName;
	}
	
	
}
