package hardware;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Class for the lift mechanism. Set P/I/D/F in talon firmware for now, code in
 * later once values are tuned.
 * 
 * @author jack
 *
 */
public class Lift {
	/**
	 * Id of the talon driving the lift motor
	 */
	static final int TALON_ID = 4;
	/**
	 * top sensor reading of the lift
	 */
	static final double TOP_POS = 1;
	/**
	 * bottom sensor reading of the lift
	 */
	static final double BOTTOM_POS = 0;

	static final double kF = 0;
	static final double kP = 0;
	static final double kI = 0;
	static final double kD = 0;
	static final int vel = 1;
	static final int accel = 1;

	/**
	 * The different positions to go to
	 * 
	 * @author jack
	 *
	 */
	public enum Positions {
		GROUND(0), SWITCH(0.5), SCALE(1);

		double pos;

		Positions(double p) {
			pos = p;
		}
	}

	/**
	 * The position that the lift will start at.
	 */
	private Positions startingPos = Positions.GROUND;

	/**
	 * the motor which drives the lift
	 */
	private FeedbackTalon liftMotor;
	/**
	 * Whether the lift is active. If true, feedback loop is run, if false it's set
	 * to 0 all the time.
	 */
	private boolean active = false;

	/**
	 * Constructor. creates a new lift object and initializes the motors
	 */
	public Lift() {
		liftMotor = new FeedbackTalon(TALON_ID, FeedbackDevice.Analog);
		liftMotor.setupMotionMagic(kF, kP, kI, kD, vel, accel);
		trackToPos(startingPos);
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
		liftMotor.setSetpoint(position.pos);
	}

	/**
	 * Runs the closed loop motion magic controller thingy on the lift
	 */
	public void update() {
		if (active)
			liftMotor.runFeedback(0);
		else
			liftMotor.setPower(0);
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
}
