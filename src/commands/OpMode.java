package controllers;

/**
 * abstract interface for a command
 * 
 * @author jackf
 *
 */
public abstract class OpMode extends Command implements CommandCallback{
	ArrayList<Command> commands;
	public OpMode(CommandCallback callback, String name){
		super(callback, name);
		commands = new ArrayList<Command>();
	}
	
	public OpMode(CommandCallback callback){
		super(callback);
	}
	//called once
	public void init(){
		//No initing of commands, since they may not start immediately.
	}
	//called periodically
	public void periodic(double deltaTime){
		for(Command cmd:commands){
			cmd.periodic(deltaTime);
		}
	}
	//called once or never, to stop the command
	public void stop(){
		for(Command cmd:commands){
			cmd.stop();
		}
	}
	
	public void commandFinished(Command cmd){
		commands.remove(cmd);
	}
}
