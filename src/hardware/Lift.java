package hardware;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import utilities.Logging;

/**
 * Class for the lift mechanism.
 * 
 * @author jack
 *
 */
public class Lift {
	/**
	 * Id of the talon driving the lift motor
	 */
	static final int LIFT_TALON_ID = 5;
	static final int LIFT_FOLLOWER_ID = 8;
	
	/**
	 * Id of the talon driving the flip motor
	 */
	static final int FLIP_TALON_ID = 7;
	
	static final int LIMIT_SWITCH_PORT = 1;

	/**
	 * The parameters used in initialization of the lift talon, like PID values
	 * 
	 * @author jack
	 *
	 */
	private static class LiftTalonParams {
		double kF = 0;
		double kP = 50;
		double kI = 0.02;
		double kD = 3;
		int vel = 50;
		int accel = 50;
	}

	/**
	 * The parameters used in initialization of the lift talon, like PID values
	 * 
	 * @author jack
	 *
	 */
	private class FlipTalonParams {
		double kF = 0;
		double kP = 40;
		double kI = 0;
		double kD = 4;
		int vel = 25;
		int accel = 50;
	}

	/**
	 * The minimum position at which the thing can flip at.
	 */
	static final double FLIP_MIN_POS = -483;

	/**
	 * The different positions to go to, in raw potentiometer values.
	 * 
	 * @author jack
	 *
	 */
	public enum Positions {
		GROUND(-280, 537), SWITCH(-455, 537), L_SCALE(-625, 400), H_SCALE(-695, 419), STARTING(-487,
				389), STARTING_FLIP(-487, 537);

		double liftPos;
		double flipPos;

		Positions(double lP, double fP) {
			liftPos = lP;
			flipPos = fP;
		}
	}

	/**
	 * The current position of the stuff
	 */
	public Positions currentPos = Positions.STARTING;
	/**
	 * the motor which drives the lift
	 */
	private FeedbackLinkedCAN liftMotor;
	private FeedbackTalon flipMotor;
	private DigitalInput limitSwitch;
	private boolean limSwitchVal = false;
	private boolean lastSwitchVal = false;
	/**
	 * Whether the lift is active. If true, feedback loop is run, if false it's set
	 * to 0 all the time.
	 */
	private boolean active = true;

	/**
	 * Stores the parameters for the lift talon
	 */
	LiftTalonParams liftParams = new LiftTalonParams();
	/**
	 * Stores the parameters for the flip talon
	 */
	FlipTalonParams flipParams = new FlipTalonParams();

	/**
	 * Constructor. creates a new lift object and initializes the motors
	 */
	public Lift() {
		addTuningToDashboard();

		FeedbackTalon liftFeedbackTalon = new FeedbackTalon(LIFT_TALON_ID, FeedbackDevice.Analog);
		Talon liftFollowerTalon = new Talon(LIFT_FOLLOWER_ID);
		liftFollowerTalon.talon.setInverted(true);
		liftFeedbackTalon.talon.setInverted(true);
		liftMotor = new FeedbackLinkedCAN(liftFeedbackTalon, liftFollowerTalon);

		flipMotor = new FeedbackTalon(FLIP_TALON_ID, FeedbackDevice.Analog);
		limitSwitch = new DigitalInput(LIMIT_SWITCH_PORT);

		setupMotionMagic();
		trackToPos(Positions.STARTING);
	}

	/**
	 * sets up motion magic on motor controllers
	 */
	private void setupMotionMagic() {
		flipMotor.setupMotionMagic(flipParams.kF, flipParams.kP, flipParams.kI, flipParams.kD, flipParams.vel,
				flipParams.accel);
		liftMotor.feedbackTalon.setupMotionMagic(liftParams.kF, liftParams.kP, liftParams.kI, liftParams.kD,
				liftParams.vel, liftParams.accel);
	}

	/**
	 * Disables motion magic
	 */
	public void disableMotionMagic() {
		flipMotor.stopMotionMagic();
		liftMotor.feedbackTalon.stopMotionMagic();
	}

	/**
	 * Sets the lift to be active(runs feedback control stuff) or inactive (motor at
	 * 0)
	 * 
	 * @param isActive
	 *            whether to be active
	 */
	public void setActive(boolean isActive) {
		active = isActive;
	}

	/**
	 * Drive the lift with no feedback control.
	 * 
	 * @param liftPow
	 *            the raw power to the lift
	 * @param flipPow
	 *            the raw power to the flipper
	 */
	public void driveNoFeedback(double liftPow, double flipPow) {
		liftMotor.setPower(liftPow);
		flipMotor.setPower(flipPow);
	}

	/**
	 * Drives the lift to the given position.
	 * 
	 * @param position
	 *            The position to go to.
	 */
	public void trackToPos(Positions position) {
		Logging.m("Set setpoint run!");
		if (currentPos == Positions.STARTING && (position != Positions.STARTING && position != Positions.H_SCALE)) {
			currentPos = Positions.STARTING_FLIP;
		} else {
			currentPos = position;
		}
		liftMotor.feedbackTalon.setSetpoint(currentPos.liftPos);
		if (liftMotor.feedbackTalon.getRawPosition() < FLIP_MIN_POS) {
			flipMotor.setSetpoint(currentPos.flipPos);
		} else {
			flipMotor.setSetpoint(Positions.GROUND.flipPos);
		}
	}

