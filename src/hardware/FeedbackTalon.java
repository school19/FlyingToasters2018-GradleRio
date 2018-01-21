package hardware;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import controllers.AbstractFeedbackController;
import hardware.interfaces.FeedbackMotorController;
import utilities.Logging;
import utilities.Utilities;

public class FeedbackTalon extends Talon implements FeedbackMotorController, Utilities.Conversions {
	boolean isEncoderReversed = false;
	private AbstractFeedbackController feedbackController;
	private boolean feedbackActive = false;
	
	public FeedbackTalon(int talonID) {
		super(talonID);
	}
	
	public FeedbackTalon(int talonID, FeedbackDevice device){
		super(talonID);
		setFeedbackDevice(device);
	}
	@Override
	public double getPosition() {
		double d = Distance.ENCODER_TICK.convert(talon.getSelectedSensorPosition(0), Distance.M);
		if(isEncoderReversed) {
			return -d;
		}else {
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
		if(feedbackActive){
			double output = feedbackController.run(getPosition(), deltaTime);
			setPower(output);
		}else{
			Logging.w("runFeedback run with feedback inactive");
		}
	}

	@Override
	public void setSetpoint(double setpoint) {
		feedbackController.setSetpoint(setpoint);		
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
