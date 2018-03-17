package hardware;

import com.ctre.phoenix.motorcontrol.NeutralMode;

public class Climber {

	private static final int LEFT = 10;
	private static final int RIGHT = 11;
	
	private Talon left;
	private Talon right;

	public Climber() {
		left = new Talon(LEFT);
		right = new Talon(RIGHT);
//		right.setFollower(left);
		
		left.setInverted(false);
		right.setInverted(true);
		
		left.talon.setNeutralMode(NeutralMode.Brake);
		right.talon.setNeutralMode(NeutralMode.Brake);
	}
	
	public void setSpeed(double speed) {
		setSpeed(speed, speed);
	}
	
	public void setSpeed(double left, double right) {
		this.left.setPower(left);
		this.right.setPower(right);
	}
}
