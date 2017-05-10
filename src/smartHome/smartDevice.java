package smartHome;

public interface smartDevice {
	
	public boolean getState();
	
	public double getValue();

	public void setState(boolean bool);

	public void setValue(double value);

	public boolean hasValue();
	
	public String getItemType();
	
}
