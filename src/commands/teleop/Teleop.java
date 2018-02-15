package commands.teleop;

import org.usfirst.frc.team3641.robot.Robot;

import commands.interfaces.OpMode;
import hardware.Intake;
import utilities.Logging;

/**
 * Teleop class for human-operated control.
 * 
 * @author jack
 *
 */
public class Teleop extends OpMode {
	/**
	 * The PS4 controller the driver uses to control the robot
	 */
	private PS4 ps4;
	private E3D e3d;

	/**
	 * Constructor
	 * 
	 * @param bot
	 *            the Robot that's being controlled.
	 */
	public Teleop(Robot bot) {
		super(bot, "Teleop");
		ps4 = new PS4(0);
		e3d = new E3D(1);
	}

	/**
	 * Called once when the opmode starts. Doesn't really do anything yet.
	 */
	public void init() {
		super.init();
		Logging.h("Starting teleop");
		robot.lift.readTuningValuesFromDashboard();
	}

	/**
	 * Called periodically during teleop period. Reads user inputs from controllers
	 * and controls the robot.
	 */
	public void periodic(double deltaTime) {
		Logging.l("Teleop periodic run");
		// updates all running commands
		super.periodic(deltaTime);
		// get input from ps4 controller
		ps4.poll();
		e3d.poll();
		// drive the derivebase
		robot.driveBase.driveGrilledCheese(ps4.getAxis(PS4.Axis.LEFT_Y), -ps4.getAxis(PS4.Axis.RIGHT_X));
		// log position
		Logging.l("left enc.: " + robot.driveBase.left.getPosition());
		Logging.l("Right enc.:" + robot.driveBase.right.getPosition());
		
		// set the power of the intake based on the user inputs.
		
		if(e3d.isPressed(3)) robot.intake.setState(Intake.State.INTAKING);
		else if(e3d.isPressed(4)) robot.intake.setState(Intake.State.OUTPUTTING);
		else if(e3d.isReleased(3) && robot.intake.getState() == Intake.State.INTAKING) robot.intake.setState(Intake.State.RECOVERY); 
		else if(e3d.isReleased(4) && robot.intake.getState() == Intake.State.OUTPUTTING) robot.intake.setState(Intake.State.RESET); 
		else if(robot.intake.getState() == Intake.State.RESTING) robot.intake.setPower(e3d.getAxis(E3D.Axis.Y));
	
		// move the lift
		/*if (ps4.isPressed(PS4.Button.DPAD_DOWN))
			robot.lift.trackToPos(Lift.Positions.GROUND);
		else if (ps4.isPressed(PS4.Button.DPAD_LEFT))
			robot.lift.trackToPos(Lift.Positions.SWITCH);
		else if (ps4.isPressed(PS4.Button.DPAD_UP))
			robot.lift.trackToPos(Lift.Positions.SCALE);
		// update the lift
		robot.lift.update();
		// log data about the lift's position, velocity, and error to the smartdashboard
		// to help tune PIDs
		robot.lift.logToDashboard();*/
	}

	/**
	 * Called when the opmode is stopped.
	 */
	public void stop() {
		super.stop();
		Logging.h("Stopping teleop");
	}
}
