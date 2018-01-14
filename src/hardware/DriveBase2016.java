package hardware;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import utilities.Logging;

public class DriveBase2016 extends DriveBase {

	private FeedbackLinkedCAN left;
	private FeedbackLinkedCAN right;
	
	
	public enum Talons {
		LEFT0(1), LEFT1(2), RIGHT0(3), RIGHT1(4);

		public int id;

		Talons(int talonID){
				id = talonID;
			}

		public Talon get() {
			return new Talon(id);
		}
	}
	
	public DriveBase2016() {
		super();
		left = new FeedbackLinkedCAN(FeedbackDevice.CTRE_MagEncoder_Absolute, Talons.LEFT0.id, Talons.LEFT1.get());
		right = new FeedbackLinkedCAN(FeedbackDevice.CTRE_MagEncoder_Absolute, Talons.RIGHT0.id, Talons.RIGHT1.get());
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
