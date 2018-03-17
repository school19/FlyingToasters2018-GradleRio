package hardware;

import commands.DelayedCommand;
import commands.LiftCommand;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import hardware.Lift.Positions;
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
	
	boolean autoliftEnabled = true;
	
	//Used to delay lifting
	//private DelayedCommand delayLift;
	
	//Used for lifting slightly when a cube is gotten
	private Lift lift;

	private State currentState = State.RESTING;
	private double time;
	private final double timeWithoutCube = .548;
	private final double maxRecoveryTime = 1;

	private final double defaultInSpeed = 1.0;
	private final double defaultOutSpeed = 0.65;
	private final double slowOutSpeed = 0.6;

	public static enum State {
		INTAKING, OUTPUTTING, RESTING, RESTING_WITH_CUBE, HAS_CUBE, RESET, RECOVERY, OUTPUTTING_SLOW,
	}

	public Intake(Lift lift) {
		leftTalon = new Talon(leftMotorID);
		rightTalon = new Talon(rightMotorID);
		leftTalon.setInverted(true);
		rightTalon.setInverted(false);
		cubeSwitch = new DigitalInput(cubeSwitchPort);
		this.lift = lift;
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
			if (time >= maxRecoveryTime) {
				//Lift up a bit if it's at the ground to avoid damage or losing the cube
				if(lift.currentPos == Positions.GROUND_TILT && autoliftEnabled) lift.trackToPos(Positions.GROUND);
				setState(State.RESET);
			}
		case INTAKING:
			setPower(-defaultInSpeed);
			if (hasCube()) {
				setState(State.HAS_CUBE);
				//Lift up a bit if it's at the ground to avoid damage or losing the cube
				if(lift.currentPos == Positions.GROUND && autoliftEnabled) lift.trackToPos(Positions.GROUND_TILT);
			}
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

	
	public void enableAutolift(boolean e) {
		autoliftEnabled = e;
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
