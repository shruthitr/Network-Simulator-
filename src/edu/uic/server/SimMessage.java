package edu.uic.server;

public class SimMessage {
	private String source;
	private String destination;
	private int applicationNumber;
	private int size;
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public int getApplicationNumber() {
		return applicationNumber;
	}
	public void setApplicationNumber(int applicationNumber) {
		this.applicationNumber = applicationNumber;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}

}
