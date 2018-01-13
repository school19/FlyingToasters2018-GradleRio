package commands;

import org.usfirst.frc.team3641.robot.Robot;

/**
 * Time based auton to cross the auto line.
 * @author jack
 *
 */
public class LineAuton extends OpMode {
	final static double DRIVE_TIME = 2;
	
	private double timer = 0;
	
	public LineAuton(Robot bot, String name) {
		super(bot, name);
	}
	
	public void init() {
		robot.driveBase.drive(0.5,0.5);
	}
	
	public void periodic(double deltaTime) {
		timer += deltaTime;
		if(timer >= DRIVE_TIME) {
			earlyStop();
			endCommand();
		}
	}
	
	private void earlyStop() {
		robot.driveBase.drive(0,0);
	}
	
	public void stop() {
		earlyStop();
	}

}
