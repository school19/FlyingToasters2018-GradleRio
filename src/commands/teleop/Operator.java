package commands.teleop;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import utilities.Logging;

public class Operator {
	DriverStation ds;
			
	private static final int E3D_TYPE = 20, GUITAR_TYPE = 1;
	
	private boolean isGuitar;
	
	public XPlorer guitar;
	public E3D e3d; //TODO: Make this private after we're done setting setpoints with manual mode.
	
	private int port;
	
	public static enum Button {
		GROUND(E3D.Button.ELEVEN, XPlorer.Button.ORANGE),
		LOW_SWITCH(E3D.Button.NINE, XPlorer.Button.BLUE),
		HIGH_SWITCH(E3D.Button.TEN, XPlorer.Button.YELLOW),
		LOW_SCALE(E3D.Button.SEVEN, XPlorer.Button.RED),
		HIGH_SCALE(E3D.Button.EIGHT, XPlorer.Button.GREEN),
		INTAKE(E3D.Button.THREE, XPlorer.Button.STRUM_UP),
		OUTTAKE(E3D.Button.FOUR, XPlorer.Button.STRUM_DOWN),
//		RESET(E3D.Button.THUMB, XPlorer.Button.BACK),
		START_CLIMB(E3D.Button.FOO, XPlorer.Button.BACK),
		END_CLIMB(E3D.Button.FOO, XPlorer.Button.START),
		AUTO_FLIP(E3D.Button.TRIGGER, XPlorer.Button.WHAMMY);
		
		E3D.Button e3dButton;
		XPlorer.Button guitarButton;
		
		Button(E3D.Button e3dButton, XPlorer.Button guitarButton)
		{
			this.e3dButton = e3dButton;
			this.guitarButton = guitarButton;
		}
	}

	
	public Operator(int port) {
		this.port = port;
		guitar = new XPlorer(port);
		e3d = new E3D(port);
		checkControllerType();
	}
	
	public void checkControllerType() {
		ds = DriverStation.getInstance();
		int opType =  ds.getJoystickType(port);
		isGuitar = (opType != E3D_TYPE);
		SmartDashboard.putString("Operator Type", (isGuitar) ? "Guitar" : "E3D");
	}
	
	public boolean isPressed(Button button) {
		if(isGuitar) return guitar.isPressed(button.guitarButton);
		else return e3d.isPressed(button.e3dButton);
	}

	public boolean isDown(Button button) {
		if(isGuitar) return guitar.isDown(button.guitarButton);
		else return e3d.isDown(button.e3dButton);
	}

	public boolean isReleased(Button button) {
		if(isGuitar) return guitar.isReleased(button.guitarButton);
		else return e3d.isReleased(button.e3dButton);
	}
	
	public void poll() {
		if(isGuitar) guitar.poll();
		else e3d.poll();
	}
}