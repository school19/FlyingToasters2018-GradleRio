package hardware;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import utilities.Logging;

public class PDP extends PowerDistributionPanel
{
	private PrintStream logStream;
	
	private final static int PORTS = 16;
	private final static String DIR = "/currentLog/";
	private int time = 0;
	private int lastTime = -1;
	
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
			String logName = DIR + "currentLog" + count + ".log";
			
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
			} catch (IOException e) { 
				Logging.h("Could not create new current log \"" + logName + "\"");
			}
		} else { 
			Logging.h("\"" + DIR + "\" is not a directory...");
		}
	}
	
	public void periodic(double deltaTime) {
		time += deltaTime;
		logCurrent();
	}
	
	public void logCurrent() {
		if(logStream != null) {
			String outputString = time + ",\t";
			for(int i = 0; i<PORTS; i++) {
				outputString += this.getCurrent(i);
				if(i != PORTS-1) outputString += ",\t";
			}
			logStream.println(outputString);
		}
	}
}