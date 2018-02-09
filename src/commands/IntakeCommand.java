package commands;

import org.usfirst.frc.team3641.robot.Robot;

import commands.interfaces.Command;
import commands.interfaces.CommandCallback;

/**
 * A command to intake/output a power cube.
 *
 */
public class IntakeCommand extends Command {
	/**
	 * the current time, used to stop command.
	 */
	double time;
	/**
	 * The robot the command is running on.
	 */
	private Robot bot;
	/**
	 * the state of the command, set when the command is initialized.
	 */
	private State currentState;
	/**
	 * The default speed at which the intake will run.
	 */
	final double defaultSpeed = 0.75;
	/**
	 * how long to run the intake for if no cube is detected.
	 */
	final double timeWithoutCube = 4;

	/**
	 * Enum to store the state of the intake.
	 * 
	 */
	private static enum State {
		INTAKING, OUTPUTTING, RESTING, RESET,
	}

	/**
	 * Constructor for the intake command.
	 * 
	 * @param opMode
	 *            the opmode calling the command. used for callback.
	 * @param robot
	 *            the robot the command is running on
	 * @param name
	 *            the name of the command
	 * @param mode
	 *            whether the command is intaking or outputting
	 */
	public IntakeCommand(CommandCallback opMode, Robot robot, String name, State mode) {
		super(opMode, name);
		bot = robot;
		currentState = mode;
	}

	/**
	 * Constructor for the intake command using default name.
	 * 
	 * @param opMode
	 *            the opmode calling the command. used for callback.
	 * @param robot
	 *            the robot the command is running on
	 * @param mode
	 *            whether the command is intaking or outputting
	 */
	public IntakeCommand(CommandCallback opMode, Robot robot, State mode) {
		this(opMode, robot, "Intake command", mode);
	}

	/**
	 * Called when the command is initialized. Sets time to zero and stops any motor
	 * movement.
	 */
	public void init() {
		time = 0;
		bot.intake.setPower(0);
	}

	/**
	 * run continuously when the command is running. Updates the time, checks the
	 * sensor, and sets the motor/ends the command apropriately.
	 */
	public void periodic(double deltaTime) {
		switch (currentState) {
		case INTAKING:
			bot.intake.setPower(defaultSpeed);
			if (bot.intake.hasCube())
				currentState = State.RESET;
			break;
		case OUTPUTTING:
			bot.intake.setPower(-defaultSpeed);
			if (bot.intake.hasCube())
				time = 0;
			else
				time += deltaTime;
			if (time >= timeWithoutCube)
				currentState = State.RESET;
			break;
		case RESET:
			bot.intake.setPower(0);
			currentState = State.RESTING;
		case RESTING:
			endCommand();
			break;
		}
	}

	/**
	 * called when the command is stopped externally. Sets motors to off.
	 */
	public void stop() {
		bot.intake.setPower(0);
	}
}
