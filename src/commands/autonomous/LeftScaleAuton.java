package commands.autonomous;

import org.usfirst.frc.team3641.robot.Robot;

import commands.interfaces.OpMode;
import path_generation.Point;
import path_generation.Waypoint;
import utilities.Logging;

//TODO add intake commands
/**
 * Drives to the left scale plate.
 * @author jack
 *
 */
public class LeftScaleAuton extends OpMode {
	
	private Waypoint start = new Waypoint(new Point(0,0),0);
	private Waypoint mid = new Waypoint(new Point(6,0.2), 0);
	private Waypoint end = new Waypoint(new Point(7.5,-0.5), -Math.PI / 2.0);
	
	/**
	 * constructor for the left scale plate auton.
	 * @param bot
	 */
	public LeftScaleAuton(Robot bot) {
		super(bot, "Left Scale auton");
	}
	
	/**
	 * called once when initialized.
	 */
	public void init() {
		super.init();
		robot.driveBase.driveWaypoints(false, start, mid, end);
	}
	/**
	 * called periodically when opmode is running.
	 */
	public void periodic(double deltaTime) {
		super.periodic(deltaTime);
		Logging.h("Left pos: " + robot.driveBase.left.getPosition() + ", right pos: " + robot.driveBase.right.getPosition());
	}
	/**
	 * called once when the opmode is stopped.
	 */
	public void stop() {
		robot.driveBase.setFeedbackActive(false);
	}
}
