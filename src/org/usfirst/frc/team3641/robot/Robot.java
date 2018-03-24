package org.usfirst.frc.team3641.robot;

import commands.autonomous.*;
import commands.interfaces.*;
import commands.teleop.*;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import hardware.Climber;
import hardware.DriveBase2018;
import hardware.Intake;
import hardware.LED;
import hardware.Lift;
import hardware.PDP;
import utilities.Logging;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory. This class implements the CommandCallback interface which allows
 * it to have a function called upon autonomous or teleop finishing.
 */
public class Robot extends IterativeRobot implements CommandCallback {
	UsbCamera Cam1, Cam2; 
	
	/**
	 * An enum of the possible autonomous commands to follow. This is passed to the
	 * SendableChooser which is put on the dashboard.
	 * 
	 * @author jack
	 *
	 */
	enum Auton {
		AUTO_LINE("Auto Line auton"), 
		AUTO_SWITCH("Switch auton"), 
		AUTO_SCALE_L("(Reverse) Left Scale auton"), 
		AUTO_SCALE_R("(Reverse) Right Scale auton"),
		AUTO_2C_SCALE_L("(Reverse) Two Cube Left Scale Auton"),
		AUTO_2C_SCALE_R("(Reverse) Two Cube Right Scale Auton"),
		AUTO_FAST_2C_SCALE_L("(Reverse) FAST Two Cube Left Scale Auton"),
		AUTO_FAST_2C_SCALE_R("(Reverse) FAST Two Cube Right Scale Auton"),
		AUTO_TEST("Test Auton");
		/**
		 * The name of the auton to display on the dashboard
		 */
		String name;

		Auton(String n) {
			name = n;
		}
	}

	/**
	 * The autonomous mode selected by the smartDashboard
	 */
	Auton autoSelected;
	/**
	 * The chooser is given the items from the Auton enum and sent to the
	 * SmartDashboard.
	 */
	SendableChooser<Auton> chooser = new SendableChooser<>();

	/**
	 * The robot's drivebase. Has the motor controllers, encoder feedback, and
	 * motion profile following code.
	 */
	public DriveBase2018 driveBase;
	/**
	 * The robot's intake mechanism.
	 */
	public Intake intake;
	
	/**
	 * The robot's lift mechanism
	 */
	public Lift lift;

	/**
	 * The robot's climber mechanism
	 */
	public Climber climber;
	
	public PDP pdp;
	
	/**
	 * The bot's LEDs
	 */
	public LED leds;
	
	// TODO add units to documentation
	/**
	 * The timestamp (in ??? units) of the last run of standardPeriodic or
	 * standardFirstPeriodic if it is the first run of standardPeriodic.
	 */
	private double lastTime;
	/**
	 * Stores the time since the last run of standardPeriodic or
	 * standardFirstPeriodic.
	 */
	private double deltaTime = 0;
	/**
	 * The timer is used to read the timestamp every time periodic/FirstPeriodic is
	 * run.
	 */
	private Timer timer;
	/**
	 * Boolean flag to determine whether to run periodic or firstPeriodic.
	 * firstPeriodic is used because of inconsistent timing between init and
	 * periodic that caused errors with feedback control (like PIDs/Motion profiles)
	 */
	private boolean isFirstPeriodic;

	/**
	 * The autonomous opMode that is run during autonomous. This and teleop should
	 * probably be replaced with a single OpMode in the future, since the two should
	 * never be used concurrently.
	 */
	private OpMode autonomous;
	/**
	 * The OopMode that is run during teleop. This and autonomous should probably be
	 * replaced with a single OpMode in the future, since the two should never be
	 * used concurrently.
	 */
	private OpMode teleop;

	/**
	 * This function is run when the robot is first started up and should be used
	 * for any initialization code. This method sets up the autonomous chooser and
	 * puts it on the smart dashboard, then initializes the components of the robot
	 * (like drivebase and intake), then initializes and resets the timer.
	 */
	@Override
	public void robotInit() {
		for (Auton a : Auton.values()) {
			chooser.addObject(a.name, a);
		}
		chooser.addDefault(Auton.AUTO_LINE.name, Auton.AUTO_LINE);
		SmartDashboard.putData("Auto choices", chooser);
		SmartDashboard.putBoolean("Manual enabled", false);
		SmartDashboard.putBoolean("Allow Auton Opposite Side", true);
		// initialize drivebase
		driveBase = new DriveBase2018();
		
		lift = new Lift();
		intake = new Intake(lift);
		climber = new Climber();
		
		pdp = new PDP();
		
		// initialize timer
		timer = new Timer();
		
		new Thread(() -> {
			Cam1 = CameraServer.getInstance().startAutomaticCapture(0);
//			Cam2 = CameraServer.getInstance().startAutomaticCapture(1);
		}).start();
		
		resetTimer();
		
		leds = new LED(0);
	}

	/**
	 * Resets and starts the timer at 0. Not entirely necessary, but helpful for
	 * debugging timing issues. Will change lastTime to avoid negative deltaTime.
	 */
	private void resetTimer() {
		lastTime -= timer.get();
		timer.reset();
		timer.start();
	}

