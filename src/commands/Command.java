package commands;

/**
 * abstract interface for a command
 * 
 * @author jackf
 *
 */
public abstract class Command {
	//the callback to call upon ending
	private CommandCallback callback;
	//the readable name of the command
	protected String readableName = "Unnamed command";
	public Command(CommandCallback callback, String name){
		this(callback);
		readableName = name;
	}
	public Command(CommandCallback callback){
		this.callback = callback;
	}
	//called once
	public void init(){}
	//called periodically
	public void periodic(double deltaTime){}
	//called once or never, to stop the command
	public void stop(){}
	
	//called when the command ends and calls back
	protected void endCommand(){
		callback.commandFinished(this);
	}
	
	//toString used for printing
	@Override
	public String toString(){
		return readableName;
	}
}
