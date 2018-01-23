package commands.teleop;

import org.usfirst.frc.team3641.robot.Robot;

import commands.interfaces.OpMode;
import utilities.Logging;

/**
 * Teleop class for human-operated control.
 * @author jack
 *
 */
public class Teleop extends OpMode {
	private PS4 ps4;
	
	public Teleop(Robot bot) {
		super(bot, "Teleop");
		ps4 = new PS4(0);
	}
	
	public void init() {
		super.init();
		Logging.h("Starting teleop");
	}
	
	public void periodic(double deltaTime) {
		//updates all running commands
		super.periodic(deltaTime);
		//get input from ps4 controller
		ps4.poll();
		//drive the derivebase
		robot.driveBase.driveGrilledCheese(ps4.getAxis(PS4.Axis.LEFT_Y), -ps4.getAxis(PS4.Axis.RIGHT_X));
		//log position
		Logging.l("left enc.: " + robot.driveBase.left.getPosition());
		Logging.l("Right enc.:" + robot.driveBase.right.getPosition());
		
		
	}
	
	public void stop() {
		super.stop();
		Logging.h("Stopping teleop");
	}
}
