package path_generation;

import java.util.ArrayList;

import utilities.Logging;

/**
 * A path of waypoints with times, velocities, and positions.
 * 
 * @author jack
 *
 */
public class Path {
	// default values for path generation
	public static final double maxVel = 2; // TODO find good values or do preset modes (Slow, medium, fast, etc.)
	public static final double maxAccel = 3;
	public static final int defaultPoints = 100;
	public static final VelocityMode defaultMode = VelocityMode.TRAPAZOIDAL;

	/**
	 * a path consists of a list of waypoints.
	 */
	public ArrayList<Waypoint> waypoints;
	/**
	 * The time at which the robot should be at the endpoint of the path
	 */
	public double endTime;
	/**
	 * The total distance of the path
	 */
	public double endPos;

	/**
	 * the different velocity profile modes.
	 * 
	 * @author jack
	 *
	 */
	public enum VelocityMode {
		TRIANGULAR, TRAPAZOIDAL, CONSTANT,
	}

	/**
	 * Creates a path between the given waypoints
	 * 
	 * @param points
	 */
	public Path(Waypoint... points) {
		this(defaultPoints, maxVel, maxAccel, defaultMode, points);
	}

	/**
	 * creates a longer path between multiple points
	 * 
	 * @param numberOfPoints
	 *            the number of points between the waypoints
	 * @param velocity
	 *            the max velocity
	 * @param accel
	 *            the max acceleration
	 * @param mode
	 *            the velocity profile mode
	 * @param points
	 *            the waypoints to make a path between
	 */
	public Path(int numberOfPoints, double velocity, double accel, VelocityMode mode, Waypoint... points) {
		waypoints = new ArrayList<Waypoint>();
		genBezierChainPath(numberOfPoints, 0.8, points);
		alignWaypoints();
		getPositions();
		getVelocities(velocity, accel, mode);
		getTimes();
	}

	/**
	 * creates a path using default values from start to end
	 * 
	 * @param start
	 * @param end
	 */
	public Path(Waypoint start, Waypoint end) {
		this(start, end, defaultPoints, maxVel, maxAccel, defaultMode);
	}

	/**
	 * create a path from two waypoints
	 * 
	 * @param start
	 *            starting waypoints
	 * @param end
	 *            ending waypoint
	 * @param numberOfPoints
	 *            the number of points between the waypoints
	 * @param velocity
	 *            the max velocity
	 * @param accel
	 *            the max acceleration
	 * @param mode
	 *            the velocity profile mode
	 */
	public Path(Waypoint start, Waypoint end, int numberOfPoints, double velocity, double accel, VelocityMode mode) {
		waypoints = new ArrayList<Waypoint>();
		genBezierPath(start, end, numberOfPoints, 1);
		alignWaypoints();
		getPositions();
		getVelocities(velocity, accel, mode);
		getTimes();
	}

	/**
	 * calculates the distances of each point and sets end position
	 */
	private void getPositions() {
		// stores the total distance
		double distanceAccumulator = 0;
		// get the position of the first point, and set its distance to 0
		Point lastPoint = waypoints.get(0).position;
		waypoints.get(0).time = 0;
		// loop through all other points
		for (int i = 1; i < waypoints.size(); i++) {
			Point wpPosition = waypoints.get(i).position;
			double dist = lastPoint.distance(wpPosition);
			lastPoint = wpPosition;
			distanceAccumulator += dist;
			waypoints.get(i).distance = distanceAccumulator;
		}
		endPos = distanceAccumulator;
	}

	/**
	 * Sets the velocities of the path's points
	 * 
	 * @param vel
	 *            the maximum velocity to use
	 * @param accel
	 *            the maximum acceleration to use
	 * @param mode
	 *            the velocity profile mode
	 */
	private void getVelocities(double vel, double accel, VelocityMode mode) {
		switch (mode) {
		case TRIANGULAR:
			for (Waypoint wp : waypoints) {
				double distance = wp.distance;
				double accelVelocity = Math.sqrt(2 * distance * accel);
				double decelVelocity = Math.sqrt(2 * (endPos - distance) * accel);
				wp.velocity = Math.min(accelVelocity, decelVelocity);
			}
			break;
		case CONSTANT:
			for (Waypoint wp : waypoints) {
				wp.velocity = vel;
			}
			break;
		case TRAPAZOIDAL:
			for (Waypoint wp : waypoints) {
				double distance = wp.distance;
				double accelVelocity = Math.sqrt(2 * distance * accel);
				double decelVelocity = Math.sqrt(2 * (endPos - distance) * accel);
				double triangularVelocity = Math.min(accelVelocity, decelVelocity);
				wp.velocity = Math.min(triangularVelocity, vel);
			}
			break;
		default:
			Logging.e("Couldn't find velocity profile mode.");
		}
	}

