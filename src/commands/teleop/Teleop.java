package commands.teleop;

import org.usfirst.frc.team3641.robot.Robot;

import commands.interfaces.OpMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DriverStation;
import hardware.Intake;
import hardware.Lift;
import utilities.Logging;

/**
 * Teleop class for human-operated control.
 * 
 * @author jack
 *
 */
public class Teleop extends OpMode {
	
	private enum DriveMode {
		TANK,
		ARCADE,
		PURE_CHEESE,
		GRILLED_CHEESE,
	}
	
	private DriveMode driveMode = DriveMode.GRILLED_CHEESE;
	
	/**
	 * The PS4 controller the driver uses to control the robot
	 */
	private PS4 ps4;
	private Operator op;
	
	private DriverStation ds;
	
	private boolean endgame;

	/**
	 * Constructor
	 * 
	 * @param bot
	 *            the Robot that's being controlled.
	 */
	public Teleop(Robot bot) {
		super(bot, "Teleop");
		ps4 = new PS4(0);
		op = new Operator(2);
		ds = DriverStation.getInstance();
		endgame = false;
	}

	/**
	 * Called once when the opmode starts. Doesn't really do anything yet.
	 */
	public void init() {
		super.init();
		Logging.h("Starting teleop");
		//Disables feedback control!!!! (OOPS WE DIDNT thiNK OF ThiS UNtiL BAG Day!!!)
		robot.driveBase.setFeedbackActive(false);
		robot.driveBase.left.setFeedbackController(null);
		robot.driveBase.right.setFeedbackController(null);
		
		robot.lift.readTuningValuesFromDashboard();
		
		if (SmartDashboard.getBoolean("Manual enabled", true)) {
			robot.lift.disableMotionMagic();
		}

		driveMode = DriveMode.GRILLED_CHEESE;
		op.checkControllerType();
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
		op.poll();
		
		//Set the current drive mode based on the d-pad if the left bumper is pressed for safety.
		if(ps4.isDown(PS4.Button.LEFT_BUMPER)) {
			if(ps4.isPressed(PS4.Button.DPAD_UP)) driveMode = DriveMode.GRILLED_CHEESE;
			else if(ps4.isPressed(PS4.Button.DPAD_RIGHT)) driveMode = DriveMode.PURE_CHEESE;
			else if(ps4.isPressed(PS4.Button.DPAD_DOWN)) driveMode = DriveMode.ARCADE;
			else if(ps4.isPressed(PS4.Button.DPAD_LEFT)) driveMode = DriveMode.TANK;
		}
		
		//Actually drive based on the drive mode
		switch(driveMode) {
		case GRILLED_CHEESE:
			robot.driveBase.driveGrilledCheese(ps4.getAxis(PS4.Axis.LEFT_Y), -ps4.getAxis(PS4.Axis.RIGHT_X));
			break;
		case PURE_CHEESE:
			if(ps4.isDown(PS4.Button.RIGHT_BUMPER)) robot.driveBase.driveArcade(ps4.getAxis(PS4.Axis.LEFT_Y), -ps4.getAxis(PS4.Axis.RIGHT_X));
			else robot.driveBase.drivePureCheese(ps4.getAxis(PS4.Axis.LEFT_Y), -ps4.getAxis(PS4.Axis.RIGHT_X));
			break;
		case ARCADE:
			robot.driveBase.driveArcade(ps4.getAxis(PS4.Axis.LEFT_Y), -ps4.getAxis(PS4.Axis.RIGHT_X));
			break;
		case TANK:
			robot.driveBase.driveTank(ps4.getAxis(PS4.Axis.LEFT_Y), ps4.getAxis(PS4.Axis.RIGHT_Y));
			break;
		}
		
		// Log the encoder positions with low priority
		Logging.l("left enc.: " + robot.driveBase.left.getPosition());
		Logging.l("Right enc.:" + robot.driveBase.right.getPosition());

		// Set the power of the intake based on the user inputs.
		if (op.isPressed(Operator.Button.INTAKE))
			robot.intake.setState(Intake.State.INTAKING);
		else if (op.isPressed(Operator.Button.OUTTAKE))
			robot.intake.setState(Intake.State.OUTPUTTING_SLOW);
		else if (op.isReleased(Operator.Button.INTAKE) && robot.intake.getState() == Intake.State.INTAKING)
			robot.intake.setState(Intake.State.RECOVERY);
		else if (op.isReleased(Operator.Button.OUTTAKE) && robot.intake.getState() == Intake.State.OUTPUTTING_SLOW)
			robot.intake.setState(Intake.State.RESET);
		// else if (robot.intake.getState() == Intake.State.RESTING)
		// robot.intake.setPower(e3d.getAxis(E3D.AxisX));

		// Move the lift on the rising edge of each button
		if (op.isPressed(Operator.Button.GROUND))
			robot.lift.trackToPos(Lift.Positions.GROUND);
		else if (op.isPressed(Operator.Button.LOW_SWITCH))
			robot.lift.trackToPos(Lift.Positions.SWITCH);
		else if (op.isPressed(Operator.Button.HIGH_SWITCH))
			robot.lift.trackToPos(Lift.Positions.H_SWITCH);
		else if (op.isPressed(Operator.Button.LOW_SCALE))
			robot.lift.trackToPos(Lift.Positions.L_SCALE);
		else if (op.isPressed(Operator.Button.HIGH_SCALE))
			robot.lift.trackToPos(Lift.Positions.H_SCALE);
		// log data about the lift's position, velocity, and error to the smartdashboard
		// to help tune PIDs
		robot.lift.logToDashboard();
		
		//Reset the lift down slowly until it hits the limit switch and re:zero s
//		if(op.isPressed(Operator.Button.RESET)) {
//			robot.lift.resetDown();
//		} else if(op.isReleased(Operator.Button.RESET)) {
//			robot.lift.stopResettingDown();
//		}
		
		//When the whammy bar is pressed, don't tilt the intake up when we get a cube.
		if(op.isDown(Operator.Button.AUTO_FLIP)) robot.intake.enableAutolift(true);
		else if(op.isReleased(Operator.Button.AUTO_FLIP)) robot.intake.enableAutolift(false);
		
		//If we get the cube, let the driver know by activating the heavy rumble
		if(robot.intake.getState() == Intake.State.HAS_CUBE) ps4.rumbleForTime(1, true, .5);
		
		//Set the light rumble based on the drivebase velocity.
		ps4.rumble(robot.driveBase.getWheelVelocity(), false);
		
		//End game:
		SmartDashboard.putBoolean("Endgame: ", endgame);
		if(!endgame) endgame = (ds.getMatchTime() > 105) || (ps4.isPressed(PS4.Button.SHARE));
		else {
			//Do endgame stuff
		}
		// Temporary manual lift control code
		// TODO remove temporary lift control code.
//		if (SmartDashboard.getBoolean("Manual enabled", false)) {
//			double liftPower = -op.e3d.getAxis(E3D.Axis.THROTTLE) * op.e3d.getAxis(E3D.Axis.Y);
//			double flipPower = op.e3d.getAxis(E3D.Axis.THROTTLE) * op.e3d.getAxis(E3D.Axis.X);
//			SmartDashboard.putNumber("Lift power", liftPower);
//			SmartDashboard.putNumber("Flip power", flipPower);
//			SmartDashboard.putNumber("E3D Throttle axis", op.e3d.getAxis(E3D.Axis.THROTTLE));
//			robot.lift.driveNoFeedback(liftPower, flipPower);
//		}
		
	}

	/**
	 * Called when the opmode is stopped.
	 */
	public void stop() {
		super.stop();
		Logging.h("Stopping teleop");
	}
}
