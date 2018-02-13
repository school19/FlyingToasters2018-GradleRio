package hardware;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;

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
	 * the motor which drives the lift
	 */
	private FeedbackTalon liftMotor;

	/**
	 * Constructor. creates a new lift object and initializes the motors
	 */
	public Lift() {
		liftMotor = new FeedbackTalon(TALON_ID, FeedbackDevice.Analog);
		liftMotor.setupMotionMagic(kF, kP, kI, kD, vel, accel);
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
}
