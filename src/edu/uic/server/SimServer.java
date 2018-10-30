package edu.uic.server;

public class SimServer extends SimDevice {
	/*
	 * Contains the name of the server, list of applications run on this server
	 * and corresponding clients on which they run
	 */
	private int size=0;
	private String serverName;
	private SimApp[] listOfApplications;
	private SimClient[] listOfClients;
	private long macAddress;
	private long ipAddress;
	
	public long getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(long ipAddress) {
		this.ipAddress = ipAddress;
	}
	public long getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(long macAddress) {
		this.macAddress = macAddress;
	}
	public SimApp[] getListOfApplications() {
		return listOfApplications;
	}
	public void setListOfApplications(SimApp[] listOfApplications) {
		this.listOfApplications = listOfApplications;
	}
	public SimClient[] getListOfClients() {
		return listOfClients;
	}
	public void setListOfClients(SimClient[] listOfClients) {
		this.listOfClients = listOfClients;
	}
	
	/**
	 * This method adds one client and one application to the server
	 * It populates the applications and clients array
	 * @param client
	 * @param application
	 */
	public void addClientAndApplication(SimClient client, SimApp application){
		if(size == 0){
			listOfClients = new SimClient[10];
			listOfApplications = new SimApp[10];
		}
		listOfClients[size] = client;
		listOfApplications[size] = application;
		size++;
		printClientAppArray();
	}
	
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
	public void printClientAppArray(){
		for(int i=0; i<size;i++){
			System.out.println("Server: " + this.getServerName() + 
					", Client: " + listOfClients[i].getClientName() + 
					", Application: " + listOfApplications[i].getAppName());
		}
	}
}
