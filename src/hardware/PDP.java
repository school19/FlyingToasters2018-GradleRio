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
	private static final String DIR = "~/current_log/";
	private static final double LOG_FREQUENCY = 0.1;
	private static final int PERCISION = 2;
	private double time = 0;
	private double lastTime = -LOG_FREQUENCY;
	
	/**
	 * Create the pdp object
	 */
	public PDP() {
		super();
		File dir = new File(DIR);
		int count = 0;
		dir.mkdirs();
		String[] files = dir.list();
		if(files != null) {
			for(String filename : files) {
				if(filename.contains(".csv")) {
					count ++;
				}
			}
			String logName = DIR + "log" + count + ".csv";
			SmartDashboard.putString("logName", logName);
			try {
				File logFile = new File(logName);
				logFile.createNewFile();
				logStream = new PrintStream(logName);
				String outputString = "Time,\tVoltage,\tTemperature,\t";
				for(int i = 0; i<PORTS; i++) {
					outputString += "Port " + i + " Current";
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
		if(time >= lastTime + LOG_FREQUENCY) {
			SmartDashboard.putBoolean("LogStream", (logStream != null));
			if(logStream != null) {
				String outputString = String.format("%." + PERCISION + "f", time) + ",\t" + String.format("%.2f", this.getVoltage()) + ",\t" + String.format("%.2f", this.getTemperature()) + ",\t";
				for(int i = 0; i<PORTS; i++) {
					outputString += String.format("%." + PERCISION + "f", this.getCurrent(i));
					if(i != PORTS-1) outputString += ",\t";
				}
				SmartDashboard.putString("PDP Data", outputString);
				logStream.println(outputString);
				logStream.flush();
			}
			lastTime = time;
		}
	}
}