	/**
	 * Runs the closed loop motion magic controller thingy on the lift
	 */
	public void periodic() {
		lastSwitchVal = limSwitchVal;
		limSwitchVal = !limitSwitch.get();
		if (active) {
			if(currentPos == Positions.GROUND && (limSwitchVal && !lastSwitchVal)) resetError();
			if (currentPos == Positions.STARTING) {
				flipMotor.setSetpoint(currentPos.flipPos);
			} else {
				if (liftMotor.feedbackTalon.getRawPosition() < FLIP_MIN_POS) {
					flipMotor.setSetpoint(currentPos.flipPos);
				} else {
					flipMotor.setSetpoint(Positions.GROUND.flipPos);
				}
			}
			liftMotor.runFeedback(0);
			flipMotor.runFeedback(0);
		}
		if(SmartDashboard.getBoolean("Reset Error", false) /* || (limitSwitch.get() && currentPos == Positions.GROUND) */) {
			resetError();
			SmartDashboard.putBoolean("Reset Error", false);
			Logging.h("Reset Lift and Flip Error");
		}
	}
	
	/**
	 * Zero both POTs to the current position.
	 */
	public void resetError() {
		double liftError = liftMotor.feedbackTalon.getRawCLError();
		double flipError = flipMotor.getRawCLError();
		SmartDashboard.putNumber("Lift Offset", liftError);
		SmartDashboard.putNumber("Flip Offset", flipError);
		
		liftMotor.feedbackTalon.talon.setSelectedSensorPosition((int)currentPos.liftPos, 0, 20);
		flipMotor.talon.setSelectedSensorPosition((int)currentPos.flipPos, 0, 20);
		
		SmartDashboard.putNumber("Current Position", currentPos.liftPos);

		SmartDashboard.putBoolean("Reset Error", false);
		Logging.h("Reset Lift and Flip Error");
		
		flipMotor.talon.set(ControlMode.PercentOutput, 0);
	}
	
	/**
	 * 
	 * @return the total error of the lift and flipper
	 */
	public double getTotalError() {
		return Math.abs(liftMotor.feedbackTalon.getRawPosition() - currentPos.liftPos) + Math.abs(flipMotor.getRawPosition() - currentPos.flipPos);
	}

	/**
	 * Writes the current position, velocity, and closed loop error to the
	 * dashboard.position
	 */
	public void logToDashboard() {
		SmartDashboard.putString("Lift setpoint name", currentPos.name());
		SmartDashboard.putNumber("lift encoder pos", liftMotor.feedbackTalon.getRawPosition());
		SmartDashboard.putNumber("lift closed loop error", liftMotor.feedbackTalon.getRawCLError());
		SmartDashboard.putNumber("flip encoder pos", flipMotor.getRawPosition());
		SmartDashboard.putNumber("flip closed loop error", flipMotor.getRawCLError());
		SmartDashboard.putNumber("flip talon output voltage", flipMotor.talon.getMotorOutputVoltage());
		SmartDashboard.putNumber("Lift motor output voltage", liftMotor.feedbackTalon.talon.getMotorOutputVoltage());
		SmartDashboard.putBoolean("Lift lim switch", limSwitchVal);
	}

	/**
	 * Read PIDF values from the dashboard
	 */
	public void readTuningValuesFromDashboard() {
		Logging.h("Reading pid tuning values");
		liftParams.kP = SmartDashboard.getNumber("lift_kp", liftParams.kP);
		liftParams.kI = SmartDashboard.getNumber("lift_ki", liftParams.kI);
		liftParams.kD = SmartDashboard.getNumber("lift_kd", liftParams.kD);
		liftParams.kF = SmartDashboard.getNumber("lift_kf", liftParams.kF);
		liftParams.vel = (int) SmartDashboard.getNumber("lift_vel", liftParams.vel);
		liftParams.accel = (int) SmartDashboard.getNumber("lift_accel", liftParams.accel);

		flipParams.kP = SmartDashboard.getNumber("flip_kp", flipParams.kP);
		flipParams.kI = SmartDashboard.getNumber("flip_ki", flipParams.kI);
		flipParams.kD = SmartDashboard.getNumber("flip_kd", flipParams.kD);
		flipParams.kF = SmartDashboard.getNumber("flip_kf", flipParams.kF);
		flipParams.vel = (int) SmartDashboard.getNumber("flip_vel", flipParams.vel);
		flipParams.accel = (int) SmartDashboard.getNumber("flip_accel", flipParams.accel);

		setupMotionMagic();
	}

	/**
	 * write PIDF values to the dashboard
	 */
	public void addTuningToDashboard() {
		SmartDashboard.putNumber("lift_kp", liftParams.kP);
		SmartDashboard.putNumber("lift_ki", liftParams.kI);
		SmartDashboard.putNumber("lift_kd", liftParams.kD);
		SmartDashboard.putNumber("lift_kf", liftParams.kF);
		SmartDashboard.putNumber("lift_vel", liftParams.vel);
		SmartDashboard.putNumber("lift_accel", liftParams.accel);

		SmartDashboard.putNumber("flip_kp", flipParams.kP);
		SmartDashboard.putNumber("flip_ki", flipParams.kI);
		SmartDashboard.putNumber("flip_kd", flipParams.kD);
		SmartDashboard.putNumber("flip_kf", flipParams.kF);
		SmartDashboard.putNumber("flip_vel", flipParams.vel);
		SmartDashboard.putNumber("flip_accel", flipParams.accel);
		
		SmartDashboard.putBoolean("Reset Error", false);
	}
}
