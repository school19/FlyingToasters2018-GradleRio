package hardware;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
	static final int LIFT_TALON_ID = 9999;
	/**
	 * Id of the talon driving the flip motor
	 */
	static final int FLIP_TALON_ID = 9999;

	/**
	 * The parameters used in initialization of the lift talon, like PID values
	 * 
	 * @author jack
	 *
	 */
	private static class LiftTalonParams {
		double kF = 0;
		double kP = 0;
		double kI = 0;
		double kD = 0;
		int vel = 1;
		int accel = 1;
	}

	/**
	 * The parameters used in initialization of the lift talon, like PID values
	 * 
	 * @author jack
	 *
	 */
	private class FlipTalonParams {
		double kF = 0;
		double kP = 0;
		double kI = 0;
		double kD = 0;
		int vel = 1;
		int accel = 1;

		double startPos = 0;
	}

	/**
	 * The minimum position at which the thing can flip at.
	 */
	static final double FLIP_MIN_POS = 0.7;

	/**
	 * The different positions to go to, in raw potentiometer values.
	 * 
	 * @author jack
	 *
	 */
	public enum Positions {
		GROUND(0, 1), SWITCH(0.5, 0), SCALE(1, 1);

		double liftPos;
		double flipPos;

		Positions(double lP, double fP) {
			liftPos = lP;
			flipPos = fP;
		}
	}

	/**
	 * The position that the lift will start at.
	 */
	static final Positions startingPos = Positions.GROUND;

	/**
	 * The current position of the stuff
	 */
	private Positions currentPos;
	/**
	 * the motor which drives the lift
	 */
	private FeedbackTalon liftMotor;
	private FeedbackTalon flipMotor;
	/**
	 * Whether the lift is active. If true, feedback loop is run, if false it's set
	 * to 0 all the time.
	 */
	private boolean active = false;
	
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
		readTuningValuesFromDashboard();
		
		liftMotor = new FeedbackTalon(LIFT_TALON_ID, FeedbackDevice.Analog);

		liftMotor.setupMotionMagic(liftParams.kF, liftParams.kP, liftParams.kI, liftParams.kD,
				liftParams.vel, liftParams.accel);

		flipMotor = new FeedbackTalon(FLIP_TALON_ID, FeedbackDevice.Analog);
		flipMotor.setupMotionMagic(flipParams.kF, flipParams.kP, flipParams.kI, flipParams.kD,
				flipParams.vel, flipParams.accel);

		//trackToPos(startingPos);
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
	 * Drives the lift to the given position.
	 * 
	 * @param position
	 *            The position to go to.
	 */
	public void trackToPos(Positions position) {
		currentPos = position;
		liftMotor.setSetpoint(position.liftPos);
		if (liftMotor.getRawPosition() > FLIP_MIN_POS) {
			flipMotor.setSetpoint(position.flipPos);
		} else {
			flipMotor.setSetpoint(Positions.GROUND.flipPos);
		}
	}

	/**
	 * Runs the closed loop motion magic controller thingy on the lift
	 */
	public void update() {
		if (active) {
			liftMotor.runFeedback(0);
			flipMotor.runFeedback(0);
			if (liftMotor.getRawPosition() > FLIP_MIN_POS) {
				flipMotor.setSetpoint(currentPos.flipPos);
			} else {
				flipMotor.setSetpoint(Positions.GROUND.flipPos);
			}
		} else {
			liftMotor.setPower(0);
			flipMotor.setPower(0);
		}
	}

	/**
	 * Writes the current position, velocity, and closed loop error to the
	 * dashboard.
	 */
	public void logToDashboard() {
		SmartDashboard.putNumber("lift pos", liftMotor.getRawPosition());
		SmartDashboard.putNumber("lift vel", liftMotor.getRawVelocity());
		SmartDashboard.putNumber("lift closed loop error", liftMotor.getRawCLError());
	}
	/**
	 * Read PIDF values from the dashboard
	 */
	public void readTuningValuesFromDashboard() {
		liftParams.kP = SmartDashboard.getNumber("lift_kp", liftParams.kP);
		liftParams.kI = SmartDashboard.getNumber("lift_ki", liftParams.kI);
		liftParams.kD = SmartDashboard.getNumber("lift_kd", liftParams.kD);
		liftParams.kF = SmartDashboard.getNumber("lift_kf", liftParams.kF);
		liftParams.vel = (int) SmartDashboard.getNumber("lift_accel", liftParams.vel);
		liftParams.accel = (int) SmartDashboard.getNumber("lift_vel", liftParams.accel);
		
		flipParams.kP = SmartDashboard.getNumber("flip_kp", flipParams.kP);
		flipParams.kI = SmartDashboard.getNumber("flip_ki", flipParams.kI);
		flipParams.kD = SmartDashboard.getNumber("flip_kd", flipParams.kD);
		flipParams.kF = SmartDashboard.getNumber("flip_kf", flipParams.kF);
		flipParams.vel = (int) SmartDashboard.getNumber("flip_accel", flipParams.vel);
		flipParams.accel = (int) SmartDashboard.getNumber("flip_vel", flipParams.accel);
		
	}
}
