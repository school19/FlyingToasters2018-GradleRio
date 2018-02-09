package org.usfirst.frc.team3641.robot;

import commands.autonomous.*;
import commands.interfaces.*;
import commands.teleop.Teleop;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import hardware.DriveBase2018;
import hardware.Intake;
import utilities.Logging;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot implements CommandCallback {
	enum Auton {
		AUTO_LINE("Auto Line auton"), AUTO_SWITCH("Switch auton"), AUTO_SCALE_L("Left Scale auton"), AUTO_SCALE_R(
				"Right Scale auton"), AUTO_MP_TEST("Motion profile test auton"), AUTO_TEST("Test Auton");

		String name;

		Auton(String n) {
			name = n;
		}
	}

	Auton autoSelected;
	SendableChooser<Auton> chooser = new SendableChooser<>();

	PowerDistributionPanel pdp = new PowerDistributionPanel(0);
	public DriveBase2018 driveBase;
	public Intake intake;
	double lastTime;
	double deltaTime = 0;

	Timer timer;

	boolean isFirstPeriodic;

	OpMode autonomous;
	OpMode teleop;

	/**
	 * This function is run when the robot is first started up and should be used
	 * for any initialization code.
	 */
	@Override
	public void robotInit() {
		for (Auton a : Auton.values()) {
			chooser.addObject(a.name, a);
		}
		SmartDashboard.putData("Auto choices", chooser);
		// initialize drivebase
		driveBase = new DriveBase2018();
		intake = new Intake();
		// initialize timer
		timer = new Timer();

		resetTimer();
	}

	/**
	 * Resets and starts the timer.
	 */
	public void resetTimer() {
		timer.reset();
		timer.start();
	}

	/**
	 * called when the robot is disabled. Stops the commands and disables closed
	 * loop control.
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

	/**
	 * Called once when auton starts. Sets up the auton and sets the isFirstPeriodic
	 * flag to true to be called on the first periodic.
	 */
	@Override
	public void autonomousInit() {
		isFirstPeriodic = true;
		autoSelected = chooser.getSelected();
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
			autonomous = new TestAuton(this, "Auto scale right not done yet");
			break;
		default:
			autonomous = new TestAuton(this, "AUTON NOT FOUND");
			Logging.e("Could not get auton from chooser");
			break;
		}
		autonomous = new MotionProfileTest(this);
	}

	/**
	 * This function is called periodically during autonomous
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
	 * This function is called once on the first loop of autonomousPeriodic. It
	 * calls init in the auton.d
	 */
	public void autonomousFirstPeriodic() {
		autonomous.init();
		standardFirstPeriodic();
	}

	/**
	 * called once before teleop starts.
	 */
	@Override
	public void teleopInit() {
		teleop = new Teleop(this);
	}

	/**
	 * This function is called periodically during operator control
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

	private void teleopFirstPeriodic() {
		standardFirstPeriodic();
	}

	/**
	 * This method is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
		standardInit();
	}

	/**
	 * This method is always called periodically
	 */
	public void standardPeriodic() {
		double currentTime = timer.get();
		deltaTime = currentTime - lastTime;
		lastTime = currentTime;
		driveBase.update(deltaTime);
	}

	/**
	 * This method is always called to initialize
	 */
	public void standardInit() {
		isFirstPeriodic = true;
	}

	/**
	 * This method is always called on the first loop of periodic.
	 */
	public void standardFirstPeriodic() {
		lastTime = timer.get();
		isFirstPeriodic = false;
	}

	@Override
	public void commandFinished(Command cmd) {
		if (cmd == autonomous) {
			autonomous = null;
			Logging.h("Auton finished!");
		}
	}
}
