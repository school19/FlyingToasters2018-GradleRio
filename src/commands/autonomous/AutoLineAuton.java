package commands.autonomous;

import org.usfirst.frc.team3641.robot.Robot;

import commands.DelayedCommand;
import commands.LiftCommand;
import commands.MotionProfileCommand;
import commands.interfaces.OpMode;
import hardware.Lift;
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
	MotionProfileCommand motionProfile;
	DelayedCommand delay;
	LiftCommand flip;
	/**
	 * constructor for auto line auton. Takes the robot object as a parameter.
	 * 
	 * @param bot
	 */
	public AutoLineAuton(Robot bot) {
		super(bot, "Motion Profile Auton");
		motionProfile = new MotionProfileCommand(this, bot, "cross line", false, MotionProfileCommand.Speed.SLOW_LOW_ACCEL, start, end);
		delay = new DelayedCommand(this, 0.5);
		flip = new LiftCommand(delay, bot, Lift.Positions.STARTING_FLIP);
		delay.setCommand(flip);
	}

	/**
	 * called when the opmode is initialized.
	 */
	public void init() {
		Logging.h("Starting baseline auton");
		super.init();
		motionProfile.init();
		delay.init();
	}

	/**
	 * called periodically when the opmode runs.
	 */
	public void periodic(double deltaTime) {
		super.periodic(deltaTime);
		motionProfile.periodic(deltaTime);
		Logging.l("Left pos: " + robot.driveBase.left.getPosition() + ", right pos: "
				+ robot.driveBase.right.getPosition());
		delay.periodic(deltaTime);
	}

	/**
	 * called once when the opmode is stopped.
	 */
	public void stop() {
		robot.driveBase.setFeedbackActive(false);
		motionProfile.stop();
	}
}
