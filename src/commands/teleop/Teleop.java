package commands.teleop;

import org.usfirst.frc.team3641.robot.Robot;

import commands.interfaces.OpMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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
	/**
	 * The PS4 controller the driver uses to control the robot
	 */
	private PS4 ps4;
	private Operator op;

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
		
		if (SmartDashboard.getBoolean("Manual enabled", true)) {
			robot.lift.disableMotionMagic();
		}
		robot.lift.readTuningValuesFromDashboard();

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
		// drive the derivebase
		robot.driveBase.driveGrilledCheese(ps4.getAxis(PS4.Axis.LEFT_Y), -ps4.getAxis(PS4.Axis.RIGHT_X));
		// log position
		Logging.l("left enc.: " + robot.driveBase.left.getPosition());
		Logging.l("Right enc.:" + robot.driveBase.right.getPosition());

		// set the power of the intake based on the user inputs.

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

		// move the lift
		if (op.isPressed(Operator.Button.GROUND))
			robot.lift.trackToPos(Lift.Positions.GROUND);
		else if (op.isPressed(Operator.Button.LOW_SWITCH))
			robot.lift.trackToPos(Lift.Positions.SWITCH);
		else if (op.isPressed(Operator.Button.HIGH_SWITCH))
			robot.lift.trackToPos(Lift.Positions.H_SCALE);
		else if (op.isPressed(Operator.Button.LOW_SCALE))
			robot.lift.trackToPos(Lift.Positions.L_SCALE);
		else if (op.isPressed(Operator.Button.HIGH_SCALE))
			robot.lift.trackToPos(Lift.Positions.H_SWITCH);
		// log data about the lift's position, velocity, and error to the smartdashboard
		// to help tune PIDs
		robot.lift.logToDashboard();
		
		if(op.isPressed(Operator.Button.RESET)) {
			robot.lift.resetDown();
		} else if(op.isReleased(Operator.Button.RESET)) {
			robot.lift.stopResettingDown();
		}
		
		if(robot.intake.getState() == Intake.State.HAS_CUBE) ps4.rumbleForTime(1, false, .5);

		// Temporary manual lift control code
		// TODO remove temporary lift control code.
		
		//It is removed :P
//		if (SmartDashboard.getBoolean("Manual enabled", false)) {
//			double liftPower = -e3d.getAxis(E3D.Axis.THROTTLE) * e3d.getAxis(E3D.Axis.Y);
//			double flipPower = e3d.getAxis(E3D.Axis.THROTTLE) * e3d.getAxis(E3D.Axis.X);
//			SmartDashboard.putNumber("Lift power", liftPower);
//			SmartDashboard.putNumber("Flip power", flipPower);
//			SmartDashboard.putNumber("E3D Throttle axis", e3d.getAxis(E3D.Axis.THROTTLE));
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
