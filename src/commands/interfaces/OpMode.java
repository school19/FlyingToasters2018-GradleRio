package commands;

import java.util.ArrayList;

import org.usfirst.frc.team3641.robot.Robot;

/**
 * abstract interface for a command
 * 
 * @author jackf
 *
 */
public abstract class OpMode extends Command implements CommandCallback{
	private ArrayList<Command> commands;
	protected Robot robot;
	public OpMode(Robot bot, String name){
		super(bot, name);
		commands = new ArrayList<Command>();
		robot = bot;
	}
	
	public OpMode(Robot bot){
		super(bot);
		commands = new ArrayList<Command>();
		robot = bot;
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
	
	protected void addCommand(Command cmd) {
		commands.add(cmd);
		cmd.init();
	}
	
	public void commandFinished(Command cmd){
		commands.remove(cmd);
	}
}
