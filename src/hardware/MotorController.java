package hardware;

//TODO implement current limits
public interface MotorController {
	void setPower(double power);
	double getPower();
	void setInverted(boolean inverted);
	void setCurrentLimit(int amps);
	void EnableCurrentLimit(boolean enable);
}
