package commands;

import org.usfirst.frc.team3641.robot.Robot;

import utilities.Logging;

/**
 * Time based auton to cross the auto line.
 * @author jack
 *
 */
public class LineAuton extends OpMode {
	final static double DRIVE_TIME = 2;
	final static double DRIVE_SPEED = 0.75;
	final static double LEFT_DS = 1.0;
	
	private double timer = 0;
	
	public LineAuton(Robot bot) {
		super(bot, "Line Auton");
	}
	
	public LineAuton(Robot bot, String name) {
		super(bot, name);
	}
	
	public void init() {
		super.init();
		//robot.driveBase.drive(DRIVE_SPEED, DRIVE_SPEED);
		timer = 0;
		Logging.h("INIT RUN!!!");
	}
	
	public void periodic(double deltaTime) {
		super.periodic(deltaTime);
		Logging.h("timer = " + timer);
		timer += deltaTime;
		if(timer >= DRIVE_TIME) {
			earlyStop();
			endCommand();
		}else {
			robot.driveBase.drive(LEFT_DS, DRIVE_SPEED);
		}
	}
	
	private void earlyStop() {
		robot.driveBase.drive(0,0);
	}
	
	public void stop() {
		super.stop();
		earlyStop();
		timer = 0;
	}

}
