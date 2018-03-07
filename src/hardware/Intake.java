package hardware;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import utilities.Logging;

/**
 * Intake class
 * 
 * @author ben
 *
 */
public class Intake {

	/* 2 motors for intake */
	static final int leftMotorID = 6;
	static final int rightMotorID = 9;
	static final int cubeSwitchPort = 0;

	private Talon leftTalon;
	private Talon rightTalon;
	private DigitalInput cubeSwitch;
	private boolean currentSwitchStatus = false;

	private State currentState = State.RESTING;
	private double time;
	private final double timeWithoutCube = .548;
	private final double maxRecoveryTime = 1;

	private final double defaultInSpeed = 0.67;
	private final double defaultOutSpeed = 0.4503;
	private final double slowOutSpeed = 0.26;

	public static enum State {
		INTAKING, OUTPUTTING, RESTING, RESTING_WITH_CUBE, HAS_CUBE, RESET, RECOVERY, OUTPUTTING_SLOW,
	}

	public Intake() {
		leftTalon = new Talon(leftMotorID);
		rightTalon = new Talon(rightMotorID);
		leftTalon.setInverted(true);
		rightTalon.setInverted(true);
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

	public void periodic(double deltaTime) {
		pollSwitch();
		switch (currentState) {
		case RECOVERY:
			time += deltaTime;
			if (time >= maxRecoveryTime)
				setState(State.RESET);
		case INTAKING:
			setPower(-defaultInSpeed);
			if (hasCube())
				setState(State.HAS_CUBE);
			break;
		case OUTPUTTING:
			setPower(defaultOutSpeed);
			if (hasCube())
				time = 0;
			else
				time += deltaTime;
			if (time >= timeWithoutCube)
				setState(State.RESET);
			break;
		case OUTPUTTING_SLOW:
			setPower(slowOutSpeed);
			if (hasCube())
				time = 0;
			else
				time += deltaTime;
			if (time >= timeWithoutCube)
				setState(State.RESET);
			break;
		case RESET:
			setPower(0);
			setState(State.RESTING);
			break;
		case HAS_CUBE:
			setPower(0);
			setState(State.RESTING_WITH_CUBE);
			break;
		case RESTING_WITH_CUBE:
			time = 0;
			if (!hasCube())
				setState(State.RECOVERY);
			break;
		case RESTING:
		default:
			break;
		}
		SmartDashboard.putString("Intake State", currentState.toString());
		SmartDashboard.putBoolean("Has Cube?", hasCube());
		SmartDashboard.putNumber("Intake Time", time);
	}

	public void setState(State newState) {
		Logging.h("Switching Intake from " + currentState.toString() + " to " + newState.toString());
		currentState = newState;
		time = 0;
	}

	public void pollSwitch() {
		currentSwitchStatus = !cubeSwitch.get();
	}

	public State getState() {
		return currentState;
	}

	public boolean hasCube() {
		return currentSwitchStatus;
	}

}
