package utilities;

import edu.wpi.first.wpilibj.DriverStation;

public class Logging {
	// priority of a message to be logged
	public enum Priority {
		LOW(0), MID(1), HIGH(2), WARN(3), ERROR(4);

		private int intValue;

		Priority(int val) {
			this.intValue = val;
		}
	}

	static Priority minPriority = Priority.HIGH;
	static boolean enableWarn = true;
	static boolean enableError = true;

	/**
	 * Sets minimum priority
	 * 
	 * @param p
	 *            minimum priority to log
	 */
	public static void setMinPriority(Priority p) {
		minPriority = p;
	}

	/**
	 * Enables printing of warnings
	 */
	public static void showWarnings() {
		enableWarn = true;
	}

	/**
	 * Enables printing errors
	 */
	public static void showErrors() {
		enableError = true;
	}

	/**
	 * Disables printing of warnings
	 */
	public static void hideWarnings() {
		enableWarn = false;
	}

	/**
	 * Disables printing errors
	 */
	public static void hideErrors() {
		enableError = false;
	}

	/**
	 * Logs a message.
	 * 
	 * @param message
	 *            the message to log
	 */
	public static void logMessage(Object message) {
		System.out.println(message.toString());
	}
	
	/**
	 * logs a warning
	 */
	public static void logWarning(Object message) {
		DriverStation.reportWarning("WARNING:" + message.toString(), false);
	}
	
	public static void logError(Object message) {
		DriverStation.reportError(message.toString(), false);
	}
	
	
	/**
	 * Logs a message if the priority is high enough.
	 * 
	 * @param message
	 *            the message to log
	 * @param p
	 *            the priority of the message
	 */
	public static void logMessage(Object message, Priority p) {
		switch (p) {
		case WARN:
			if (enableWarn)
				logWarning(message);
			break;
		case ERROR:
			if (enableError)
				logError(message);
			break;
		default:
			if (p.intValue >= minPriority.intValue) {
				logMessage(message);
			}
			break;
		}
	}
	/**
	 * Logs a message if the priority is high enough.
	 * 
	 * @param message
	 *            the message to log
	 * @param p
	 *            the priority of the message, 0 = low, 1 = mid, 2 = high, 3 =
	 *            warn, 4 = error
	 */
	public static void logMessage(Object message, int p) {
		// Priority to be converted into
		Priority enumValue;

		switch (p) {
		case 0:
			enumValue = Priority.LOW;
			break;
		case 1:
			enumValue = Priority.MID;
			break;
		case 2:
			enumValue = Priority.HIGH;
			break;
		case 3:
			enumValue = Priority.WARN;
			break;
		case 4:
			enumValue = Priority.ERROR;
			break;
		default:
			enumValue = Priority.LOW;
			logMessage("Error: Priority value out of range: " + p, Priority.ERROR);
			break;
		}

		logMessage(message, enumValue);
	}
	
	public static void l(Object message){
		logMessage(message, Priority.LOW);
	}
	public static void m(Object message){
		logMessage(message, Priority.MID);
	}
	public static void h(Object message){
		logMessage(message, Priority.HIGH);
	}
	public static void w(Object message){
		logMessage(message, Priority.WARN);
	}
	public static void e(Object message){
		logMessage(message, Priority.ERROR);
	}
}
