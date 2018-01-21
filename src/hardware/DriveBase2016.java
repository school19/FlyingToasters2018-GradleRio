package hardware;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import controllers.PIDcontroller;
import controllers.motion_profiles.MotionProfile;
import controllers.motion_profiles.SkidsteerProfileGenerator;
import controllers.motion_profiles.WheelProfileGenerator;
import hardware.interfaces.DriveBase;
import path_generation.Path;
import path_generation.Waypoint;
import utilities.Logging;

public class DriveBase2016 extends DriveBase {
	public FeedbackLinkedCAN left;
	public FeedbackLinkedCAN right;
	
	final static double velGain = 0.35;
	final static double accelGain = 0.01;
	
	private PIDcontroller leftMotionProfilePID = new PIDcontroller(5,0,0.1);
	private PIDcontroller rightMotionProfilePID = new PIDcontroller(5,0,0.1);
	
	private WheelProfileGenerator leftProfileGen;
	private WheelProfileGenerator rightProfileGen;
	
	public MotionProfile leftMotionProfile;
	public MotionProfile rightMotionProfile;
	
	public double leftPower = 0;
	public double rightPower = 0;
	
	public enum Talons {
		LEFT0(3), LEFT1(4), RIGHT0(1), RIGHT1(2);

		public int id;

		Talons(int talonID){
				id = talonID;
			}

		public Talon get() {
			return new Talon(id);
		}
	}
	
	public DriveBase2016() {
		super();
		left = new FeedbackLinkedCAN(FeedbackDevice.CTRE_MagEncoder_Absolute, Talons.LEFT1.id, Talons.LEFT0.get());
		right = new FeedbackLinkedCAN(FeedbackDevice.CTRE_MagEncoder_Absolute, Talons.RIGHT1.id, Talons.RIGHT0.get());
		left.setInverted(true);
		left.setEncoderReversed(true);
		//add the motor controllers to the list to be updated
		registerMotorController(left);
		registerMotorController(right);
		
		//TODO set offsets appropriately
		leftProfileGen = new SkidsteerProfileGenerator(-0.3413125);
		rightProfileGen = new SkidsteerProfileGenerator(0.3413125);
		
		leftMotionProfilePID.setDOnMeasurement(false);
		rightMotionProfilePID.setDOnMeasurement(false);
		
		leftMotionProfile = new MotionProfile(leftMotionProfilePID, velGain, accelGain, leftProfileGen);
		rightMotionProfile = new MotionProfile(rightMotionProfilePID, velGain, accelGain, rightProfileGen);
	}
	
	@Override
	public void drive(double... inputs) {
		if(inputs.length == 2) {
			driveArcade(inputs[0],inputs[1]);
		} else {
			Logging.e("Invalid number of inputs to drive");
		}
	}
	
	public void driveArcade(double power, double turn) {
		double leftPow = power - turn;
		double rightPow = power + turn;
		left.setPower(leftPow);
		right.setPower(rightPow);
	}
	
	public void driveGrilledCheese(double power, double rotation) {
		double gain = 1;
		double limit = 0.25;
		double subLimitWeight = 2.0;
		double exp = 1.5;
		
		rotation = expInput(rotation, exp);
		double outputPower = expInput(power, exp);
		
		double arcadeRotation = rotation;
		double cheesyRotation = rotation * gain * Math.abs(outputPower);
		double arcadeWeight = (1-Math.abs(power)/limit/subLimitWeight);
		double cheesyWeight = (Math.abs(power)/limit*subLimitWeight);
		
		double outputRotation = cheesyRotation;
		if(Math.abs(power) <= limit) outputRotation = cheesyWeight*cheesyRotation + arcadeWeight*arcadeRotation;
		
		driveArcade(outputPower, outputRotation);
	}
	
	public double expInput(double input, double power) {
		if(input > 0) {
			return Math.pow(input, power);
		}else {
			return -Math.pow(-input, power);
		}
	}
	
	public void drivePath(Path p) {
		//generate profiles
		leftMotionProfile.generateProfileFromPath(p);
		rightMotionProfile.generateProfileFromPath(p);

		//set offsets
		leftMotionProfile.setOffset(left.getPosition());
		rightMotionProfile.setOffset(right.getPosition());
		
		//enable them
		left.setFeedbackController(leftMotionProfile);
		right.setFeedbackController(rightMotionProfile);
		left.setFeedbackActive(true);
		right.setFeedbackActive(true);
	}
	
	public void driveFromTo(Waypoint from, Waypoint to) {
		//generate path then drive it
		Path path = new Path(from, to);
		Logging.h(path);
		drivePath(path);
	}
}
