package org.usfirst.frc.team3641.robot;


import java.util.ArrayList;

import commands.Command;
import commands.CommandCallback;
import commands.OpMode;
import commands.MotionProfileTest;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import hardware.DriveBase2016;
import utilities.Logging;
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot implements CommandCallback {
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	ArrayList<Command> commands;
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();

	public DriveBase2016 driveBase;

	double lastTime;
	double deltaTime = 0;;
	
	Timer timer;
	
	boolean isFirstPeriodic;
	PS4 ps4;
	
	OpMode opMode;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);
		SmartDashboard.putData("Auto choices", chooser);
		driveBase = new DriveBase2016();
		opMode = new MotionProfileTest(this);
		ps4 = new PS4(0);
		timer = new Timer();
		timer.reset();
		timer.start();
	}
	
	public void disabledInit() {
		opMode.stop();
		driveBase.setFeedbackActive(false);
		isFirstPeriodic = true;
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		isFirstPeriodic = true;
		opMode = new MotionProfileTest(this);
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		if (isFirstPeriodic) {
			autonomousFirstPeriodic();
		} else {
			standardPeriodic();
			/*
			 * switch (autoSelected) { default: // Put default auto code here break; }
			 */
			
			opMode.periodic(deltaTime);
		}
	}
	
	public void autonomousFirstPeriodic() {
		opMode.init();
		standardFirstPeriodic();
	}
	
	@Override
	public void teleopInit() {
		isFirstPeriodic = true;
	}
	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		if(isFirstPeriodic) {
			teleopFirstPeriodic();
		}else {
			standardPeriodic();
			ps4.poll();
			driveBase.driveGrilledCheese(ps4.getAxis(PS4.Axis.LEFT_Y), -ps4.getAxis(PS4.Axis.RIGHT_X));
		}
	}
	
	private void teleopFirstPeriodic() {
		standardFirstPeriodic();
	}
	
	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
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
		lastTime = -1;
		driveBase.left.resetEncoders();
		driveBase.right.resetEncoders();
	}

	public void standardFirstPeriodic() {
		lastTime = timer.get();
		isFirstPeriodic = false;
	}

	@Override
	public void commandFinished(Command cmd) {
		Logging.h("Auton finished!");
	}
}
