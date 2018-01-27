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
	private PS4 ps4;
	final static double TRIGGER_DEADZONE = .1;

	public Teleop(Robot bot) {
		super(bot, "Teleop");
		ps4 = new PS4(0);
	}

	public void init() {
		super.init();
		Logging.h("Starting teleop");
	}

	public void periodic(double deltaTime) {
		Logging.h("Teleop periodic run");
		// updates all running commands
		super.periodic(deltaTime);
		// get input from ps4 controller
		ps4.poll();
		// drive the derivebase
		robot.driveBase.driveGrilledCheese(ps4.getAxis(PS4.Axis.LEFT_Y), -ps4.getAxis(PS4.Axis.RIGHT_X));
		// log position
		Logging.l("left enc.: " + robot.driveBase.left.getPosition());
		Logging.l("Right enc.:" + robot.driveBase.right.getPosition());

		double rightTrigger = ps4.getAxis(PS4.Axis.RIGHT_TRIGGER);
		double leftTrigger = ps4.getAxis(PS4.Axis.LEFT_TRIGGER);
		if (rightTrigger >= TRIGGER_DEADZONE) {
			robot.intake.setPower(rightTrigger);
		} else if (leftTrigger >= TRIGGER_DEADZONE) {
			robot.intake.setPower(-leftTrigger);
		}
	}

	public void stop() {
		super.stop();
		Logging.h("Stopping teleop");
	}
}
