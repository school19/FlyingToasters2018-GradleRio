package commands.teleop;
import java.util.EnumMap;

import edu.wpi.first.wpilibj.Joystick;
import utilities.Coords;
import utilities.Logging;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;

public class PS4
{
	private EnumMap<Button, Boolean> current, last;
	private EnumMap<Axis, Double> axes;
	private Joystick[] rawJoysticks = new Joystick[2];
	private double leftAngle, leftMagnitude, rightAngle, rightMagnitude;

	private RumbleThread rumbleThread;
	
	private class RumbleThread extends Thread {
		double value;
		boolean heavy;
		long ms;
		
		RumbleThread(double value, boolean heavy, double time) {
			ms = (long)(1000*time);
			this.value = value;
			this.heavy = heavy;
			this.start();
		}
		
		public void run() {
			rumble(value, heavy);
			try {
				Thread.sleep(ms);
				rumble(0, heavy);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	/**
	 * Initialize the PS4 controller with the port.
	 * 
	 * @param port The port it uses on the driver station.
	 */
	public PS4(int port)
	{
		rawJoysticks[0] = new Joystick(port);
		rawJoysticks[1] = new Joystick(port+1);
		current = new EnumMap<Button, Boolean>(Button.class);
		last = new EnumMap<Button, Boolean>(Button.class);
		axes = new EnumMap<Axis, Double>(Axis.class);
		poll(); //Populate the current EnumMap so the last EnumMap won't be null when the user polls for the first time.
		rumbleThread = new RumbleThread(0,true,0);
	}
	
	/**
	 * The buttons it supports
	 */
	public static enum Button
	{
		SQUARE(1,0),
		X(2,0),
		CIRCLE(3,0),
		TRIANGLE(4,0),
		LEFT_BUMPER(5,0),
		RIGHT_BUMPER(6,0),
		LEFT_TRIGGER_BUTTON(7,0),
		RIGHT_TRIGGER_BUTTON(8,0),
		SHARE(9,0),
		OPTIONS(10,0),
		LEFT_STICK_BUTTON(11,0),
		RIGHT_STICK_BUTTON(12,0),
		PLAYSTATION_BUTTON(13,0),
		TOUCHPAD_BUTTON(14,0),
		DPAD_LEFT,
		DPAD_RIGHT,
		DPAD_UP,
		DPAD_DOWN;
		
		int number;
		int joystickNumber;
		boolean easy;
		
		Button(int number, int joystickNumber) {
			this.number = number;
			this.joystickNumber = joystickNumber;
			this.easy = true;
		}
		
		Button() {
			this.easy = false;
		}
	}
	
	/**
	 * The axes it supports
	 */
	public enum Axis
	{
		TILT_ROLL, TILT_PITCH,
		LEFT_X, LEFT_Y, LEFT_TRIGGER,
		RIGHT_X, RIGHT_Y, RIGHT_TRIGGER;
	}
		
	/**
	 * Returns the value of the specified axis.
	 * 
	 * @param axis The axis to read.
	 * @return The value of said axis.
	 */
	public double getAxis(Axis axis)
	{
		return axes.get(axis);
	}
	
	/**
	 * Get the polar angle of the left stick.
	 * 
	 * @return The polar angle of the left stick.
	 */
	public double getLeftAngle()
	{
		return leftAngle;
	}
	
	/**
	 * Get the polar magnitude of the left stick.
	 * 
	 * @return the polar magnitude of the left stick.
	 */
	public double getLeftMagnitude()
	{
		return leftMagnitude;
	}
	
	/**
	 * Get the polar angle of the right stick.
	 * 
	 * @return The polar angle of the right stick.
	 */
	public double getRightAngle()
	{
		return rightAngle;
	}
	
	/**
	 * Get the polar magnitude of the right stick.
	 * 
	 * @return the polar magnitude of the right stick.
	 */
	public double getRightMagnitude()
	{
		return rightMagnitude;
	}
	
	/**
	 * Checks if the specified button is down at all.
	 * 
	 * @param button The button to read.
	 * @return True if the button is down.
	 */
	public boolean isDown(Button button)
	{
		return current.get(button);
	}
	
	/**
	 * Checks if the specified button has just been pressed.
	 * 
	 * @param button The button to read.
	 * @return True on the rising edge of the button.
	 */
	public boolean isPressed(Button button)
	{
		return (current.get(button) && !last.get(button));
	}
	
	/**
	 * Checks if the specified button has just been released.
	 * 
	 * @param button The button to read.
	 * @return True on the falling edge of the button.
	 */
	public boolean isReleased(Button button)
	{
		return (!current.get(button) && last.get(button));
	}
	
	public void rumble(double value, boolean heavy)
	{
		RumbleType type = (heavy) ? RumbleType.kLeftRumble : RumbleType.kRightRumble;
		rawJoysticks[1].setRumble(type, value);
	}
	
	public void rumbleForTime(double value, boolean heavy, double time)
	{
		rumbleThread.interrupt();
		rumbleThread = new RumbleThread(value, heavy, time);
	}
	
	/**
	 * Read the current state of each button and axis.
	 */
	public void poll()
	{
		last = current.clone();

		axes.put(Axis.TILT_ROLL, rawJoysticks[1].getRawAxis(0));
		axes.put(Axis.TILT_PITCH, rawJoysticks[1].getRawAxis(1));
		
		axes.put(Axis.LEFT_X, rawJoysticks[0].getRawAxis(0));
		axes.put(Axis.LEFT_Y, -rawJoysticks[0].getRawAxis(1));
		axes.put(Axis.RIGHT_X, rawJoysticks[0].getRawAxis(2));
		axes.put(Axis.LEFT_TRIGGER, rawJoysticks[0].getRawAxis(3)/2 + .5);
		axes.put(Axis.RIGHT_TRIGGER, rawJoysticks[0].getRawAxis(4)/2 +.5);
		axes.put(Axis.RIGHT_Y, -rawJoysticks[0].getRawAxis(5));

		for(Button button : Button.values()) {
			if(button.easy)	{
				current.put(button, rawJoysticks[button.joystickNumber].getRawButton(button.number));
			}
		}

		current.put(Button.DPAD_LEFT, (rawJoysticks[0].getPOV(0) == 270));
		current.put(Button.DPAD_RIGHT, (rawJoysticks[0].getPOV(0) == 90));
		current.put(Button.DPAD_UP, (rawJoysticks[0].getPOV(0) == 0));
		current.put(Button.DPAD_DOWN, (rawJoysticks[0].getPOV(0) == 180));
		
		leftMagnitude = Coords.rectToPolarRadius(getAxis(Axis.LEFT_X), getAxis(Axis.LEFT_Y));
		leftAngle = Coords.rectToPolarAngle(getAxis(Axis.LEFT_X), getAxis(Axis.LEFT_Y));
		rightMagnitude = Coords.rectToPolarRadius(getAxis(Axis.RIGHT_X), getAxis(Axis.RIGHT_Y));
		rightAngle = Coords.rectToPolarAngle(getAxis(Axis.RIGHT_X), getAxis(Axis.RIGHT_Y));
	}
}
