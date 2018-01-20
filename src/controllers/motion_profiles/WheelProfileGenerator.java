package controllers.motion_profiles;
import path_generation.*;
public abstract class WheelProfileGenerator {
	public abstract Profile genPoints(Path p);
}

