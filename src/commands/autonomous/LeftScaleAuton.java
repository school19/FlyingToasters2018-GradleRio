package commands.autonomous;

import org.usfirst.frc.team3641.robot.Robot;

import commands.interfaces.OpMode;
import path_generation.Point;
import path_generation.Waypoint;
import utilities.Logging;

/**
 * Drives to the left scale plate.
 * @author jack
 *
 */
public class LeftScaleAuton extends OpMode {
	final static double dist_m = 4.0;
	
	Waypoint start = new Waypoint(new Point(0,0),0);
	Waypoint mid = new Waypoint(new Point(6,0.2), 0);
	Waypoint end = new Waypoint(new Point(7.5,-0.5), -Math.PI / 2.0);
	
	public LeftScaleAuton(Robot bot) {
		super(bot, "Left Scale auton");
	}
	
	public void init() {
		super.init();
		robot.driveBase.driveWaypoints(start, mid, end);
	}
	public void periodic(double deltaTime) {
		super.periodic(deltaTime);
		Logging.h("Left pos: " + robot.driveBase.left.getPosition() + ", right pos: " + robot.driveBase.right.getPosition());
	}
	public void stop() {
		robot.driveBase.setFeedbackActive(false);
	}
}
