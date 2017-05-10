package smartHome;

public class AirConditioner implements smartDevice {

	private boolean state;
	private double value;
	
	public AirConditioner() {
		state = false;
		value = 0;
	}
	
	@Override
	public boolean getState() {
		return state;
	}

	@Override
	public void setState(boolean bool) {
		if(bool) value=25;
		else value = 0;
		state = bool;		
	}

	public double getValue() {
		return value;
	}
	
	@Override
	public void setValue(double val) {
		value = val;
		
	}

	@Override
	public boolean hasValue() {
		return true;
	}

	@Override
	public String getItemType() {
		return "Air Conditioner";
	}

}
