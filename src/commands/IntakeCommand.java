package commands;

import org.usfirst.frc.team3641.robot.Robot;

import commands.interfaces.Command;
import commands.interfaces.CommandCallback;
import hardware.Intake;

/**
 * A command to intake/output a power cube.
 *
 */
public class IntakeCommand extends Command {
	/**
	 * The robot the command is running on.
	 */
	private Robot bot;

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
	public IntakeCommand(CommandCallback opMode, Robot robot, String name, Intake.State mode) {
		super(opMode, name);
		bot = robot;
		bot.intake.setState(mode);
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
	public IntakeCommand(CommandCallback opMode, Robot robot, Intake.State mode) {
		this(opMode, robot, "Intake command", mode);
	}

	/**
	 * Called when the command is initialized. Sets time to zero and stops any motor
	 * movement.
	 */
	public void init() {
		bot.intake.setPower(0);
	}

	/**
	 * run continuously when the command is running. Updates the time, checks the
	 * sensor, and sets the motor/ends the command appropriately.
	 */
	public void periodic(double deltaTime) {
		bot.intake.perodic(deltaTime);
		if(bot.intake.getState() == Intake.State.RESTING) stop();
	}

	/**
	 * called when the command is stopped externally. Sets motors to off.
	 */
	public void stop() {
		bot.intake.setPower(0);
		super.stop();;
	}
}
