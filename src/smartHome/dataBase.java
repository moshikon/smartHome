package smartHome;

import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;

public class dataBase {

	private static ConcurrentHashMap<String, ArrayList<smartDevice>> userDevices = new ConcurrentHashMap<String, ArrayList<smartDevice>>();
	
	public static boolean userExist(String user){
		return userDevices.containsKey(user);
	}
	
	public boolean deviceExist(String user, smartDevice device){
		return userDevices.get(user).contains(device);
	}
	
	public static ArrayList<smartDevice> createArray(){
		ArrayList<smartDevice> devices = new ArrayList<smartDevice>();
		devices.add(new AirConditioner());
		devices.add(new Lamp());
		return devices;
	}
	
	public static void addUser(String user){
		userDevices.put(user, createArray());
	}
	
	public static Object[] getDevices(String user){
		return userDevices.get(user).toArray();
	}
	
	public static void setState(String user, smartDevice device, boolean bool){
		device.setState(bool);
	}
	
	public static void setValue(String user, smartDevice device, double value){
		device.setValue(value);
	}

}
