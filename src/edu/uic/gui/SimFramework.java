package edu.uic.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import edu.uic.server.SimApp;
import edu.uic.server.SimClient;
import edu.uic.server.SimDevice;
import edu.uic.server.SimServer;
import edu.uic.server.SimSwitch;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JTextPane;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.awt.event.ActionEvent;

/*
 * This class contains the GUI for the network simulator application
 * It provides functionality to :
 * 1. Add devices, delete devices
 * 2. Connect devices to switches
 * 3. Add application to be run on one client and one server
 */
public class SimFramework extends JFrame {
	private static final int NUMBER_DEVICES = 20;
	private static final int NUMBER_APPS = 4;
	private static ArrayList<ArrayList<Long>> allOptimalRoutes = new ArrayList<>();
	private static ArrayList<ArrayList<Long>> allNetworkRoutes = new ArrayList<>();
	private static HashMap<Long, Long> macIPPair = new HashMap<>();
	public static HashMap<Long, Long> getMacIPPair() {
		return macIPPair;
	}
	
	private ArrayList<SimSwitch> connectedSwitches = new ArrayList<>();
	private ArrayList<SimSwitch> connectedSwitchesUnicast = new ArrayList<>();
	private HashMap<SimApp, SimClient> applicationSourceMap = new HashMap<>();
	private HashMap<SimApp, SimServer> applicationServerMap = new HashMap<>();
	/*
	 * The class contains a time counter, list of clients, servers, switches
	 * and applications present in the network
	 */
	private static BufferedWriter writer;
	public static ArrayList<ArrayList<Long>> getAllOptimalRoutes() {
		return allOptimalRoutes;
	}
	
	public static ArrayList<ArrayList<Long>> getAllNetworkRoutes(){
		return allNetworkRoutes;
	}

	private static long timeCounter = 0L;
	private SimServer[] listOfServers = new SimServer[SimFramework.NUMBER_DEVICES];
	private SimClient[] listOfClients = new SimClient[SimFramework.NUMBER_DEVICES];
	private SimSwitch[] listOfSwitches = new SimSwitch[SimFramework.NUMBER_DEVICES];
	private SimApp[] listOfApplications = new SimApp[SimFramework.NUMBER_APPS];
	private long mac = 2155872257L;
	private long ip = 3221225473L;
	private ArrayList<ArrayList<Long>> listOfRoutesSameDes = new ArrayList<>();
	
	
	private int numServer, nameServer = 0;
	private int numClient, nameClient = 0;
	private int numSwitch, nameSwitch = 0;
	private int numApp = 0;
	
	public static long getTimeCounter(){
		return timeCounter;
	}
	
	public static void setTimeCounter(long timeCounter){
		SimFramework.timeCounter = timeCounter;
	}
	public JList getServerList() {
		return serverList;
	}

	public void setServerList(JList serverList) {
		this.serverList = serverList;
	}

	public JList getSwitchList() {
		return switchList;
	}

	public void setSwitchList(JList switchList) {
		this.switchList = switchList;
	}

	public JList getClientList() {
		return clientList;
	}

	public void setClientList(JList clientList) {
		this.clientList = clientList;
	}

	private JList serverList;
	private JList switchList;
	private JList clientList;
	private JList applicationList;
	private JPanel contentPane;

	public JList getApplicationList() {
		return applicationList;
	}

