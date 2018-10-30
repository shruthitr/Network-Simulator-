package edu.uic.server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import edu.uic.gui.SimFramework;

public class SimApp {
	private String appName;
	private int numPackets;
	private int timeBetweenPackets;
	
	public SimApp(){
	}
	
	public int getNumPackets() {
		return numPackets;
	}

	public void setNumPackets(int numPackets) {
		this.numPackets = numPackets;
	}

	public int getTimeBetweenPackets() {
		return timeBetweenPackets;
	}

	public void setTimeBetweenPackets(int timeBetweenPackets) {
		this.timeBetweenPackets = timeBetweenPackets;
	}

	public void createMessage(String source, String destination, int messageSize, String transmitMode){
		SimMessage message = new SimMessage();
		message.setSource(source);
		message.setDestination(destination);
		message.setSize(messageSize);
		System.out.println(source + ", des: " + destination + ", size: " + messageSize);
		SimPacket[] packets = getPackets(message);
		if("Unicast".equals(transmitMode)){
			transmitPacketsUnicast(packets);
		}else{
			transmitPacketBroadcast(packets);
		}
		
	}
	
	public void transmitPacketBroadcast(SimPacket[] packets){
		SimFramework.writeToFile("--------------- BROADCAST TRANSMISSIONS START-----------");
		SimPacket packet = packets[0];
			ArrayList<ArrayList<Long>> allRoutes = SimFramework.getAllNetworkRoutes();
			ArrayList<String> broadcastRoutes = new ArrayList<>();
			for (int routeNum=0; routeNum < allRoutes.size();routeNum++){
				ArrayList<Long> route = allRoutes.get(routeNum);
					if(packet.getSource().equals(Long.toString(route.get(0)))){
						broadcastRoutes.add(SimFramework.getRoutingPathMAC(route));
					}		
			}
			
			for (int i=0; i<packets.length; i++){
				packet = packets[i];
				for (int r=0; r<broadcastRoutes.size();r++){
					if(SimFramework.getTimeCounter() < 10000){
						long transmitTime = System.currentTimeMillis();
						StringBuilder sb = new StringBuilder();
						 sb.append("Time in ms: ").append(new SimpleDateFormat(("mm:ss:SSS")).
						 format(new Date(transmitTime))).
						 append(", Packet ").append(i).append(" from ").append(appName)
						 .append(" broadcasted through route ").append(broadcastRoutes.get(r));
						 SimFramework.writeToFile(sb.toString());
						System.out.println("Packet " + i + "transmitted from " + appName + " with "
								+ "source as " + packets[i].getSource() + " and destination as "
								+ packets[i].getDestination());
						try {
							TimeUnit.MILLISECONDS.sleep(timeBetweenPackets);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						SimFramework.setTimeCounter(SimFramework.getTimeCounter() + 
								(System.currentTimeMillis() - transmitTime));
					}else{
						break;
					}
					
				}
				if(SimFramework.getTimeCounter() > 10000){
					break;
				}
			}	
			SimFramework.writeToFile("--------------- BROADCAST TRANSMISSIONS END-----------");
	}
	
	public void transmitPacketsUnicast(SimPacket[] packets){
		SimFramework.writeToFile("--------------- UNICAST TRANSMISSIONS START-----------");
		SimPacket packet = packets[0];
		String path = null;
			ArrayList<ArrayList<Long>> allRoutes = SimFramework.getAllOptimalRoutes();
			for (int routeNum=0; routeNum < allRoutes.size();routeNum++){
				ArrayList<Long> route = allRoutes.get(routeNum);
					if(packet.getSource().equals(Long.toString(route.get(0)))){
						String destination = Long.toString(route.get(route.size()-1));
						HashMap<Long, Long> macIP = SimFramework.getMacIPPair();
						String ipAddress = Long.toString(macIP.get(Long.valueOf(destination)));
						if (packet.getDestination().equals(ipAddress)){
							 path = SimFramework.getRoutingPathMAC(route);
							 break;
						}
					}		
			}
			if(path != null){
				for (int i=0; i<packets.length; i++){
					if(SimFramework.getTimeCounter() < 10000){
						long transmitTime = System.currentTimeMillis();
						packet = packets[i];
						StringBuilder sb = new StringBuilder();
						sb.append("Time in ms: ").append(new SimpleDateFormat(("mm:ss:SSS")).
								 format(new Date(transmitTime))).
						append(", Packet ").append(i).append(" from ").append(appName)
						 .append(" transmitted through route ").append(path);
						SimFramework.writeToFile(sb.toString());
						System.out.println("Packet " + i + "transmitted from " + appName + " with "
								+ "source as " + packets[i].getSource() + " and destination as "
								+ packets[i].getDestination());
						try {
							TimeUnit.MILLISECONDS.sleep(timeBetweenPackets);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						SimFramework.setTimeCounter(SimFramework.getTimeCounter() + 
								(System.currentTimeMillis() - transmitTime));
					}else{
						break;
					}
				}
				
			}
			SimFramework.writeToFile("--------------- UNICAST TRANSMISSIONS END-----------");
	}
	
	/**
	 * This method generates the packets by breaking down a large message
	 * into 1Kb packets which are then transmitted
	 * It return the array of packets
	 * @param message
	 * @return
	 */
	public SimPacket[] getPackets(SimMessage message){
		SimPacket[] packets = new SimPacket[message.getSize()];
		int size = message.getSize();
		for (int i=0; i<size;i++){
			SimPacket packet = new SimPacket();
			packet.setSource(message.getSource());
			packet.setDestination(message.getDestination());
			packet.setApplicationNumber(message.getApplicationNumber());
			packet.setSeqNumber(i);
			packets[i] = packet;
		}
		return packets;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

}
