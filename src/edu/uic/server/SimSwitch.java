package edu.uic.server;

public class SimSwitch extends SimDevice {
	/*Switch contains port count for the number of ports,
	an array of sending and receiving devices and name of the switch*/
	private int portCount = 60;
	private SimDevice[] sendingDevices = new SimDevice[60];
	private SimDevice[] receivingDevices = new SimDevice[60];
	private String switchName;
	private long macAddress;
	public long getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(long macAddress) {
		this.macAddress = macAddress;
	}
	public String getSwitchName() {
		return switchName;
	}
	public void setSwitchName(String switchName) {
		this.switchName = switchName;
	}

	public int getPortCount() {
		return portCount;
	}
	public void setPortCount(int portCount) {
		this.portCount = portCount;
	}
	public SimDevice[] getSendingDevices() {
		return sendingDevices;
	}
	public void setSendingDevices(SimDevice[] sendingDevices) {
		this.sendingDevices = sendingDevices;
	}
	public SimDevice[] getReceivingDevices() {
		return receivingDevices;
	}
	public void setReceivingDevices(SimDevice[] receivingDevices) {
		this.receivingDevices = receivingDevices;
	}
	
	/**
	 * This methods adds the devices to be connected to the switch
	 * If the device is a client or another switch, we are populating sending devices
	 * If the device is a server, we are populating receiving devices 
	 * @param simDevices : The devices to be added to the switch
	 */
	public void addDevices(SimDevice[] simDevices){
		
		int sending = getArrayLength(sendingDevices);
		int receiving = getArrayLength(receivingDevices);
		for (SimDevice device : simDevices){
			if (device instanceof SimSwitch || 
					device instanceof SimClient){
				sendingDevices[sending] = device;
				sending++;
			} else if (device instanceof SimServer){
				receivingDevices[receiving] = device;
				receiving++;
			}
		}
		
		System.out.println(this.getSwitchName() + ": Sending: " + getArrayLength(sendingDevices) +
				", Receving: " + getArrayLength(receivingDevices));
	}
	
	/**
	 * Get the length of the sending or receiving array
	 * @param array
	 * @return
	 */
	public int getArrayLength(SimDevice[] array){
		int length = 0;
		for (SimDevice device: array){
			if (device != null){
				length++;
			}else {
				break;
			}
		}
		return length;
	}
	
}