	public void setApplicationList(JList applicationList) {
		this.applicationList = applicationList;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SimFramework frame = new SimFramework();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SimFramework() {
		
		//Add applications
		try {
			writer = new BufferedWriter(new FileWriter("./output.txt"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (int i = 0; i < NUMBER_APPS; i++) {
			SimApp app = new SimApp();
			int timeBetPackets=0;
			int numberOfPackets=0;
			app.setAppName("Application" + (i+1));
			listOfApplications[i] = app;
			if(i==0){
				Random rn = new Random();
				timeBetPackets = rn.nextInt(2*500) + 1;
				numberOfPackets = rn.nextInt(9) + 1;
			}else if(i==1){
				Random rn = new Random();
				timeBetPackets = rn.nextInt(2*100) + 1;
				numberOfPackets = rn.nextInt(23) + 2;
			}else if(i==2){
				Random rn = new Random();
				timeBetPackets = rn.nextInt(2*200) + 1;
				numberOfPackets = rn.nextInt(18) + 2;
			}else if(i==3){
				Random rn = new Random();
				timeBetPackets = rn.nextInt(2*500) + 1;					
				numberOfPackets = rn.nextInt(3) + 2;
			}
			app.setNumPackets(numberOfPackets);
			app.setTimeBetweenPackets(timeBetPackets);
		}
		
		serverList = new JList(new DefaultListModel<>());
		serverList.setVisible(false);
		switchList = new JList(new DefaultListModel<>());
		switchList.setVisible(false);
		clientList = new JList(new DefaultListModel<>());
		clientList.setVisible(false);
		applicationList = new JList(new DefaultListModel<>());

		applicationList.setVisible(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout());
		setContentPane(contentPane);

		JPanel server = new JPanel();
		// server.setBounds(5, 5, 150, 200);
		contentPane.add(server, BorderLayout.LINE_START);
		server.setLayout(new GridLayout(0, 1));

		JTextPane txtpnDataCenter = new JTextPane();
		txtpnDataCenter.setText("Data Center");
		server.add(txtpnDataCenter);
		writeToFile("The set of devices in the network");
		JButton btnAddServer = new JButton("Add Server");
		btnAddServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SimServer server = new SimServer();
				
				server.setServerName("Server" + (nameServer+1));
				server.setMacAddress(mac);
				server.setIpAddress(ip);
				System.out.println("MAC: " + mac);
				writeToFile(server.getServerName() +" - " + "MAC Address: " + getAddress(mac) + 
						", IP Address: " + getAddress(ip));
				macIPPair.put(mac, ip);
				mac++;
				ip++;
				listOfServers[numServer] = server;
				numServer++;
				nameServer++;
				serverList = displayServers(server);
				serverList.setVisible(true);
			}
		});
		JButton btnDeleteServer = new JButton("Delete Server");
		btnDeleteServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame deviceFrame = new JFrame();
				deviceFrame.setBounds(400, 100, 350, 250);
				JPanel mainPanel = new JPanel();
				mainPanel.setBounds(100, 100, 350, 200);
				mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
				mainPanel.setLayout(new BorderLayout());
				deviceFrame.setContentPane(mainPanel);
				JPanel serverPane = new JPanel();
				serverPane.setBounds(100, 10, 100, 150);
				serverPane.setLayout(new GridLayout(0, 1));
				mainPanel.add(serverPane, BorderLayout.LINE_START);
				ButtonGroup group = new ButtonGroup();
				JRadioButton[] serverButton = new JRadioButton[numServer];
				for (int i = 1; i <= numServer; i++) {
					serverButton[i - 1] = new JRadioButton(listOfServers[i-1].getServerName());
					serverPane.add(serverButton[i - 1]);
					group.add(serverButton[i - 1]);
				}
				deviceFrame.setVisible(true);
				JButton addButton = new JButton("Delete");
				addButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						for (int i = 0; i < serverButton.length; i++) {
							if (serverButton[i] == null) {
								break;
							}
							if (serverButton[i].isSelected()) {
								System.out.println("Button selected : " + serverButton[i].getText());
								removeServer(i);
								break;
							}
						}
					}
				});
				serverPane.add(addButton);
				serverList.setVisible(true);
			}
		});
		server.add(btnDeleteServer);
		server.add(btnAddServer);
		server.add(serverList);	

		JPanel switches = new JPanel();
		switches.setLayout(new GridLayout(0, 1));
		// switches.setBounds(160, 5, 50, 200);
		contentPane.add(switches, BorderLayout.CENTER);
		JTextPane txtpnSwitches = new JTextPane();
		txtpnSwitches.setText("Switches");
		switches.add(txtpnSwitches);

		JButton btnAddSwitch = new JButton("Add Switch");
		btnAddSwitch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SimSwitch addSwitch = new SimSwitch();
				addSwitch.setSwitchName("Switch"+(nameSwitch+1));
				addSwitch.setMacAddress(mac);
				System.out.println("MAC: " + mac);
				writeToFile(addSwitch.getSwitchName() + " - " + "MAC Address: " + getAddress(mac));
				macIPPair.put(mac, null);
				mac++;
				listOfSwitches[numSwitch] = addSwitch;
				numSwitch++;
				nameSwitch++;
				switchList = displaySwitches(addSwitch);
				switchList.setVisible(true);
			}
		});
		
		JButton btnDeleteSwitch = new JButton("Delete Switch");
		btnDeleteSwitch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame deviceFrame = new JFrame();
				deviceFrame.setBounds(400, 100, 350, 250);
				JPanel mainPanel = new JPanel();
				mainPanel.setBounds(100, 100, 350, 200);
				mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
				mainPanel.setLayout(new BorderLayout());
				deviceFrame.setContentPane(mainPanel);
				JPanel switchPane = new JPanel();
				switchPane.setBounds(100, 10, 100, 150);
				switchPane.setLayout(new GridLayout(0, 1));
				mainPanel.add(switchPane, BorderLayout.LINE_START);
				ButtonGroup group = new ButtonGroup();
				JRadioButton[] switchButton = new JRadioButton[numSwitch];
				for (int i = 1; i <= numSwitch; i++) {
					switchButton[i - 1] = new JRadioButton(listOfSwitches[i-1].getSwitchName());
					switchPane.add(switchButton[i - 1]);
					group.add(switchButton[i - 1]);
				}
				deviceFrame.setVisible(true);
				JButton addButton = new JButton("Delete");
				addButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						for (int i = 0; i < switchButton.length; i++) {
							if (switchButton[i] == null) {
								break;
							}
							if (switchButton[i].isSelected()) {
								System.out.println("Button selected : " + switchButton[i].getText());
								removeSwitch(i);
								break;
							}
						}
					}
				});
				switchPane.add(addButton);
				switchList.setVisible(true);
			}
		});
		switches.add(btnDeleteSwitch);

		JButton btnAddDevices = new JButton("Add Devices");
		btnAddDevices.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame deviceFrame = new JFrame();
				deviceFrame.setBounds(400, 100, 350, 250);
				JPanel mainPanel = new JPanel();
				mainPanel.setBounds(100, 100, 350, 200);
				mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
				mainPanel.setLayout(new BorderLayout());
				deviceFrame.setContentPane(mainPanel);
				JPanel switchPane = new JPanel();
				switchPane.setBounds(100, 10, 100, 150);
				switchPane.setLayout(new GridLayout(0, 1));
				mainPanel.add(switchPane, BorderLayout.LINE_START);
				JPanel devicePane = new JPanel();
				devicePane.setBounds(210, 10, 100, 150);
				devicePane.setLayout(new GridLayout(0, 1));
				mainPanel.add(devicePane, BorderLayout.LINE_END);
				ButtonGroup group = new ButtonGroup();
				JRadioButton[] switchButton = new JRadioButton[SimFramework.NUMBER_DEVICES];
				JCheckBox[] serverDeviceButton = new JCheckBox[SimFramework.NUMBER_DEVICES];
				JCheckBox[] clientDeviceButton = new JCheckBox[SimFramework.NUMBER_DEVICES];
				JCheckBox[] switchDeviceButton = new JCheckBox[SimFramework.NUMBER_DEVICES];
				for (int i = 1; i <= numSwitch; i++) {
					switchButton[i - 1] = new JRadioButton(listOfSwitches[i-1].getSwitchName());
					switchPane.add(switchButton[i - 1]);
					group.add(switchButton[i - 1]);
				}
				for (int i = 1; i <= numServer; i++) {
					serverDeviceButton[i - 1] = new JCheckBox(listOfServers[i-1].getServerName());
					devicePane.add(serverDeviceButton[i - 1]);
				}
				for (int i = 1; i <= numClient; i++) {
					clientDeviceButton[i - 1] = new JCheckBox(listOfClients[i-1].getClientName());
					devicePane.add(clientDeviceButton[i - 1]);
				}
				for (int i = 1; i <= numSwitch; i++) {
					switchDeviceButton[i - 1] = new JCheckBox(listOfSwitches[i-1].getSwitchName());
					devicePane.add(switchDeviceButton[i - 1]);
				}
				JButton addButton = new JButton("Connect");
				addButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						SimSwitch simSwitch = null;
						// TODO Auto-generated method stub
						for (int i = 0; i < switchButton.length; i++) {
							if (switchButton[i] == null) {
								break;
							}
							if (switchButton[i].isSelected()) {
								System.out.println("Button selected : " + switchButton[i].getText());
								simSwitch = listOfSwitches[i];
								break;
							}
						}
						ArrayList<SimDevice> simDevices = new ArrayList<>();
						for (int i = 0; i < serverDeviceButton.length; i++) {

							if (serverDeviceButton[i] == null) {
								break;
							}
							if (serverDeviceButton[i].isSelected()) {
								System.out.println("Box selected : " + serverDeviceButton[i].getText());
								simDevices.add(listOfServers[i]);
							}
						}
						for (int i = 0; i < clientDeviceButton.length; i++) {

							if (clientDeviceButton[i] == null) {
								break;
							}
							if (clientDeviceButton[i].isSelected()) {
								System.out.println("Box selected : " + clientDeviceButton[i].getText());
								simDevices.add(listOfClients[i]);
							}
						}
						for (int i = 0; i < switchDeviceButton.length; i++) {

							if (switchDeviceButton[i] == null) {
								break;
							}
							if (switchDeviceButton[i].isSelected()) {
								System.out.println("Box selected : " + switchDeviceButton[i].getText());
								simDevices.add(listOfSwitches[i]);
							}
						}
						if (simSwitch != null) {
							simSwitch.addDevices(simDevices.toArray(new SimDevice[simDevices.size()]));
						}

					}
				});
				mainPanel.add(addButton, BorderLayout.SOUTH);
				deviceFrame.setVisible(true);
			}
			
		});
		switches.add(btnAddSwitch);
		switches.add(switchList);
		switches.add(btnAddDevices);
		
		JButton generateUnicastTraffic = new JButton("Unicast Traffic");
		generateUnicastTraffic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				generateTraffic("Unicast");
			}
		});
		
		JButton generateBroadcastTraffic = new JButton("Broadcast Traffic");
		generateBroadcastTraffic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setAllConections();
				generateTraffic("Broadcast");
			}
		});
		switches.add(generateUnicastTraffic);
		switches.add(generateBroadcastTraffic);

		JPanel client = new JPanel();
		client.setLayout(new GridLayout(0, 1));
		// client.setBounds(220, 5, 50, 200);
		contentPane.add(client, BorderLayout.LINE_END);
		JTextPane txtpnClient = new JTextPane();
		txtpnClient.setText("Clients");
		client.add(txtpnClient);

		JButton btnAddClient = new JButton("Add Client");
		btnAddClient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SimClient client = new SimClient();
				client.setClientName("Client" + (nameClient+1));
				client.setMacAddress(mac);
				client.setIpAddress(ip);
				System.out.println("MAC: " + mac);
				writeToFile(client.getClientName() +" - " + "MAC Address: " + getAddress(mac) + 
						", IP Address: " + getAddress(ip));
				macIPPair.put(mac, ip);
				mac++;
				ip++;
				listOfClients[numClient] = client;
				numClient++;
				nameClient++;
				clientList = displayClients(client);
				clientList.setVisible(true);
			}
		});
		client.add(btnAddClient);
		client.add(clientList);
		
		JButton btnDeleteClient = new JButton("Delete Client");
		btnDeleteClient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame deviceFrame = new JFrame();
				deviceFrame.setBounds(400, 100, 350, 250);
				JPanel mainPanel = new JPanel();
				mainPanel.setBounds(100, 100, 350, 200);
				mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
				mainPanel.setLayout(new BorderLayout());
				deviceFrame.setContentPane(mainPanel);
				JPanel clientPane = new JPanel();
				clientPane.setBounds(100, 10, 100, 150);
				clientPane.setLayout(new GridLayout(0, 1));
				mainPanel.add(clientPane, BorderLayout.LINE_START);
				ButtonGroup group = new ButtonGroup();
				JRadioButton[] clientButton = new JRadioButton[numClient];
				for (int i = 1; i <= numClient; i++) {
					clientButton[i - 1] = new JRadioButton(listOfClients[i-1].getClientName());
					clientPane.add(clientButton[i - 1]);
					group.add(clientButton[i - 1]);
				}
				deviceFrame.setVisible(true);
				JButton addButton = new JButton("Delete");
				addButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						for (int i = 0; i < clientButton.length; i++) {
							if (clientButton[i] == null) {
								break;
							}
							if (clientButton[i].isSelected()) {
								System.out.println("Button selected : " + clientButton[i].getText());
								removeClient(i);
								break;
							}
						}
					}
				});
				clientPane.add(addButton);
				clientList.setVisible(true);
			}
		});
		client.add(btnDeleteClient);

		JButton btnAddApplication = new JButton("Add  Application");
		JButton btnAddMapClientApp = new JButton("Assign server");

	
		JRadioButton[] clientButton = new JRadioButton[SimFramework.NUMBER_DEVICES];
		JRadioButton[] applicationButton = new JRadioButton[SimFramework.NUMBER_DEVICES];
		btnAddApplication.addActionListener(new ActionListener() {

		

			public void actionPerformed(ActionEvent arg0) {
				applicationList.removeAll();
				numApp = NUMBER_APPS;

				JFrame applicationFrame = new JFrame();
				applicationFrame.setBounds(400, 100, 350, 250);
				JPanel applicationPanel = new JPanel();
				applicationPanel.setBounds(100, 10, 100, 150);

				applicationPanel.setLayout(new GridLayout(0, 1));
				applicationFrame.getContentPane().add(applicationPanel, BorderLayout.LINE_START);
				JPanel clientDisplayPanel = new JPanel();
				clientDisplayPanel.setBounds(100, 10, 100, 150);
				clientDisplayPanel.setLayout(new GridLayout(0, 1));
				applicationFrame.getContentPane().add(clientDisplayPanel, BorderLayout.LINE_END);
				ButtonGroup groupClient = new ButtonGroup();
				ButtonGroup groupApp = new ButtonGroup();

				for (int i = 1; i <= numClient; i++) {
					clientButton[i - 1] = new JRadioButton("client" + i);
					clientDisplayPanel.add(clientButton[i - 1]);
					groupClient.add(clientButton[i - 1]);

				}

				for (int i = 1; i <= numApp; i++) {

					applicationButton[i - 1] = new JRadioButton("Application" + i);
					applicationPanel.add(applicationButton[i - 1]);
					groupApp.add(applicationButton[i - 1]);
				}
				applicationList = displayApplication();
				applicationList.setVisible(true);
				applicationFrame.add(btnAddMapClientApp);
				applicationFrame.setVisible(true);           
                btnAddMapClientApp.addActionListener(new ActionListener() {			
					int appIndex=0;
					int clientIndex = 0;

					public void actionPerformed(ActionEvent arg0) {
						 // please look into this
					
						for (int i = 0; i < clientButton.length; i++) {
							if (clientButton[i] == null) {
								break;
							}
							if (clientButton[i].isSelected()) {
								
								System.out.println("Client selected : " + clientButton[i].getText());
								clientIndex=i;
								System.out.println("Index of client : " + clientIndex);
								
								
								SimClient simClient = listOfClients[clientIndex];
								SimServer simServer=getServer(simClient);
								
								for(int k=0;k<applicationButton.length;k++)
								{
									if (applicationButton[k] == null) {
										break;
									}
									if (applicationButton[k].isSelected()) {
									System.out.println("App selected : " + applicationButton[k].getText());
									appIndex=k;
									System.out.println("Index of App : " + appIndex);
									
									simServer.addClientAndApplication(listOfClients[clientIndex],
											listOfApplications[appIndex]);
									simClient.addApplicationAndServer(listOfApplications[appIndex], 
											simServer);
									break;	
								}}
								applicationSourceMap.put(listOfApplications[appIndex], simClient);
								applicationServerMap.put(listOfApplications[appIndex], simServer);
								ArrayList<Long> route = allOptimalRoutes.get(allOptimalRoutes.size()-1);
								String routingPath = getRoutingPathMAC(route);
								System.out.println();
								StringBuilder outputFile = new StringBuilder(listOfApplications[appIndex].
										getAppName().toString());
										outputFile.append(" is running on ").append(simClient.getClientName().
												toString()).append(" and ").
										append(simServer.getServerName().toString());
										outputFile.append(System.getProperty("line.separator"));
										outputFile.append(routingPath);
										outputFile.append(System.getProperty("line.separator"));
										writeToFile(outputFile.toString());
								break;
							}
						}

					}
					
				});
			}

		});
		client.add(btnAddApplication);
		

	}
	
	public void setAllConections(){
		for (int client = 0; client<numClient; client++){
			connectedSwitches.clear();
			generateConnections(listOfClients[client], null, new ArrayList<>());
		}
	}
	
	public void generateConnections(SimDevice device, SimDevice previous, ArrayList<Long> route){
		if (device instanceof SimClient){
			route.add(((SimClient) device).getMacAddress());
			for (SimSwitch simSwitch : listOfSwitches){
				if(simSwitch == null){
					break;
				}
				if(!connectedSwitches.contains(simSwitch)){
					SimDevice client;
					ArrayList<Long> commonRoute = new ArrayList<>(route);
					for (int i=0; i<simSwitch.getSendingDevices().length; i++){
						client = simSwitch.getSendingDevices()[i];
						if(device.equals(client)){
							commonRoute.add(simSwitch.getMacAddress());
							generateConnections(simSwitch, device, commonRoute);
							break;
						}
						
					}
				}
			}
		}else if (device instanceof SimSwitch){
			for (SimDevice server: ((SimSwitch) device).getReceivingDevices()){
				if(server == null){
					break;
				}
				ArrayList<Long> routeTillNow = new ArrayList<>(route);
				if(server instanceof SimServer){
					routeTillNow.add(((SimServer) server).getMacAddress());
					allNetworkRoutes.add(routeTillNow);
				}
			}
			for (SimDevice sw : ((SimSwitch) device).getSendingDevices()){
				if(sw == null){
					break;
				}
				if(sw instanceof SimSwitch && !sw.equals(previous)){
					connectedSwitches.add((SimSwitch) sw);
					route.add(((SimSwitch) sw).getMacAddress());
					generateConnections(sw, device, route);
				}
			}
		}
	}
	
	public void generateTraffic(String transmitMode){
		System.out.println("Tranmit mode: " + transmitMode);
		long startTime = System.currentTimeMillis();
		SimApp application;
		String destination="";
		while(timeCounter < 10000){
			for (int i=0; i<4; i++){
				application = listOfApplications[i];
				int messageSize = application.getNumPackets();
				String source = Long.toString(applicationSourceMap.get(application).getMacAddress());
				if("Unicast".equals(transmitMode)){
					destination = Long.toString(applicationServerMap.
							get(application).getIpAddress());
				}else{
					destination = "0.0.0.0";
				}
				
				application.createMessage(source, destination, messageSize, transmitMode);
				if(timeCounter > 10000){
					break;
				}
			}
			System.out.println("Time counter: " + timeCounter);
		}	
	}

	public JList<String> displayServers(SimServer server) {
		((DefaultListModel) serverList.getModel()).addElement(server.getServerName()
				 + ", IP: " + getAddress(server.getIpAddress()) + 
				 ", MAC: " + getAddress(server.getMacAddress()));
		return serverList;
	}

	public JList<String> displaySwitches(SimSwitch simSwitch) {
		((DefaultListModel) switchList.getModel()).addElement(simSwitch.getSwitchName()
				 +  ", MAC: " + getAddress(simSwitch.getMacAddress()));
		return switchList;
	}

	public JList<String> displayClients(SimClient client) {
		((DefaultListModel) clientList.getModel()).addElement(client.getClientName()
			+ ", IP: " + getAddress(client.getIpAddress()) + ", MAC: " + getAddress(client.getMacAddress()));
		return clientList;
	}

	public JList<String> displayApplication() {

		((DefaultListModel) applicationList.getModel()).addElement("Application" + numApp);

		return applicationList;
	}
	
	public static String getRoutingPathMAC(ArrayList<Long> route){
		StringBuilder routingPath = new StringBuilder();
		for (int mac=0;mac<route.size(); mac++){
			routingPath.append(getAddress(route.get(mac)) + " ");
		}
		return routingPath.toString();
	}
	
	public SimServer getServer(SimClient simClient){
		Random rn = new Random();
		int index = rn.nextInt(numServer);
		System.out.println("Server selected: " + listOfServers[index].getServerName());
		connectedSwitchesUnicast.clear();
		generateRoute(listOfServers[index], simClient, new ArrayList<Long>());
		ArrayList<Long> shortest = getShortestRoute();
		if (null != shortest){
			allOptimalRoutes.add(shortest);
		}
		return listOfServers[index];
	}
	
	public void generateRoute(SimServer destination, SimDevice previousNode, ArrayList<Long> route){
		if (previousNode instanceof SimClient){
			route.add(((SimClient) previousNode).getMacAddress());
			for (SimSwitch simSwitch : listOfSwitches){
				if(simSwitch == null){
					break;
				}
				SimDevice client;
				ArrayList<Long> commonRoute = new ArrayList<>(route);
				for (int i=0; i<simSwitch.getSendingDevices().length; i++){
					client = simSwitch.getSendingDevices()[i];
					if(client == null){
						break;
					}
					if(previousNode.equals(client)){
						commonRoute.add(simSwitch.getMacAddress());
						generateRoute(destination, simSwitch, commonRoute);
						break;
					}
					
				}
			}
		}else if (previousNode instanceof SimSwitch){
			for (SimDevice server: ((SimSwitch) previousNode).getReceivingDevices()){
				if(server == null){
					break;
				}
				if(server instanceof SimServer){
					if (destination.equals((SimServer) server)){
						route.add(destination.getMacAddress());
						listOfRoutesSameDes.add(route);
						return;
					}
				}
			}
			for (SimDevice device : ((SimSwitch) previousNode).getSendingDevices()){
				if(device == null){
					break;
				}
				if(device instanceof SimSwitch && !connectedSwitchesUnicast.contains(device)){
					connectedSwitchesUnicast.add((SimSwitch) device);
					ArrayList<Long> newRoute = new ArrayList<>(route);
					newRoute.add(((SimSwitch) device).getMacAddress());
					generateRoute(destination, device, newRoute);
				}
			}
		}
		
	}
	
	public static String getAddress(Long type){
		String address =  String.valueOf(Long.parseLong(Long.toHexString(type).
				substring(0, 2), 16)) + "."
		 + String.valueOf(Long.parseLong(Long.toHexString(type).substring(2, 4), 16)) + "." 
				+ String.valueOf(Long.parseLong(Long.toHexString(type).substring(4, 6), 16)) + "."
		 + String.valueOf(Long.parseLong(Long.toHexString(type).substring(6, 8), 16));
		return address;
	}
	public static void writeToFile(String content){
		try {
			writer.append(content);
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<Long> getShortestRoute(){
		if(listOfRoutesSameDes.isEmpty()){
			System.out.println("No path exists from route to the destination");
			return null;
		}
		int length = listOfRoutesSameDes.get(0).size();
		ArrayList<Long> route = listOfRoutesSameDes.get(0);
		for (int i=1; i<listOfRoutesSameDes.size(); i++){
			if (listOfRoutesSameDes.get(i).size() < length){
				length = listOfRoutesSameDes.get(i).size();
				route = listOfRoutesSameDes.get(i);
			}
		}
		listOfRoutesSameDes.clear();
		return route;
	}
	
	public void removeServer(int index){
		SimServer server = listOfServers[index];
		if (server.getListOfClients() != null && server.getListOfClients()[0] != null){
			JFrame frame = new JFrame();
			JOptionPane.showMessageDialog(frame.getComponent(0), "Cannot remove server... Its in use");
			return;
		}
		System.out.println("Length of server list: " + serverList.getModel().getSize());
		((DefaultListModel) serverList.getModel()).remove(index);
		for (int num=index;  num<numServer-1; num++){
			listOfServers[index] = listOfServers[index+1];
		}
		numServer--;
		JFrame frame = new JFrame();
		JOptionPane.showMessageDialog(frame.getComponent(0), "Removed Server : " + server.getServerName());
		System.out.println("Remaining servers...");
		for (int i=0; i<numServer; i++){
			System.out.print(listOfServers[i].getServerName() + ", ");
		}
	}
	
	public void removeClient(int index){
		SimClient client = listOfClients[index];
		if (client.getListOfServers() != null && client.getListOfServers()[0] != null){
			JFrame frame = new JFrame();
			JOptionPane.showMessageDialog(frame.getComponent(0), "Cannot remove client... Its in use");
			return;
		}
		System.out.println("Length of client list: " + clientList.getModel().getSize());
		((DefaultListModel) clientList.getModel()).remove(index);
		for (int num=index;  num<numClient-1; num++){
			listOfClients[index] = listOfClients[index+1];
		}
		numClient--;
		JFrame frame = new JFrame();
		JOptionPane.showMessageDialog(frame.getComponent(0), "Removed client : " + client.getClientName());
		System.out.println("Remaining clients...");
		for (int i=0; i<numClient; i++){
			System.out.print(listOfClients[i].getClientName() + ", ");
		}
	}
	
	public void removeSwitch(int index){
		SimSwitch switches = listOfSwitches[index];
		if ((switches.getSendingDevices() != null && switches.getSendingDevices()[0] != null)
				|| (switches.getReceivingDevices() != null && switches.getReceivingDevices()[0] != null)){
			JFrame frame = new JFrame();
			JOptionPane.showMessageDialog(frame.getComponent(0), "Cannot remove switch... Its in use");
			return;
		}
		((DefaultListModel) switchList.getModel()).remove(index);
		for (int num=index;  num<numSwitch-1; num++){
			listOfSwitches[index] = listOfSwitches[index+1];
		}
		numSwitch--;
		JFrame frame = new JFrame();
		JOptionPane.showMessageDialog(frame.getComponent(0), "Removed switch : " + switches.getSwitchName());
		System.out.println("Remaining switches...");
		for (int i=0; i<numSwitch; i++){
			System.out.print(listOfSwitches[i].getSwitchName() + ", ");
		}
	}
}
