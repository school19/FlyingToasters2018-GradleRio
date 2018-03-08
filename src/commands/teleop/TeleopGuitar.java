package commands.teleop;

import org.usfirst.frc.team3641.robot.Robot;

import commands.interfaces.OpMode;
import commands.teleop.Harmonix.Button;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import hardware.Intake;
import hardware.Lift;
import hardware.Lift.Positions;
import utilities.Logging;

/**
 * Teleop class for human-operated control.
 * 
 * @author jack
 *
 */
public class TeleopGuitar extends OpMode {
	/**
	 * The PS4 controller the driver uses to control the robot
	 */
	private PS4 ps4;
	private Harmonix guitar;

	/**
	 * Constructor
	 * 
	 * @param bot
	 *            the Robot that's being controlled.
	 */
	public TeleopGuitar(Robot bot) {
		super(bot, "Teleop");
		ps4 = new PS4(0);
		guitar = new Harmonix(1);
	}

	/**
	 * Called once when the opmode starts. Doesn't really do anything yet.
	 */
	public void init() {
		super.init();
		Logging.h("Starting teleop");
		// Disables feedback control!!!! (OOPS WE DIDNT thiNK OF ThiS UNtiL BAG Day!!!)
		robot.driveBase.setFeedbackActive(false);
		robot.driveBase.left.setFeedbackController(null);
		robot.driveBase.right.setFeedbackController(null);

		if (SmartDashboard.getBoolean("Manual enabled", true)) {
			robot.lift.disableMotionMagic();
		}
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
		guitar.poll();
		// drive the derivebase
		robot.driveBase.driveGrilledCheese(ps4.getAxis(PS4.Axis.LEFT_Y), -ps4.getAxis(PS4.Axis.RIGHT_X));
		// log position
		Logging.l("left enc.: " + robot.driveBase.left.getPosition());
		Logging.l("Right enc.:" + robot.driveBase.right.getPosition());

		// set the power of the intake based on the user inputs.

		if (guitar.isPressed(Button.STRUM_UP))
			robot.intake.setState(Intake.State.INTAKING);
		else if (guitar.isPressed(Button.STRUM_DOWN))
			robot.intake.setState(Intake.State.OUTPUTTING);
		/*
		 * else if (guitar.isPressed(6))
		 * robot.intake.setState(Intake.State.OUTPUTTING_SLOW);
		 */
		else if (guitar.isReleased(Button.STRUM_UP) && robot.intake.getState() == Intake.State.INTAKING)
			robot.intake.setState(Intake.State.RECOVERY);
		else if (guitar.isReleased(Button.STRUM_DOWN) && robot.intake.getState() == Intake.State.OUTPUTTING)
			robot.intake.setState(Intake.State.RESET);
		/*
		 * else if (guitar.isReleased(6) && robot.intake.getState() ==
		 * Intake.State.OUTPUTTING_SLOW) robot.intake.setState(Intake.State.RESET);
		 */
		// else if (robot.intake.getState() == Intake.State.RESTING)
		// robot.intake.setPower(e3d.getAxis(E3D.AxisX));

		// move the lift
		if (guitar.isPressed(Button.ORANGE))
			robot.lift.trackToPos(Lift.Positions.GROUND);
		else if (guitar.isPressed(Button.BLUE))
			robot.lift.trackToPos(Lift.Positions.SWITCH);
		else if (guitar.isPressed(Button.YELLOW))
			robot.lift.trackToPos(Positions.H_SWITCH);
		else if (guitar.isPressed(Button.RED))
			robot.lift.trackToPos(Lift.Positions.L_SCALE);
		else if (guitar.isPressed(Button.GREEN))
			robot.lift.trackToPos(Lift.Positions.H_SCALE);
		// log data about the lift's position, velocity, and error to the smartdashboard
		// to help tune PIDs
		robot.lift.logToDashboard();

		// TODO Add guitar reset button or fix the lift
		/*
		 * if(guitar.isPressed(E3D.Button.THUMB)) { robot.lift.resetError(); }
		 */
	}

	/**
	 * Called when the opmode is stopped.
	 */
	public void stop() {
		super.stop();
		Logging.h("Stopping teleop");
	}
}
