package commands.autonomous;

import org.usfirst.frc.team3641.robot.Robot;

import commands.IntakeCommand;
import commands.MotionProfileCommand;
import commands.interfaces.Command;
import commands.interfaces.OpMode;
import edu.wpi.first.wpilibj.DriverStation;
import path_generation.Point;
import path_generation.Waypoint;
import utilities.Logging;

//TODO add intake commands and maybe use motionProfileCommand
/**
 * An autonomous mode which automatically drives to the correct side of the
 * switch based on the game data.
 * 
 * @author jack
 *
 */
public class SwitchAuton extends OpMode {
	// meters from wall to switch
	final static double switch_dist = 2.5;

	final static double switch_left = 2.0;
	final static double switch_right = -1.0;

	Waypoint start = new Waypoint(new Point(0, 0), 0);
	Waypoint end;

	MotionProfileCommand motionProfileCmd;

	public SwitchAuton(Robot bot) {
		super(bot, "Motion Profile Auton");
		String gameData = DriverStation.getInstance().getGameSpecificMessage();
		Logging.h(gameData);
		if (gameData.charAt(0) == 'L') {
			end = new Waypoint(new Point(switch_dist, switch_left), 0.0);
		} else {
			end = new Waypoint(new Point(switch_dist, switch_right), 0.0);
		}
		motionProfileCmd = new MotionProfileCommand(this, robot, "drive to switch", false, start, end);
	}

	public void init() {
		Logging.h("Init run!");

		super.init();
		addCommand(motionProfileCmd);
	}

	public void periodic(double deltaTime) {
		super.periodic(deltaTime);
		Logging.h("Left pos: " + robot.driveBase.left.getPosition() + ", right pos: "
				+ robot.driveBase.right.getPosition());
	}

	public void stop() {
		robot.driveBase.setFeedbackActive(false);
	}

	public void commandFinished(Command cmd) {
		super.commandFinished(cmd);
		//Add the intake command to output the cube.
		//TODO set mode properly
		if (cmd == motionProfileCmd) {
			//addCommand(new IntakeCommand(this, robot, mode));
		}
	}
}
