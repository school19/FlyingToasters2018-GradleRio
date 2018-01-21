package hardware;


import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import controllers.AbstractFeedbackController;

public interface FeedbackMotorController extends MotorController{
	double getPosition();
	void resetEncoders();
	void setFeedbackDevice(FeedbackDevice device);
	void setFeedbackController(AbstractFeedbackController controller);
	public AbstractFeedbackController getFeedbackController();
	void setFeedbackActive(boolean active);
	boolean getFeedbackActive();
	void setSetpoint(double setpoint);
	double getSetpoint();
	void runFeedback(double deltaTime);
	void setEncoderReversed(boolean reversed);
}
