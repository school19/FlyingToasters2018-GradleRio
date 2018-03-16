package hardware;

public class Climber {

	private static final int MASTER = 10;
	private static final int FOLLOWER = 11;
	
	private Talon master;
	private Talon follower;

	public Climber(){
		master = new Talon(MASTER);
		follower = new Talon(FOLLOWER);
		follower.setFollower(master);
		
		master.setInverted(false);
		follower.setInverted(true);
	}
	
	public void setSpeed(double speed) {
		master.setPower(speed);
	}
}
