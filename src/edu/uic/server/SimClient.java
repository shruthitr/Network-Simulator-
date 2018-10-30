package edu.uic.server;

public class SimClient extends SimDevice {
	/*
	 * Contains the name of the client, array of applications connected to
	 * the client and array of servers connected to the client
	 */
	int size=0;
	private SimServer[] listOfServers;
	private SimApp[] listOfApplications;
	private String clientName;
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

	public SimServer[] getListOfServers() {
		return listOfServers;
	}
	public void setListOfServers(SimServer[] listOfServers) {
		this.listOfServers = listOfServers;
	}
	public SimApp[] listOfApplications() {
		return listOfApplications;
	}
	public void setListOfApp(SimApp[] listOfApplications) {
		this.listOfApplications = listOfApplications;
	}
	
	public SimApp[] getListOfApplications() {
		return listOfApplications;
	}
	public void setListOfApplications(SimApp[] listOfApplications) {
		this.listOfApplications = listOfApplications;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	
	/**
	 * This method adds one application to be run on server and
	 * corresponding client on which its run
	 * It populates the application and server array with this data
	 * @param application
	 * @param server
	 */
	public void addApplicationAndServer(SimApp application, SimServer server){
		if(size == 0){
			listOfServers = new SimServer[10];
			listOfApplications = new SimApp[10];
		}
		listOfServers[size] = server;
		listOfApplications[size] = application;
		size++;
		printAppAndServerArray();
	}
	
	public void printAppAndServerArray(){
		for(int i=0; i<size;i++){
			System.out.println("Client: " + this.getClientName() + 
					", Server: " + listOfServers[i].getServerName() + 
					", Application: " + listOfApplications[i].getAppName());
		}
	}
	
}
