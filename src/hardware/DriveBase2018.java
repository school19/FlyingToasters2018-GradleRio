package hardware;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import controllers.PIDcontroller;
import controllers.motion_profiles.MotionProfile;
import controllers.motion_profiles.SkidsteerProfileGenerator;
import controllers.motion_profiles.WheelProfileGenerator;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import hardware.interfaces.DriveBase;
import path_generation.Path;
import path_generation.Waypoint;
import utilities.Logging;
import utilities.Utilities;

/**
 * The 2018 robot drivertrain.
 * 
 * @author jack
 *
 */
public class DriveBase2018 extends DriveBase {
	public FeedbackLinkedCAN left;
	public FeedbackLinkedCAN right;
	
	private final static double MAX_VELOCITY = 3500;
	
	public final static double wheelDistance = 0.665;
	//public final static double wheelDistance = 0.735;
	
	final static double velGain = 0.25;
	final static double accelGain = 0.005;

	private PIDcontroller leftMotionProfilePID = new PIDcontroller(7.5, 20, 0.375);
	private PIDcontroller rightMotionProfilePID = new PIDcontroller(7.5, 20, 0.375);
	private WheelProfileGenerator leftProfileGen;
	private WheelProfileGenerator rightProfileGen;

	public MotionProfile leftMotionProfile;
	public MotionProfile rightMotionProfile;

	public double leftPower = 0;
	public double rightPower = 0;

	public enum Motors {
		LEFT0(3), LEFT1(4), RIGHT0(1), RIGHT1(2);

		public int id;

		Motors(int talonID) {
			id = talonID;
		}

		public Talon getTalon() {
			return new Talon(id);
		}

		public Victor getVictor() {
			return new Victor(id);
		}
	}

	public DriveBase2018() {
		super();
		left = new FeedbackLinkedCAN(FeedbackDevice.CTRE_MagEncoder_Absolute, Motors.LEFT1.id,
				Motors.LEFT0.getVictor());
		left.feedbackTalon.talon.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_20Ms, 0);
		right = new FeedbackLinkedCAN(FeedbackDevice.CTRE_MagEncoder_Absolute, Motors.RIGHT1.id,
				Motors.RIGHT0.getVictor());
		right.feedbackTalon.talon.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_20Ms, 0);
		right.setInverted(true);
		left.setEncoderReversed(true);
		
		left.setCurrentLimit(50);
		left.enableCurrentLimit(true);
		right.setCurrentLimit(50);
		right.enableCurrentLimit(true);
		
		// add the motor controllers to the list to be updated
		registerMotorController(left);
		registerMotorController(right);

		// TODO set offsets appropriately
		leftProfileGen = new SkidsteerProfileGenerator(-wheelDistance / 2);
		rightProfileGen = new SkidsteerProfileGenerator(wheelDistance / 2);

		leftMotionProfilePID.setDOnMeasurement(false);
		rightMotionProfilePID.setDOnMeasurement(false);

		leftMotionProfile = new MotionProfile(leftMotionProfilePID, velGain, accelGain, leftProfileGen);
		rightMotionProfile = new MotionProfile(rightMotionProfilePID, velGain, accelGain, rightProfileGen);
	}

	public double getWheelVelocity() {
		double vl = Math.abs(left.feedbackTalon.getRawVelocity());
		double vr = Math.abs(right.feedbackTalon.getRawVelocity());
		double v = Math.min(Math.max(vl,vr),MAX_VELOCITY)/MAX_VELOCITY;
		v = Utilities.expInput(v,1.4);
		return v;
	}
	
	public void update(double dT) {
		super.update(dT);
		SmartDashboard.putNumber("left current", left.feedbackTalon.talon.getOutputCurrent());
		SmartDashboard.putNumber("right current", right.feedbackTalon.talon.getOutputCurrent());
		SmartDashboard.putNumber("left position", left.getPosition());
		SmartDashboard.putNumber("right position", right.getPosition());
		leftMotionProfile.writeErrorToDashboard("left MP error");
		rightMotionProfile.writeErrorToDashboard("right MP error");
	}

	@Override
	public void drive(double... inputs) {
		if (inputs.length == 2) {
			driveArcade(inputs[0], inputs[1]);
		} else {
			Logging.e("Invalid number of inputs to drive");
		}
	}

	public double driveArcade(double power, double turn) {
		double leftPower = power + turn;
		double rightPower = power - turn;
		return driveTank(leftPower, rightPower);
	}
	
	public double driveTank(double leftPower, double rightPower) {
		leftPower = Math.max(Math.min(leftPower, 1),-1);
		rightPower = Math.max(Math.min(rightPower, 1),-1);
		left.setPower(leftPower);
		right.setPower(rightPower);
		return Math.sqrt(leftPower*leftPower + rightPower*rightPower)/2.0;
	}

	public double drivePureCheese(double power, double rotation) {
		double gain = 1.1;
		double exp = 1.375;

		rotation = Utilities.expInput(rotation, exp);
		double outputPower = Utilities.expInput(power, exp);

		double cheesyRotation = rotation * gain * Math.abs(outputPower);
		
		return driveArcade(outputPower, cheesyRotation);
	}
	
	public double driveGrilledCheese(double power, double rotation) {
		double gain = 1;
		double limit = 0.25;
		double subLimitWeight = .8;
		double exp = 1.5;

		rotation = Utilities.expInput(rotation, exp);
		double outputPower = Utilities.expInput(power, exp);

		double arcadeRotation = rotation;
		double cheesyRotation = rotation * gain * Math.abs(outputPower);
		double arcadeWeight = (1 - Math.abs(power) / limit / subLimitWeight);
		double cheesyWeight = (Math.abs(power) / limit * subLimitWeight);

		double outputRotation = cheesyRotation;
		if (Math.abs(power) <= limit)
			outputRotation = cheesyWeight * cheesyRotation + arcadeWeight * arcadeRotation;

		return driveArcade(outputPower, outputRotation);
	}

	public void drivePath(Path p, boolean isBackwards) {
		// reset motion profiles
		leftMotionProfile.reset();
		rightMotionProfile.reset();

		// generate profiles
		leftMotionProfile.generateProfileFromPath(p, isBackwards);
		rightMotionProfile.generateProfileFromPath(p, isBackwards);

		// set offsets
		leftMotionProfile.setOffset(left.getPosition());
		rightMotionProfile.setOffset(right.getPosition());

		// enable them
		left.setFeedbackController(leftMotionProfile);
		right.setFeedbackController(rightMotionProfile);
		left.setFeedbackActive(true);
		right.setFeedbackActive(true);
	}

	/**
	 * drive from one waypoint to another
	 * 
	 * @param from
	 *            starting waypoint
	 * @param to
	 *            ending waypoint
	 */
	public void driveFromTo(Waypoint from, Waypoint to, boolean isBackwards) {
		// generate path then drive it
		Path path = new Path(from, to);
		Logging.l(path);
		drivePath(path, isBackwards);
	}

	public void driveFromTo(Waypoint from, Waypoint to, boolean isBackwards, double vel, double accel) {
		// generate path then drive it
		Path path = new Path(from, to);
		Logging.l(path);
		drivePath(path, isBackwards);
	}

	/**
	 * Drives a series of waypoints, similar to driveFromTo
	 * 
	 * @param waypoints
	 *            the series of waypoints to drive, at least 2
	 */
	public void driveWaypoints(boolean isBackwards, Waypoint... waypoints) {
		// generate path then drive it
		Path path = new Path(waypoints);
		Logging.l(path);
		drivePath(path, isBackwards);
	}
}
