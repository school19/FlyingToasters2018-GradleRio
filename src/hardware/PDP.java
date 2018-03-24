package hardware;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import utilities.Logging;

public class PDP extends PowerDistributionPanel
{
	private PrintStream logStream;
	
	private static final int PORTS = 16;
	private static final String DIR = "/currentLog/";
	private static final double logFrequency = 0.1;
	private double time = 0;
	private double lastTime = -logFrequency;
	
	/**
	 * Create the pdp object
	 */
	public PDP() {
		super();
		File dir = new File(DIR);
		int count = 0;
		dir.mkdir();
		String[] files = dir.list();
		if(files != null) {
			for(String filename : files) {
				if(filename.contains(".log")) {
					count ++;
				}
			}
			String logName = DIR + "currentLog" + count + ".csv";
			SmartDashboard.putString("logName", logName);
			try {
				File logFile = new File(logName);
				logFile.createNewFile();
				logStream = new PrintStream(logName);
				String outputString = "time,\t";
				for(int i = 0; i<PORTS; i++) {
					outputString += i;
					if(i != PORTS-1) outputString += ",\t";
				}
				logStream.println(outputString);
				logStream.flush();
			} catch (IOException e) { 
				Logging.h("Could not create new current log \"" + logName + "\"");
			}
		} else { 
			SmartDashboard.putString("logName", "NONE!");
			Logging.h("\"" + DIR + "\" is not a directory...");
		}
	}
	
	public void periodic(double deltaTime) {
		time += deltaTime;
		SmartDashboard.putNumber("PDP Log time", time);
	}
	
	public void logCurrent() {
		if(time >= lastTime + logFrequency) {
			SmartDashboard.putBoolean("LogStream", (logStream != null));
			if(logStream != null) {
				String outputString = time + ",\t";
				for(int i = 0; i<PORTS; i++) {
					outputString += this.getCurrent(i);
					if(i != PORTS-1) outputString += ",\t";
				}
				logStream.println(outputString);
				logStream.flush();
			} else Logging.h("logStream is null");
			lastTime = time;
		}
	}
}