	/**
	 * called when the robot is disabled first. Stops the commands and disables
	 * closed loop control. Also sets auton and teleop to null to free memory for
	 * the next garbage collection. This helps prevent any slowdown next time they
	 * are initialized.
	 */
	public void disabledInit() {
		if (autonomous != null)
			autonomous.stop();
		if (teleop != null)
			teleop.stop();
		autonomous = null;
		teleop = null;
		driveBase.setFeedbackActive(false);
	}
	
	public void disabledPeriodic() {
		lift.logToDashboard();
	}
	/**
	 * Called once when auton starts. This method reads the selected auton from the
	 * chooser on the smart dashboard, then initializes it and sets the
	 * isFirstPeriodic flag to true so the first periodic method will be run.
	 */
	@Override
	public void autonomousInit() {
		//call the standard initialization method
		standardInit();
		//get the selected auton
		autoSelected = chooser.getSelected();
		Logging.h(autoSelected.name + "selected");
		intake.setState(Intake.State.RESTING_WITH_CUBE);
		//call the constructor of the auton.
		switch (autoSelected) {
		case AUTO_LINE:
			autonomous = new AutoLineAuton(this);
			break;
		case AUTO_SWITCH:
			autonomous = new SwitchAuton(this);
			break;
		case AUTO_SCALE_L:
			autonomous = new LeftScaleAuton(this);
			break;
		case AUTO_SCALE_R:
			autonomous = new RightScaleAuton(this);
			break;
		case AUTO_2C_SCALE_L:
			autonomous = new LeftScaleAuton2Cube(this);
			break;
		case AUTO_2C_SCALE_R:
			autonomous = new RightScaleAuton2Cube(this);
			break;
		case AUTO_FAST_2C_SCALE_L:
			autonomous = new Fast2CubeAuton(this, true);
			break;
		case AUTO_FAST_2C_SCALE_R:
			autonomous = new Fast2CubeAuton(this, false);
			break;
		default:
			autonomous = new TestAuton(this, "AUTON NOT FOUND");
			Logging.e("Could not get auton from chooser");
			break;
		}
	}

	/**
	 * This method is called periodically during autonomous. It will call
	 * firstPeriodic if it is the first time since autonomousInit, and otherwise
	 * call the periodic method in the autonomous opmode.
	 */
	@Override
	public void autonomousPeriodic() {
		// run the first periodic if it's the first time
		if (isFirstPeriodic) {
			autonomousFirstPeriodic();
		} else {
			standardPeriodic();
			autonomous.periodic(deltaTime);
		}
	}

	/**
	 * This method is called once on the first loop of autonomousPeriodic. It calls
	 * init in the auton command.
	 */
	public void autonomousFirstPeriodic() {
		autonomous.init();
		standardFirstPeriodic();
	}

	/**
	 * called once before teleop starts. this method initializes the teleop opmode.
	 */
	@Override
	public void teleopInit() {
		teleop = new Teleop(this);
		standardInit();
	}

	/**
	 * This method is called periodically during operator control. If it is the
	 * first time the method is run since teleopInit, it calls teleopFirstPeriodic,
	 * and otherwise calls the periodic function of the opmode.
	 */
	@Override
	public void teleopPeriodic() {
		if (isFirstPeriodic) {
			teleopFirstPeriodic();
		} else {
			standardPeriodic();
			teleop.periodic(deltaTime);
		}
	}

	/**
	 * This method is called on the first run of teleopPeriodic. It doesn't do
	 * anything special now.
	 */
	private void teleopFirstPeriodic() {
		teleop.init();
		standardFirstPeriodic();
	}

	/**
	 * This method is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
		standardInit();
	}

	/**
	 * This method is always called periodically in auton or teleop.
	 */
	private void standardPeriodic() {
		driveBase.update(deltaTime);
		intake.periodic(deltaTime);
		lift.periodic();
		leds.updateLightsToRobotState(this);
		pdp.logCurrent();
	}
	
	public void robotPeriodic() {
		double currentTime = timer.get();
		deltaTime = currentTime - lastTime;
		lastTime = currentTime;
		pdp.periodic(deltaTime);
	}

	/**
	 * This method is always called to initialize auton or teleop. It sets
	 * isFirstPeriodic to true and resets the timer.
	 */
	private void standardInit() {
		isFirstPeriodic = true;
		resetTimer();
	}

	/**
	 * This method is always called on the first loop of periodic. It will set
	 * lastTime and set isFirstPeriodic to false.
	 */
	private void standardFirstPeriodic() {
		lastTime = timer.get();
		isFirstPeriodic = false;
	}

	/**
	 * Called when a command is finished. Sets the command to null to free it up for
	 * garbage collection and prints a message that the command is finished.
	 */
	@Override
	public void commandFinished(Command cmd) {
		if (cmd == autonomous) {
			autonomous = null;
			Logging.h("Auton finished!");
		}

	}
}
