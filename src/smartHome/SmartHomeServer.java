package smartHome;

import java.io.*;
import java.net.*;

class ServerProtocol{
	public ServerProtocol() { }
}

class ServerProtocolFactory{
	public ServerProtocol create(){
		return new ServerProtocol();
	}
}

class ConnectionHandler implements Runnable {
	
	private BufferedReader in;
	private PrintWriter out;
	private boolean login;
	private String userName;
	Socket clientSocket;
	ServerProtocol protocol;
	
	public ConnectionHandler(Socket acceptedSocket, ServerProtocol p){
		in = null;
		out = null;
		login = false;
		userName = "";
		clientSocket = acceptedSocket;
		protocol = p;
		System.out.println("Accepted connection from client!");
	}
	
	public void run(){	
		try {
			initialize();
		}
		catch (IOException e) {
			System.out.println("Error in initializing I/O");
		}
		try {
			process();
		} 
		catch (IOException e) {
			System.out.println("Error in I/O");
		} 	
		System.out.println("Connection closed");
		close();
	}
	
	public void process() throws IOException{
		String msg;
		out.println("Please type: login <userName>");

		while ((msg = in.readLine()) != null){
			if(msg.contains("login")){
				if(!login)
					login(msg); 
				else 
					out.println("You are already logged in");   
			   } else if(login){				   
				   if (msg.contains("ListDevices"))
					   getListDevices();   
				   else if (msg.contains("SetState"))
					   setState(msg);   
				   else if (msg.contains("SetValue"))
					   setValue(msg);
				   else if (!checkMessage(msg)) out.println("Wrong command");
			   }  
				else out.println("Please logged in first");
			
			if (msg.contains("bye"))
				break;
		}
	}

	private boolean checkMessage(String msg) {
		return (msg.contains("login")||msg.contains("ListDevices")||msg.contains("SetState")||msg.contains("SetValue"));
	}

	private void setValue(String msg) {
		smartDevice device = getDevice(msg);
		if(device == null) return;
		String val = msg.substring(9);
		double value;
		try {
			value = Double.parseDouble(val.substring(val.indexOf(" ")));			
		} catch (Exception e) {
			out.println("Wrong command");
			return;
		}	
		if (!device.hasValue()) {
			out.println("Not possible to set a value for " + device.getItemType());
			return;
		}
		if (device.getState()){
		dataBase.setValue(userName, device, value);
		out.println(device.getItemType() + " value is now " + value);
		}
		else out.println("First, turn on your " + device.getItemType() + " and then set the value");
	}

	private void setState(String msg) {
		String status = "off";
		boolean state = false;
		   if(msg.contains("on")){
			   state = true;
			   status = "on";
		   } else if (!msg.contains("off")){
			   out.println("Wrond command"); 
			   return;
			   }
		   smartDevice device = getDevice(msg); 
		   if(device != null){
		   dataBase.setState(userName, device, state);
		   out.println(device.getItemType() +" is now " + status);
		   }
	}

	private void getListDevices() {
		Object[] deviceList= dataBase.getDevices(userName);
		out.println("Here are all your devices:");
	    for (int i = 0; i < deviceList.length; i++) {
		   smartDevice device = (smartDevice) deviceList[i];
		   String status = "off";
		   if (device.getState()) status="on";
		   String res = "Device id: "+ (i+1) + ", Type: " + device.getItemType() + ", State: " + status;
		   if (device.hasValue())
			   out.println("   " + res + ", Value: " + device.getValue());						
		   else out.println("   " + res);						
	   }		
	}

	private void login(String msg) {
		String user = msg.substring(msg.indexOf(" "));
		   if(!dataBase.userExist(user)){
			   dataBase.addUser(user);
			   login = true;
			   userName = user;
			   out.println("Hello" + userName + "!");
			   printCommands(out);
		   }
		   else out.println("Choose another name");		
	}

	private void printCommands(PrintWriter out) {
		   out.println("Please type one of the following comaands:");
		   out.println("   - ListDevices");
		   out.println("   - SetState <deviceid> <on|off>");
		   out.println("   - SetValue <deviceid> <value>");
		   out.println("   - bye");		
	}

	private smartDevice getDevice(String msg) {
		   String next = msg.substring(9);
		   int index = Integer.parseInt(next.substring(0, next.indexOf(" ")));
		   Object[] device = dataBase.getDevices(userName);
		   if (index > device.length) {
			   out.println("Wrong Device ID"); 
			   return null;
		   }
		   return (smartDevice) dataBase.getDevices(userName)[index-1];
	}

	public void initialize() throws IOException
	{
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(),"UTF-8"));
		out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(),"UTF-8"), true);
		System.out.println("I/O initialized");
	}
	
	public void close()
	{
		try {
			if (in != null)
			{
				in.close();
			}
			if (out != null)
			{
				out.close();
			}
			
			clientSocket.close();
		}
		catch (IOException e)
		{
			System.out.println("Exception in closing I/O");
		}
	}
}

class SmartHomeServer implements Runnable {
	private ServerSocket serverSocket;
	private ServerProtocolFactory factory;
	
	public SmartHomeServer(ServerProtocolFactory p)
	{
		serverSocket = null;
		factory = p;
	}
	
	public void run()
	{
		try {
			serverSocket = new ServerSocket(6789);
			System.out.println("Listening...");
		}
		catch (IOException e) {
			System.out.println("Cannot listen on port 6789");
		}
		
		while (true)
		{
			try {
				ConnectionHandler newConnection = new ConnectionHandler(serverSocket.accept(), factory.create());
            new Thread(newConnection).start();
			}
			catch (IOException e)
			{
				System.out.println("Failed to accept on port 6789");
			}
		}
	}
	

	public void close() throws IOException
	{
		serverSocket.close();
	}
	
	public static void main(String[] args) throws IOException
	{
		SmartHomeServer server = new SmartHomeServer(new ServerProtocolFactory());
		Thread serverThread = new Thread(server);
		serverThread.start();
		try {
			serverThread.join();
		}
		catch (InterruptedException e)
		{
			System.out.println("Server stopped");
		}
	}
}
