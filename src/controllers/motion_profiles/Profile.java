package controllers.motion_profiles;

import utilities.Logging;

/**
 * stores a list of points and allows reading an interpolated point at any time
 * through the profile.
 * 
 * @author jack
 *
 */
public class Profile {
	/**
	 * the list of points.
	 */
	private MPPoint[] trajectory;

	/**
	 * creates a profile with the given length
	 * 
	 * @param length
	 *            the number of points in the profile
	 */
	public Profile(int length) {
		trajectory = new MPPoint[length];
	}

	/**
	 * creates a profile from the given points
	 * 
	 * @param points
	 *            the points to store in the profile
	 */
	public void setPoints(MPPoint... points) {
		if (points.length < 2) {
			Logging.e("Useless motion profile - less than 2 points");
		} else {
			trajectory = points;
		}
	}

	/**
	 * sets the given point of the profile
	 * 
	 * @param index
	 *            the index of the point
	 * @param point
	 *            what to put at the given index
	 */
	public void setPoint(int index, MPPoint point) {
		trajectory[index] = point;
	}

	/**
	 * gets a point at an integer index
	 * 
	 * @param index
	 *            the index of the point
	 * @return the requested point
	 */
	public MPPoint getPoint(int index) {
		return trajectory[index];
	}

	/**
	 * returns an interpolated point at the given time.
	 * 
	 * @param time
	 *            the time to get the point at
	 * @return the point
	 */
	public MPPoint getInterpolatedPoint(double time) {
		int upperIndex = 0;
		while (getPoint(upperIndex).time < time) {
			upperIndex++;
		}
		if (upperIndex > 0) {
			int lowerIndex = upperIndex - 1;

			MPPoint upper = getPoint(upperIndex);
			MPPoint lower = getPoint(lowerIndex);

			// find what fraction of the way from upper to lower the time is
			double alpha = (time - lower.time) / (upper.time - lower.time);

			return lower.lerp(upper, alpha);
		} else {
			return getPoint(0);
		}
	}

	/**
	 * returns the first point
	 * 
	 * @return the first point
	 */
	public MPPoint start() {
		return getPoint(0);
	}

	/**
	 * returns the last point
	 * 
	 * @return the last point
	 */
	public MPPoint end() {
		return getPoint(trajectory.length - 1);
	}

	/**
	 * returns the time of the last point
	 * 
	 * @return the time of the last point
	 */
	public double getEndTime() {
		return end().time;
	}

	public String toString() {
		String out = "";
		for (MPPoint getPoint : trajectory) {
			out += getPoint.toString() + "\n";
		}
		return out;
	}
}