package commands;

import org.usfirst.frc.team3641.robot.Robot;

import commands.interfaces.Command;
import commands.interfaces.CommandCallback;

public class IntakeCommand extends Command {
	double time;
	private Robot bot;
	private State currentState;
	final double defaultSpeed = 0.75;
	final double timeWithoutCube = 4;
	
	private static enum State {
		INTAKING,
		OUTPUTTING,
		RESTING,
		RESET,
	}
	
	public IntakeCommand(CommandCallback opMode, Robot robot, String name, State mode) {
		super(opMode, name);
		bot = robot;
		currentState = mode;
	}
	
	public void init() {
		time = 0;
		bot.intake.setPower(0);
	}
	
	public void periodic(double deltaTime) {
		switch(currentState) {
		case INTAKING:
			bot.intake.setPower(defaultSpeed);
			if(bot.intake.hasCube()) currentState = State.RESET;
			break;
		case OUTPUTTING:
			bot.intake.setPower(-defaultSpeed);
			if(bot.intake.hasCube()) time = 0;
			else time += deltaTime;
			if(time >= timeWithoutCube) currentState = State.RESET;
			break;
		case RESET:
			bot.intake.setPower(0);
			currentState = State.RESTING;
		case RESTING:
			endCommand();
			break;
		}
	}
	
	public void stop() {
		bot.intake.setPower(0);
	}
}
