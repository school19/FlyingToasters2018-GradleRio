package commands;

import org.usfirst.frc.team3641.robot.Robot;

import commands.interfaces.Command;
import commands.interfaces.CommandCallback;
import path_generation.Path;
import path_generation.Waypoint;
import utilities.Logging;

public class MotionProfileCommand extends Command {
	public static double END_TIME_EXTRA = 1;
	
	private Waypoint[] wp;
	private boolean backwards;
	private Robot bot;
	private Path path;
	
	private double time = 0;
	private double endTime;
	public MotionProfileCommand(CommandCallback opMode, Robot robot, String name, boolean isBackwards,
			Waypoint... waypoints) {
		super(opMode, name);
		wp = waypoints;
		backwards = isBackwards;
		bot = robot;
		//generate path
		path = new Path(wp);
		Logging.l(path);
		endTime = path.endTime + END_TIME_EXTRA;
	}

	public void init() {
		time = 0;
		bot.driveBase.drivePath(path, backwards);
	}
	
	public void periodic(double deltaTime) {
		super.periodic(deltaTime);
		time += deltaTime;
		if(time >= endTime) {
			endCommand();
		}
	}
}
