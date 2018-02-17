package commands.autonomous;

import org.usfirst.frc.team3641.robot.Robot;

import commands.IntakeCommand;
import commands.MotionProfileCommand;
import commands.interfaces.Command;
import commands.interfaces.OpMode;
import edu.wpi.first.wpilibj.DriverStation;
import hardware.Intake;
import path_generation.Point;
import path_generation.Waypoint;
import utilities.Logging;

//TODO add intake commands
/**
 * Drives to the left scale plate.
 * 
 * @author jack
 *
 */
public class RightScaleAuton extends OpMode {
	private Waypoint[] rightPath = { new Waypoint(new Point(0, 0), 0), new Waypoint(new Point(5.5, 0), 0),
			new Waypoint(new Point(7.2, 0.7), Math.PI / 4.0) };
	private Waypoint[] leftPath = { new Waypoint(new Point(0, 0), 0), new Waypoint(new Point(4, -0.2), 0),
			new Waypoint(new Point(5.65, 1.5), Math.PI / 2.0), new Waypoint(new Point(5.65, 2.9), Math.PI / 2.0) };
	private Waypoint[] leftPath2 = { new Waypoint(new Point(5.65, 2.9), Math.PI / 2.0),
			new Waypoint(new Point(5.65, 4.75), Math.PI / 2.0), new Waypoint(new Point(7, 6.0), -Math.PI / 4) };

	private MotionProfileCommand mpCommand;
	private MotionProfileCommand leftMpCommand2;
	private boolean left;

	/**
	 * constructor for the left scale plate auton.
	 * 
	 * @param bot
	 */
	public RightScaleAuton(Robot bot) {
		super(bot, "Left Scale auton");
		String gameData = DriverStation.getInstance().getGameSpecificMessage();
		Logging.h(gameData);
		if (gameData.charAt(1) == 'L') {
			left = true;
			mpCommand = new MotionProfileCommand(this, robot, "mp command", true, leftPath);
			leftMpCommand2 = new MotionProfileCommand(this, robot, "Mp command 2", true, leftPath2);
		} else {
			left = false;
			mpCommand = new MotionProfileCommand(this, robot, "mp command", true, rightPath);
		}

	}

	/**
	 * called once when initialized.
	 */
	public void init() {
		super.init();
		addCommand(mpCommand);
	}

	/**
	 * called periodically when opmode is running.
	 */
	public void periodic(double deltaTime) {
		super.periodic(deltaTime);
		Logging.l("Left pos: " + robot.driveBase.left.getPosition() + ", right pos: "
				+ robot.driveBase.right.getPosition());
	}

	/**
	 * called once when the opmode is stopped.
	 */
	public void stop() {
		robot.driveBase.setFeedbackActive(false);
	}

	public void commandFinished(Command cmd) {
		// Add the intake command to output the cube.
		if (cmd == mpCommand) {
			if (left) {
				addCommand(leftMpCommand2);
			} else {
				addCommand(new IntakeCommand(this, robot, Intake.State.OUTPUTTING));
			}
		} else if (cmd == leftMpCommand2) {
			addCommand(new IntakeCommand(this, robot, Intake.State.OUTPUTTING));
		}
		super.commandFinished(cmd);
	}
}
