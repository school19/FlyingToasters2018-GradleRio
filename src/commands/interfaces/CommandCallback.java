package commands.interfaces;

/**
 * abstract interface for a command
 * 
 * @author jackf
 *
 */
public interface CommandCallback {
	//called once
	public void commandFinished(Command cmd);
}
