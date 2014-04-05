package compositelauncher.actions.ui;

/**
 * Instance of this class contains information about launch configuration.<br/> 
 * It stores configuration memento, name, launch mode and delay after launch of this configuration
 * @author tdq
 */
public class LaunchConfig {
	private String memento;
	private String name;
	private String mode;
	private int delay;
	
	/**
	 * Constructs instance by setting values for all fields.
	 * @param memento - configuration memento
	 * @param name - name of launch configuration
	 * @param mode - launch mode
	 * @param delay - delay after launch 
	 */
	public LaunchConfig(String memento, String name, String mode, int delay) {
		this.memento = memento;
		this.name = name;
		this.mode = mode;
		this.delay = delay > 0 ? delay : 0;
	}
	
	/**
	 * Constructs instance from tab separated list of fields.<br/>
	 * The format is:<br/>
	 * <code>
	 * [memento]\t[name]\t[mode]\t[delay]
	 * </code>
	 * @param view
	 */
	public LaunchConfig(String view) {
		String[] tokens = view.split("\t");
		
		//TODO check if tokens has all elements otherwise use default values
		
		memento = tokens[0];
		if(tokens.length > 1)
			name = tokens[1];
		else
			name = "Default name";
		
		if(tokens.length > 2)
			mode = tokens[2];
		else
			mode = "run";
		
		if(tokens.length > 3)
			delay = Integer.parseInt(tokens[3]);
		else
			delay = 0;
	}
	
	/**
	 * Returns the configuration memento
	 * @return configuration memento
	 */
	public String getMemento() {
		return memento;
	}
	
	/**
	 * Returns the configuration name
	 * @return name of configuration
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns configuration launch mode
	 * @return configuration launch mode
	 */
	public String getMode() {
		return mode;
	}
	
	/**
	 * Returns delay after launch this configuration
	 * @return delay after launch this configuration
	 */
	public int getDelay() {
		return delay;
	}
	
	@Override
	public String toString() {
		return memento+"\t"+name+"\t"+mode+"\t"+delay;
	}
}