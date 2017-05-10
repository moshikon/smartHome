package smartHome;

public class Lamp implements smartDevice{
	
	private boolean state;

	public Lamp() {
		state = false;
	}
	@Override
	public boolean getState() {
		return state;
	}

	@Override
	public void setState(boolean bool) {
		state = bool;		
	}

	@Override
	public String getItemType() {
		return "Lamp";
	}
	
	@Override
	public void setValue(double value) {
		// TODO Auto-generated method stub		
	}
	@Override
	public boolean hasValue() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getValue() {
		// TODO Auto-generated method stub
		return 0;
	}


}
