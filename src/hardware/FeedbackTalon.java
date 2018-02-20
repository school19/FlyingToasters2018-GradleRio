package hardware;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import controllers.AbstractFeedbackController;
import hardware.interfaces.FeedbackMotorController;
import utilities.Logging;
import utilities.Utilities;

public class FeedbackTalon extends Talon implements FeedbackMotorController, Utilities.Conversions {
	boolean isEncoderReversed = false;
	private AbstractFeedbackController feedbackController;
	boolean feedbackActive = false;
	private boolean isMotionMagicMode = false;
	private double lastSetpoint = 0;

	public FeedbackTalon(int talonID) {
		super(talonID);
	}

	public FeedbackTalon(int talonID, FeedbackDevice device) {
		super(talonID);
		setFeedbackDevice(device);
	}

	public void setupMotionMagic(double kF, double kP, double kI, double kD, int rawVel, int rawAccel) {
		talon.config_kF(0, kF, 1000);
		talon.config_kP(0, kP, 1000);
		talon.config_kI(0, kI, 1000);
		talon.config_kD(0, kD, 1000);
		talon.configMotionCruiseVelocity(rawVel, 1000);
		talon.configMotionAcceleration(rawAccel, 1000);
		isMotionMagicMode = true;
	}

	public void stopMotionMagic() {
		isMotionMagicMode = false;
	}

	public double getRawPosition() {
		return talon.getSelectedSensorPosition(0);
	}

	public double getRawVelocity() {
		return talon.getSelectedSensorVelocity(0);
	}

	public double getRawCLError() {
		return talon.getClosedLoopError(0);
	}

	@Override
	public double getPosition() {
		double d = Distance.ENCODER_TICK.convert(talon.getSelectedSensorPosition(0), Distance.M);
		if (isEncoderReversed) {
			return -d;
		} else {
			return d;
		}
	}

	@Override
	public void setFeedbackController(AbstractFeedbackController controller) {
		feedbackController = controller;
	}

	@Override
	public void setFeedbackActive(boolean active) {
		feedbackActive = active;
	}

	@Override
	public boolean getFeedbackActive() {
		return feedbackActive;
	}

	@Override
	public void runFeedback(double deltaTime) {
		if (isMotionMagicMode) {
			//Logging.h("Motion Magic Mode run!");
			talon.set(ControlMode.MotionMagic, lastSetpoint);
		} else {
			if (feedbackActive) {
				double output = feedbackController.run(getPosition(), deltaTime);
				setPower(output);
			} else {
				//Logging.l("runFeedback run with feedback inactive");
			}
		}
	}

	@Override
	public void setSetpoint(double setpoint) {
		if(isMotionMagicMode) {
			if (feedbackController != null) {
				feedbackController.setSetpoint(setpoint);
			}
		}
		else talon.set(ControlMode.MotionMagic, setpoint);
		lastSetpoint = setpoint;
	}

	@Override
	public double getSetpoint() {
		return feedbackController.getSetpoint();
	}

	@Override
	public void setFeedbackDevice(FeedbackDevice device) {
		talon.configSelectedFeedbackSensor(device, 0, 1000);
	}

	@Override
	public AbstractFeedbackController getFeedbackController() {
		return feedbackController;
	}

	@Override
	public void setEncoderReversed(boolean reversed) {
		isEncoderReversed = reversed;
	}

	@Override
	public void resetEncoders() {
		talon.setSelectedSensorPosition(0, 0, 1000);
	}

}
