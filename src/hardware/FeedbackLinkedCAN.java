package hardware;


import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import controllers.AbstractFeedbackController;
import hardware.interfaces.CANMotorController;
import hardware.interfaces.FeedbackMotorController;
import utilities.Logging;

/**
 * Linked can motor controllers with feedback, works in master/slave mode
 * @author jack
 *
 */
public class FeedbackLinkedCAN extends LinkedCANMotorControllers implements FeedbackMotorController {
	private FeedbackTalon feedbackTalon;
	private AbstractFeedbackController feedbackController;
	private boolean feedbackActive = false;
	/**
	 * Creates a new set of linked talons.
	 * 
	 * @param talonIDs
	 *            Each of the IDs you want to control.
	 */
	public FeedbackLinkedCAN(FeedbackTalon feedback, CANMotorController... controllers) {
		//initializes the controllers and stuff
		super(controllers);
		feedbackTalon = feedback;
		//set the motor controllers to follow the master talon
		for(CANMotorController mc : motorControllers) {
			mc.setFollower(feedbackTalon);
		}
	}
	
	public FeedbackLinkedCAN(FeedbackDevice device, int feedbackTalonID, CANMotorController... controllers){
		this(new FeedbackTalon(feedbackTalonID, device), controllers);
		feedbackTalon.setFeedbackDevice(device);
	}

	@Override
	/**
	 * Set output power of all of the talons.
	 * 
	 * @param power
	 *            The power to set each of the talons to.
	 */
	public void setPower(double power) {
		if(isReversed) {
			feedbackTalon.setPower(-power);
		} else {
			feedbackTalon.setPower(power);
		}
	}

	@Override
	public double getPower() {
		
		return currentPower;
	}

	@Override
	public double getPosition() {
		return feedbackTalon.getPosition();
	}

	@Override
	public void setFeedbackDevice(FeedbackDevice device) {
		feedbackTalon.setFeedbackDevice(device);
	}

	@Override
	//set the feedback controller,
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
	public void setSetpoint(double setpoint) {
		feedbackController.setSetpoint(setpoint);		
	}

	@Override
	public double getSetpoint() {
		return feedbackController.getSetpoint();
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
	
	public void setCurrentLimit(int amps){
		super.setCurrentLimit(amps);
		feedbackTalon.setCurrentLimit(amps);
	}
	
	public void enableCurrentLimit(boolean enable){
		super.enableCurrentLimit(enable);
		feedbackTalon.enableCurrentLimit(enable);
	}

	@Override
	public AbstractFeedbackController getFeedbackController() {
		return feedbackController;
	}

	@Override
	public void setEncoderReversed(boolean reversed) {
		feedbackTalon.setEncoderReversed(reversed);		
	}

	@Override
	public void resetEncoders() {
		feedbackTalon.resetEncoders();
	}
}
