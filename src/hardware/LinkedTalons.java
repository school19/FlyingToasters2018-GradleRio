package hardware;


import com.ctre.phoenix.motorcontrol.can.TalonSRX;

//TODO use Talon[] instead of CANTalon[] to allow for current limiting talons

public class LinkedTalons implements MotorController {
	protected boolean isReversed = false;
	protected int numberOfTalons;
	protected Talon[] talons;
	protected double currentPower = 0;
	
	/**
	 * Creates a new set of linked talons.
	 * 
	 * @param talonIDs
	 *            Each of the IDs you want to control.
	 */
	public LinkedTalons(int... talonIDs) {
		
		numberOfTalons = talonIDs.length;
		talons = new Talon[numberOfTalons];

		for (int i = 0; i < numberOfTalons; i++)
			talons[i] = new Talon(talonIDs[i]);
	}

	@Override
	/**
	 * Set output power of all of the talons.
	 * 
	 * @param power
	 *            The power to set each of the talons to.
	 */
	public void setPower(double power) {
		currentPower = power;
		for (Talon talon : talons) {
			if(isReversed) {
				talon.setPower(-power);
			} else {
				talon.setPower(power);
			}
		}
	}

	@Override
	public double getPower() {
		return currentPower;
	}
	
	public void setCurrentLimit(int amps){
		for(Talon ct : talons){
			ct.setCurrentLimit(amps);
		}
	}
	
	public void EnableCurrentLimit(boolean enable){
		for(Talon ct : talons){
			ct.EnableCurrentLimit(enable);
		}
	}

	@Override
	public void setInverted(boolean inverted) {
		isReversed = inverted;
	}
}
