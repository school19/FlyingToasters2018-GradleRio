package commands;

import org.usfirst.frc.team3641.robot.Robot;

import path_generation.Point;
import path_generation.Waypoint;
import utilities.Logging;

public class MotionProfileTest extends OpMode {
	final static double dist_m = 4.0;
	
	Waypoint start = new Waypoint(new Point(0,0),0);
	Waypoint end = new Waypoint(new Point(2,1), 0);
	
	public MotionProfileTest(Robot bot) {
		super(bot, "Motion Profile Auton");
	}
	
	public void init() {
		Logging.h("Init run!");
		super.init();
		robot.driveBase.driveFromTo(start, end);
	}
	public void periodic(double deltaTime) {
		super.periodic(deltaTime);
		Logging.h("Left pos: " + robot.driveBase.left.getPosition() + ", right pos: " + robot.driveBase.right.getPosition());
	}
	public void stop() {
		robot.driveBase.setFeedbackActive(false);
	}
}
