package hardware;

import java.util.ArrayList;

import utilities.Logging;

/**
 * Drivebase class for all of your driving needs
 * 
 * @author jackf
 *
 */

public abstract class DriveBase {
	protected boolean isFeedbackActive = false;
	// these two should be mutually exclusive.
	private ArrayList<MotorController> controllers;
	private ArrayList<FeedbackMotorController> feedbackControllers;

	public DriveBase() {
		controllers = new ArrayList<MotorController>();
		feedbackControllers = new ArrayList<FeedbackMotorController>();
	}

	// override this with whatever stuff
	abstract public void drive(double... inputs);

	// add a motor controller to the list
	protected void registerMotorController(MotorController mc) {
		if (mc instanceof FeedbackMotorController) {
			FeedbackMotorController fmc = (FeedbackMotorController) mc;
			feedbackControllers.add(fmc);
		} else {
			controllers.add(mc);
		}
	}

	// udpate should be called periodically.
	public void update(double dT) {

		for (FeedbackMotorController c : feedbackControllers) {
			if (c.getFeedbackActive()) {
				c.runFeedback(dT);
			}
		}
	};

	public void setFeedbackActive(boolean active) {
		isFeedbackActive = active;
		for (FeedbackMotorController c : feedbackControllers) {
			c.setFeedbackActive(active);
		}
		update(0);
	}

	public boolean getFeedbackActive() {
		return isFeedbackActive;
	}
}
