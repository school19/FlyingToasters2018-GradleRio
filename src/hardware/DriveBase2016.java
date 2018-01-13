package hardware;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import utilities.Logging;

public class DriveBase2016 extends DriveBase {

	private FeedbackLinkedTalons left;
	private FeedbackLinkedTalons right;
	
	
	public enum Talon {
		LEFT0(1),
		LEFT1(2),
		RIGHT0(3),
		RIGHT1(4);
		
		public int id;
		Talon(int talonID){
			id = talonID;
		}
	}
	
	public DriveBase2016() {
		super();
		left = new FeedbackLinkedTalons(FeedbackDevice.CTRE_MagEncoder_Absolute, Talon.LEFT0.id, Talon.LEFT1.id);
		right = new FeedbackLinkedTalons(FeedbackDevice.CTRE_MagEncoder_Absolute, Talon.RIGHT0.id, Talon.RIGHT1.id);
		//add the motor controllers to the list to be updated
		registerMotorController(left);
		registerMotorController(right);
	}
	
	@Override
	public void drive(double... inputs) {
		Logging.h("Drive called! Inputs = " + inputs[0] + inputs[1]);
		if(inputs.length != 2){
			Logging.e("Incorrect number of inputs to drive(double... inputs): " + inputs.length);
		}else{
			//tank drive
			left.setPower(inputs[1]);
			right.setPower(-inputs[0]);
		}
	}
}
