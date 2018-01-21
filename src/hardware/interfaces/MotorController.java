package hardware.interfaces;

//TODO implement current limits
public interface MotorController {
	void setPower(double power);
	double getPower();
	void setInverted(boolean inverted);
}
