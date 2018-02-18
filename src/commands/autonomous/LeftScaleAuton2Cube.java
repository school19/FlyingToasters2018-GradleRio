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
public class LeftScaleAuton2Cube extends OpMode {
	private Waypoint[] leftPath = { new Waypoint(new Point(0, 0), 0), new Waypoint(new Point(5.5, 0), 0),
			new Waypoint(new Point(7.2, -0.7), -Math.PI / 4.0) };
	private Waypoint[] leftGetCube = { new Waypoint(new Point(7.2, -0.7), 3 * Math.PI / 4.0),
			new Waypoint(new Point(5.5, -1.2), -3 * Math.PI / 4.0) };
	private Waypoint[] left2ndCube = { new Waypoint(new Point(5.5, -1.2), Math.PI / 4.0),
			new Waypoint(new Point(7.2, -0.7), -Math.PI / 4.0) };
	
	private Waypoint[] rightPath = { new Waypoint(new Point(0, 0), 0), new Waypoint(new Point(4, 0.2), 0),
			new Waypoint(new Point(5.65, -1.5), -Math.PI / 2.0), new Waypoint(new Point(5.65, -2.9), -Math.PI / 2.0) };
	private Waypoint[] rightPath2 = { new Waypoint(new Point(5.65, -2.9), -Math.PI / 2.0),
			new Waypoint(new Point(5.65, -4.75), -Math.PI / 2.0), new Waypoint(new Point(7.2, -6.0), Math.PI / 4) };
	private Waypoint[] rightGetCube = { new Waypoint(new Point(7.2, -6.0), -3 * Math.PI / 4),
			new Waypoint(new Point(5.5, -5.5), 3 * Math.PI / 4) };
	private Waypoint[] right2ndCube = { new Waypoint(new Point(5.5, -5.5), -Math.PI / 4),
			new Waypoint(new Point(7.2, -6.0), Math.PI / 4) };
	
	// First profile run
	private MotionProfileCommand mpCommand;
	// second one to cross the bump when going right
	private MotionProfileCommand rightMpCommand2;
	// drives to get cube
	private MotionProfileCommand getCubeCommand;
	// drives to drop the second command
	private MotionProfileCommand driveToDump2ndCube;
	private IntakeCommand intakeCommand;
	private boolean left;

	/**
	 * constructor for the left scale plate auton.
	 * 
	 * @param bot
	 */
	public LeftScaleAuton2Cube(Robot bot) {
		super(bot, "Left Scale auton");
		String gameData = DriverStation.getInstance().getGameSpecificMessage();
		Logging.h(gameData);
		if (gameData.charAt(1) == 'L') {
			left = true;
			mpCommand = new MotionProfileCommand(this, robot, "mp command", true, MotionProfileCommand.Speed.MED_LOW_ACCEL, leftPath);
			getCubeCommand = new MotionProfileCommand(this, robot, "Get left cube", false, MotionProfileCommand.Speed.MED_LOW_ACCEL,  leftGetCube);
			driveToDump2ndCube = new MotionProfileCommand(this, robot, "go to dump cube 2", true, MotionProfileCommand.Speed.MED_LOW_ACCEL, left2ndCube);
		} else {
			left = false;
			mpCommand = new MotionProfileCommand(this, robot, "mp command", true, MotionProfileCommand.Speed.MED, rightPath);
			rightMpCommand2 = new MotionProfileCommand(this, robot, "Mp command 2", true, MotionProfileCommand.Speed.MED_LOW_ACCEL, rightPath2);
			getCubeCommand = new MotionProfileCommand(this, robot, "Get right cube", false, MotionProfileCommand.Speed.MED_LOW_ACCEL, rightGetCube);
			driveToDump2ndCube = new MotionProfileCommand(this, robot, "go to dump cube 2", true, MotionProfileCommand.Speed.MED_LOW_ACCEL, right2ndCube);
		}

		intakeCommand = new IntakeCommand(this, robot, Intake.State.INTAKING);

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
		Logging.h("Command ended: " + cmd.getName());
		Logging.h("Running commands: " + commands.toString());
		// Add the intake command to output the cube.
		if (cmd == mpCommand) {
			if (!left) {
				addCommand(rightMpCommand2);
			} else {
				addCommand(new IntakeCommand(this, robot, Intake.State.OUTPUTTING));
				addCommand(getCubeCommand);
			}
		} else if (cmd == rightMpCommand2) {
			addCommand(new IntakeCommand(this, robot, Intake.State.OUTPUTTING));
			addCommand(getCubeCommand);
		} else if (cmd == getCubeCommand) {
			addCommand(intakeCommand);
		} else if (cmd == intakeCommand) {
			addCommand(driveToDump2ndCube);
		} else if (cmd == driveToDump2ndCube) {
			addCommand(new IntakeCommand(this, robot, Intake.State.OUTPUTTING));
		}
		super.commandFinished(cmd);
	}
}
