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
		right.setInverted(true);
		//add the motor controllers to the list to be updated
		registerMotorController(left);
		registerMotorController(right);
	}
	
	@Override
	public void drive(double... inputs) {
		if(inputs.length == 2) {
			driveArcade(inputs[0],inputs[1]);
		} else {
			Logging.e("Invalid number of inputs to drive");
		}
	}
	
	public void driveArcade(double power, double turn) {
		double leftPow = power - turn;
		double rightPow = power + turn;
		left.setPower(leftPow);
		right.setPower(rightPow);
	}
	
	public void driveGrilledCheese(double power, double rotation) {
		double gain = 1;
		double limit = 0.25;
		
		
		rotation = expInput(rotation, 1.5);
		double arcadePower = expInput(power, 1.5);
		double arcadeRotation = rotation;
		double cheesyRotation = rotation * gain * Math.abs(arcadePower);
		
		power = Math.abs(power);
		if(power == 0) rotation = arcadeRotation;
		else if(power <= limit) rotation = (power/limit)*cheesyRotation + (1-power/limit) * arcadeRotation;
		else rotation = cheesyRotation;
		
		driveArcade(arcadePower, rotation);
	}
	
	public double expInput(double input, double power) {
		if(input > 0) {
			return Math.pow(input, power);
		}else {
			return -Math.pow(-input, power);
		}
	}
}
