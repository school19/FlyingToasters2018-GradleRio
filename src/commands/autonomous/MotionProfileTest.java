package commands.autonomous;

import org.usfirst.frc.team3641.robot.Robot;

import commands.interfaces.OpMode;
import path_generation.Point;
import path_generation.Waypoint;
import utilities.Logging;

public class MotionProfileTest extends OpMode {
	final static double dist_m = 4.0;
	
	Waypoint start = new Waypoint(new Point(0,0),0);
	Waypoint mid = new Waypoint(new Point(0,0), 0);
	Waypoint end = new Waypoint(new Point(2,0), 0);
	
	public MotionProfileTest(Robot bot) {
		super(bot, "Motion Profile Auton");
	}
	
	public void init() {
		Logging.h("Init run! TESTSTSTWTTSESTES");
		super.init();
		robot.driveBase.driveFromTo(mid, end);
	}
	public void periodic(double deltaTime) {
		super.periodic(deltaTime);
		Logging.h("Left pos: " + robot.driveBase.left.getPosition() + ", right pos: " + robot.driveBase.right.getPosition());
	}
	public void stop() {
		robot.driveBase.setFeedbackActive(false);
	}
}
