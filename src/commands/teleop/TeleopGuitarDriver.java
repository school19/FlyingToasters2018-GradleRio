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
public class TeleopGuitarDriver extends OpMode {
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
	public TeleopGuitarDriver(Robot bot) {
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
		guitar.poll();
		// drive the derivebase
		robot.driveBase.driveGrilledCheese(guitar.getAxis(Harmonix.Axis.WHAMMY_BAR) * guitar.getAxis(Harmonix.Axis.STRUM), guitar.getAxis(Harmonix.Axis.BUTTONS));
		// log position
		Logging.l("left enc.: " + robot.driveBase.left.getPosition());
		Logging.l("Right enc.:" + robot.driveBase.right.getPosition());
	}

	/**
	 * Called when the opmode is stopped.
	 */
	public void stop() {
		super.stop();
		Logging.h("Stopping teleop");
	}
}
