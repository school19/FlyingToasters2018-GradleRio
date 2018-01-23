package hardware;

/**
 * Intake class
 * 
 * @author ben
 *
 */
public class Intake {

	/* 2 motors for intake */
	static final int leftMotorID = 5;
	static final int rightMotorID = 6;

	private Talon leftTalon;
	private Talon rightTalon;

	public Intake() {

		leftTalon = new Talon(leftMotorID);
		rightTalon = new Talon(rightMotorID);

	}

	/**
	 * Method that sets power to the motor
	 * 
	 * @param power
	 *            assigned to the motor
	 */
	public void setPower(double power) {
		leftTalon.setPower(power);
		rightTalon.setPower(power);
		
	}
}
