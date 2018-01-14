package hardware;

import controllers.PIDcontroller;
import controllers.motion_profiles.MotionProfile;
import controllers.motion_profiles.SkidsteerProfileGenerator;
import controllers.motion_profiles.WheelProfileGenerator;
import path_generation.Path;
import path_generation.Waypoint;
import utilities.Logging;

public class Pro775DriveBase extends DriveBase{
	final static int currentLimit = 25;

	final static double velGain = -1;
	final static double accelGain = 0;
	
	private PIDcontroller leftMotionProfilePID = new PIDcontroller(0,0,0);
	private PIDcontroller rightMotionProfilePID = new PIDcontroller(0,0,0);
	
	private FeedbackLinkedCAN left;
	private FeedbackLinkedCAN right;
	
	private WheelProfileGenerator leftProfileGen;
	private WheelProfileGenerator rightProfileGen;
	
	public MotionProfile leftMotionProfile;
	public MotionProfile rightMotionProfile;
	
	public enum Talons {
		LEFT0(8),
		LEFT1(9),
		LEFT2(10),
		LEFT3(11),
		RIGHT0(5),
		RIGHT1(1),
		RIGHT2(2),
		RIGHT3(3);
		public int id;
		Talons(int talonID){
			id = talonID;
		}
		public Talon get() {
			return new Talon(id);
		}
	}
	
	public Pro775DriveBase() {
		super();
		//create the linked talons for each side of the drive base
		//TODO Fix this if the 775 drivebase ever happens.
		/*left = new FeedbackLinkedCAN(FeedbackDevice.CTRE_MagEncoder_Absolute, 
				Talon.LEFT0.id, Talon.LEFT1.id, Talon.LEFT2.id, Talon.LEFT3.id);
		right = new FeedbackLinkedCAN(FeedbackDevice.CTRE_MagEncoder_Absolute, 
				Talon.RIGHT0.id, Talon.RIGHT1.id, Talon.RIGHT2.id, Talon.RIGHT3.id);*/
		//setup current limiting
		left.setCurrentLimit(currentLimit);
		right.setCurrentLimit(currentLimit);
		left.enableCurrentLimit(true);
		right.enableCurrentLimit(true);
		
		//add the motor controllers to the list to be updated
		registerMotorController(left);
		registerMotorController(right);
		
		//TODO set offsets appropriately
		leftProfileGen = new SkidsteerProfileGenerator(-0.5);
		rightProfileGen = new SkidsteerProfileGenerator(0.5);
		
		leftMotionProfile = new MotionProfile(leftMotionProfilePID, velGain, accelGain, leftProfileGen);
		rightMotionProfile = new MotionProfile(rightMotionProfilePID, velGain, accelGain, rightProfileGen);
	}

	@Override
	public void drive(double... inputs) {
		if(inputs.length != 2){
			Logging.e("Incorrect number of inputs to drive(double... inputs): " + inputs.length);
		}else{
			//tank drive
			left.setPower(inputs[0]);
			right.setPower(inputs[1]);
		}
	}
	
	public void drivePath(Path p) {
		//generate profiles
		leftMotionProfile.generateProfileFromPath(p, left.getPosition());
		rightMotionProfile.generateProfileFromPath(p, right.getPosition());
		//enable them
		left.setFeedbackController(leftMotionProfile);
		right.setFeedbackController(rightMotionProfile);
		left.setFeedbackActive(true);
		right.setFeedbackActive(true);
	}
	
	public void driveFromTo(Waypoint from, Waypoint to) {
		//generate path then drive it
		Path path = new Path(from, to, 10, 1, 1, Path.VelocityMode.TRIANGULAR);
		Logging.h(path);
		drivePath(path);
		
	}
}
