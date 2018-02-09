package commands.teleop;

import org.usfirst.frc.team3641.robot.Robot;

import commands.interfaces.OpMode;
import utilities.Logging;

/**
 * Teleop class for human-operated control.
 * 
 * @author jack
 *
 */
public class Teleop extends OpMode {
	/**
	 * The PS4 controller the driver uses to control the robot
	 */
	private PS4 ps4;
	
	/**
	 * Constructor
	 * @param bot the Robot that's being controlled.
	 */
	public Teleop(Robot bot) {
		super(bot, "Teleop");
		ps4 = new PS4(0);
	}
	
	/**
	 * Called once when the opmode starts. Doesn't really do anything yet.
	 */
	public void init() {
		super.init();
		Logging.h("Starting teleop");
	}
	/**
	 * Called periodically during teleop period. Reads user inputs from controllers and controls the robot.
	 */
	public void periodic(double deltaTime) {
		Logging.l("Teleop periodic run");
		// updates all running commands
		super.periodic(deltaTime);
		// get input from ps4 controller
		ps4.poll();
		// drive the derivebase
		robot.driveBase.driveGrilledCheese(ps4.getAxis(PS4.Axis.LEFT_Y), -ps4.getAxis(PS4.Axis.RIGHT_X));
		// log position
		Logging.l("left enc.: " + robot.driveBase.left.getPosition());
		Logging.l("Right enc.:" + robot.driveBase.right.getPosition());
		//set the power of the intake based on the user inputs.
		robot.intake.setPower(ps4.getAxis(PS4.Axis.RIGHT_TRIGGER)-ps4.getAxis(PS4.Axis.LEFT_TRIGGER));
	}
	
	/**
	 * Called when the opmode is stopped.
	 */
	public void stop() {
		super.stop();
		Logging.h("Stopping teleop");
	}
}
