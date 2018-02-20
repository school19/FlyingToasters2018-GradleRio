package commands.autonomous;

import org.usfirst.frc.team3641.robot.Robot;

import commands.IntakeCommand;
import commands.LiftCommand;
import commands.MotionProfileCommand;
import commands.interfaces.Command;
import commands.interfaces.OpMode;
import edu.wpi.first.wpilibj.DriverStation;
import hardware.Intake;
import hardware.Intake.State;
import hardware.Lift.Positions;
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
public class LeftScaleAuton extends OpMode {
	private Waypoint[] leftPath = { new Waypoint(new Point(0, 0), 0), new Waypoint(new Point(5.5, 0), 0),
			new Waypoint(new Point(7.2, -0.7), -Math.PI / 4.0) };
	private Waypoint[] rightPath = { new Waypoint(new Point(0, 0), 0), new Waypoint(new Point(4, 0.2), 0),
			new Waypoint(new Point(5.65, -1.5), -Math.PI / 2.0), new Waypoint(new Point(5.65, -2.9), -Math.PI / 2.0) };
	private Waypoint[] rightPath2 = { new Waypoint(new Point(5.65, -2.9), -Math.PI / 2.0),
			new Waypoint(new Point(5.65, -4.75), -Math.PI / 2.0), new Waypoint(new Point(7, -6.0), Math.PI / 4) };

	private MotionProfileCommand mpCommand;
	private MotionProfileCommand rightMpCommand2;
	private boolean left;
	
	private LiftCommand flip;
	private LiftCommand raise;
	private LiftCommand lower;
	private IntakeCommand output;
	
	/**
	 * constructor for the left scale plate auton.
	 * 
	 * @param bot
	 */
	public LeftScaleAuton(Robot bot) {
		super(bot, "Left Scale auton");
		
		flip = new LiftCommand(this, bot, Positions.STARTING_FLIP);
		raise = new LiftCommand(this, bot, Positions.H_SCALE);
		lower = new LiftCommand(this, bot, Positions.GROUND);
		output = new IntakeCommand(this, bot, State.OUTPUTTING);
		
		String gameData = DriverStation.getInstance().getGameSpecificMessage();
		Logging.h(gameData);
		if (gameData.charAt(1) == 'L') {
			left = true;
			mpCommand = new MotionProfileCommand(this, robot, "mp command", true, MotionProfileCommand.Speed.MED_LOW_ACCEL, leftPath);
		} else {
			left = false;
			mpCommand = new MotionProfileCommand(this, robot, "mp command", true, MotionProfileCommand.Speed.MED, rightPath);
			rightMpCommand2 = new MotionProfileCommand(this, robot, "Mp command 2", true, MotionProfileCommand.Speed.MED_LOW_ACCEL, rightPath2);
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
			if (!left) {
				addCommand(rightMpCommand2);
			} else {
				addCommand(flip);
			}
		} else if (cmd == rightMpCommand2) {
			addCommand(flip);
		} else if(cmd == flip) {
			addCommand(raise);
		} else if(cmd == raise) {
			addCommand(output);
		} else if(cmd == output) {
			addCommand(lower);
		}
		super.commandFinished(cmd);
	}
}
