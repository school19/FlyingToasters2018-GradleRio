package commands.autonomous;

import org.usfirst.frc.team3641.robot.Robot;

import commands.IntakeCommand;
import commands.LiftCommand;
import commands.MotionProfileCommand;
import commands.interfaces.Command;
import commands.interfaces.OpMode;
import edu.wpi.first.wpilibj.DriverStation;
import hardware.Intake.State;
import hardware.Lift;
import path_generation.Point;
import path_generation.Waypoint;
import utilities.Logging;

/**
 * An autonomous mode which automatically drives to the correct side of the
 * switch based on the game data.
 * 
 * @author jack
 *
 */
public class SwitchAuton extends OpMode {
	// meters from wall to switch
	final static double switch_dist = 2.4;

	final static double switch_left = 2.0;
	final static double switch_right = -1.0;

	Waypoint start = new Waypoint(new Point(0, 0), 0);
	Waypoint end;

	MotionProfileCommand motionProfileCmd;

	LiftCommand flip;
	
	public SwitchAuton(Robot bot, String gameData) {
		super(bot, "Motion Profile Auton");
		Logging.h(gameData);
		if (gameData.charAt(0) == 'L') {
			end = new Waypoint(new Point(switch_dist, switch_left), 0.0);
		} else {
			end = new Waypoint(new Point(switch_dist, switch_right), 0.0);
		}
		motionProfileCmd = new MotionProfileCommand(this, robot, "drive to switch", false, MotionProfileCommand.Speed.MED_LOW_ACCEL, start, end);
		flip = new LiftCommand(this, bot, Lift.Positions.STARTING_FLIP);
	}

	public void init() {
		Logging.h("Init run!");

		super.init();
		addCommand(motionProfileCmd);
	}

	public void periodic(double deltaTime) {
		super.periodic(deltaTime);
		Logging.l("Left pos: " + robot.driveBase.left.getPosition() + ", right pos: "
				+ robot.driveBase.right.getPosition());
	}

	public void stop() {
		robot.driveBase.setFeedbackActive(false);
	}

	public void commandFinished(Command cmd) {
		//Add the intake command to output the cube.
		if (cmd == motionProfileCmd) {
			addCommand(flip);
		}else if(cmd == flip){
			addCommand(new IntakeCommand(this, robot, State.OUTPUTTING));
		}
		super.commandFinished(cmd);
	}
}
