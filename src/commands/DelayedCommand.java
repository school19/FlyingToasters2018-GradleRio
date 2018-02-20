package commands;

import commands.interfaces.Command;
import commands.interfaces.CommandCallback;

public class DelayedCommand extends Command implements CommandCallback{
	private double delay;
	private Command command;
	private boolean commandRunning = false;
	CommandCallback callback;
	public DelayedCommand (CommandCallback cb, double delayTime) {
		super(cb, "Delayed command");
		delay = delayTime;
	}
	
	public void setCommand(Command command) {
		this.command = command;
	}
	
	public void init() {
	}
	
	public void periodic (double deltaTime) {
		if(commandRunning) {
			command.periodic(deltaTime);
		} else {
			delay -= deltaTime;
			if(delay <= 0) {
				commandRunning = true;
				command.init();
			}
		}
	}
	
	public void stop() {
		command.stop();
	}

	@Override
	public void commandFinished(Command cmd) {
		endCommand();
	}
}
