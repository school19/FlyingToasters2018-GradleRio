package hardware;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Talon implements MotorController {
	protected TalonSRX talon;
	protected double currentPower;
	public Talon(int talonID) {
		talon = new TalonSRX(talonID);
		currentPower = 0;
	}


	@Override
	public void setPower(double power) {
		currentPower = power;
		talon.set(ControlMode.PercentOutput, currentPower);
	}

	@Override
	public double getPower() {
		return currentPower;
	}
	
	@Override
	public void setCurrentLimit(int amps){
		talon.configContinuousCurrentLimit(amps, 100);
		talon.configPeakCurrentLimit(amps, 100);
		talon.configPeakCurrentDuration(100, 100);
	}
	
	@Override
	public void EnableCurrentLimit(boolean enable){
		talon.enableCurrentLimit(enable);
	}
}
