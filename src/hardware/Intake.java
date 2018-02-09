package hardware;

import edu.wpi.first.wpilibj.DigitalInput;
/**
 * Intake class
 * 
 * @author ben
 *
 */
public class Intake {

	/* 2 motors for intake */
	static final int leftMotorID = 5;
	static final int rightMotorID = 8;
	static final int cubeSwitchPort = 0;

	private Talon leftTalon;
	private Talon rightTalon;
	private DigitalInput cubeSwitch;

	public Intake() {

		leftTalon = new Talon(leftMotorID);
		rightTalon = new Talon(rightMotorID);
		cubeSwitch = new DigitalInput(cubeSwitchPort);

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
	
	public boolean hasCube() {
		return cubeSwitch.get();
	}

}
