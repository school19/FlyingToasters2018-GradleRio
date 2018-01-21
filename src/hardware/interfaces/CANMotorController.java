package hardware.interfaces;

import com.ctre.phoenix.motorcontrol.IMotorController;

/**
 * An abstract interface for talon SRX's and Victor SPX's, so they can work together in linked things
 * @author jack
 *
 */
public interface CANMotorController extends MotorController{
	//sets the motor controller as a follower of another can motor controller
	public void setFollower(CANMotorController master);
	//returns the actual motor controller object (TalonSRX or VictorSPX)
	public IMotorController getMotorController();
}