	/**
	 * Calculates the time of each waypoint by determining the average velocity and
	 * distance and determining the time between waypoints then adding the times up.
	 */
	private void getTimes() {
		// starting point is at time 0
		waypoints.get(0).time = 0;
		// set previous
		Waypoint lastWaypoint = waypoints.get(0);
		// loop through all but first
		for (int i = 1; i < waypoints.size(); i++) {
			Waypoint currentWaypoint = waypoints.get(i);
			// calculate the distance,velocity, and time between the last and current
			// waypoint
			double distance = currentWaypoint.distance - lastWaypoint.distance;
			double averageVel = (currentWaypoint.velocity + lastWaypoint.velocity) / 2.0;
			double deltaTime = distance / averageVel;
			// calculate the time based on the delta time and last time
			currentWaypoint.time = lastWaypoint.time + deltaTime;

			lastWaypoint = currentWaypoint;
		}
		endTime = waypoints.get(waypoints.size() - 1).time;
	}

	/**
	 * generates a path with a bezier curve
	 * 
	 * @param start
	 *            the starting point
	 * @param end
	 *            the ending point
	 * @param numberOfPoints
	 *            the number of points between the start and the end
	 * @param tightness
	 *            how close the guide points are put to the start/end
	 */
	void genBezierPath(Waypoint start, Waypoint end, int numberOfPoints, double tightness) {
		// get the location of the start and end points
		Point startPoint = start.getPoint();
		Point endPoint = end.getPoint();
		double distance = startPoint.distance(endPoint);
		double gpLength = distance / 2 * tightness;

		Point startOffset = Point.PolarPoint(gpLength, start.rotation);
		Point endOffset = Point.PolarPoint(-gpLength, end.rotation);

		Point gp1 = startPoint.sum(startOffset);
		Point gp2 = endPoint.sum(endOffset);

		waypoints.add(start);
		for (int i = 1; i < numberOfPoints; i++) {
			double alpha = (double) i / (double) numberOfPoints;
			Point p1 = Point.lerp(startPoint, gp1, alpha);
			Point p2 = Point.lerp(gp1, gp2, alpha);
			Point p3 = Point.lerp(gp2, endPoint, alpha);

			Point p5 = Point.lerp(p1, p2, alpha);
			Point p6 = Point.lerp(p2, p3, alpha);

			Point p = Point.lerp(p5, p6, alpha);
			Waypoint wp = new Waypoint(p, 0);
			waypoints.add(wp);
		}
		waypoints.add(end);
	}

	/**
	 * Generates a longer path by chaining together more waypoints.
	 * 
	 * @param pointsPerCurve
	 *            the number of points in each curve
	 * @param tightness
	 *            how close the guide points are to the start/end of each individual
	 *            curve.
	 * @param points
	 *            the waypoints to build the curve from
	 */
	void genBezierChainPath(int pointsPerCurve, double tightness, Waypoint... points) {
		if (points.length < 2) {
			Logging.e("Not enough waypoints to make a path!");
			return;
		}

		// add first point
		waypoints.add(points[0]);
		// iterates throught the waypoints in pairs.
		for (int wp = 0; wp < points.length - 1; wp++) {
			Waypoint start = points[wp];
			Waypoint end = points[wp + 1];
			Point startPoint = start.getPoint();
			Point endPoint = end.getPoint();
			double distance = startPoint.distance(endPoint);
			double gpLength = distance / 2 * tightness;

			Point startOffset = Point.PolarPoint(gpLength, start.rotation);
			Point endOffset = Point.PolarPoint(-gpLength, end.rotation);

			Point gp1 = startPoint.sum(startOffset);
			Point gp2 = endPoint.sum(endOffset);

			for (int i = 1; i <= pointsPerCurve; i++) {
				double alpha = (double) i / (double) pointsPerCurve;
				Point p1 = Point.lerp(startPoint, gp1, alpha);
				Point p2 = Point.lerp(gp1, gp2, alpha);
				Point p3 = Point.lerp(gp2, endPoint, alpha);

				Point p5 = Point.lerp(p1, p2, alpha);
				Point p6 = Point.lerp(p2, p3, alpha);

				Point p = Point.lerp(p5, p6, alpha);
				waypoints.add(new Waypoint(p, 0));
			}
		}
		// set last point
		waypoints.set(waypoints.size() - 1, points[points.length - 1]);
	}

	/**
	 * aligns the waypoints all pointing to the next waypoint.
	 */
	void alignWaypoints() {
		for (int i = 1; i < waypoints.size() - 1; i++) {
			waypoints.get(i).pointTowards(waypoints.get(i + 1).getPoint());
		}
	}

	@Override
	public String toString() {
		String out = "";
		for (Waypoint wp : waypoints) {
			out = out + wp.toString() + "\n";
		}
		return out;
	}
}
