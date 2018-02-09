package commands.autonomous;

import org.usfirst.frc.team3641.robot.Robot;

import commands.interfaces.OpMode;
import path_generation.Point;
import path_generation.Waypoint;
import utilities.Logging;

//TODO rewrite using MotionProfileCommand or change my mind about rewriting using MotionProfileCommand
/**
 * Auto line autonomous mode. Drives forward 2.5 meters.
 * 
 * @author jack
 *
 */
public class AutoLineAuton extends OpMode {
	private final static double dist_m = 2.5;

	private Waypoint start = new Waypoint(new Point(0, 0), 0);
	private Waypoint end = new Waypoint(new Point(dist_m, 0), 0);

	/**
	 * constructor for auto line auton. Takes the robot object as a parameter.
	 * 
	 * @param bot
	 */
	public AutoLineAuton(Robot bot) {
		super(bot, "Motion Profile Auton");
	}

	/**
	 * called when the opmode is initialized.
	 */
	public void init() {
		Logging.h("Starting baseline auton");
		super.init();
		robot.driveBase.driveFromTo(start, end, false);
	}

	/**
	 * called periodically when the opmode runs.
	 */
	public void periodic(double deltaTime) {
		super.periodic(deltaTime);
		Logging.l("Left pos: " + robot.driveBase.left.getPosition() + ", right pos: "
				+ robot.driveBase.right.getPosition());
	}

	/**
	 * called once when the opmode is stopped.
	 */
	public void stop() {
		robot.driveBase.setFeedbackActive(false);
	}
}
