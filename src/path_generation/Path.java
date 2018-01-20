package path_generation;

import java.util.ArrayList;

import utilities.Logging;


public class Path {
	static final double maxVel = 3;
	static final double maxAccel = 2.5;
	static final int defaultPoints = 50;
	static final VelocityMode defaultMode = VelocityMode.TRAPAZOIDAL;
	
	//a path consists of a list of waypoints.
	public ArrayList<Waypoint> waypoints;
	
	public double endTime;
	public double endPos;
	
	//the different modes of generating profiles
	public enum VelocityMode{
		TRIANGULAR,
		TRAPAZOIDAL,
		CONSTANT
	}
	
	public Path(Waypoint start, Waypoint end){
		this(start, end, defaultPoints, maxVel, maxAccel, defaultMode);
	}
	
	//create a path from two waypoints
	public Path(Waypoint start, Waypoint end, int numberOfPoints, double velocity, double accel,
			VelocityMode mode) {
		waypoints = new ArrayList<Waypoint>();
		genBezierPath(start, end, numberOfPoints, 0.5);
		alignWaypoints();
		getPositions();
		getVelocities(velocity, accel, mode);
		getTimes();
	}
	
	//calculate the distances of each point, return end position
	private void getPositions() {
		//stores the total distance
		double distanceAccumulator = 0;
		//get the position of the first point, and set its distance to 0
		Point lastPoint = waypoints.get(0).position;
		waypoints.get(0).time = 0;
		//loop through all other points
		for(int i = 1; i < waypoints.size(); i++) {
			Point wpPosition = waypoints.get(i).position;
			double dist = lastPoint.distance(wpPosition);
			lastPoint = wpPosition;
			distanceAccumulator += dist;
			waypoints.get(i).distance = distanceAccumulator;
		}
		endPos = distanceAccumulator;
	}
	
	//set the times for every waypoint based on distance
	private void getVelocities(double vel, double accel, VelocityMode mode) {
		switch(mode) {
		case TRIANGULAR:
			for(Waypoint wp : waypoints) {
				double distance = wp.distance;
				double accelVelocity = Math.sqrt(2 * distance * accel);
				double decelVelocity = Math.sqrt(2 * (endPos - distance) * accel);
				wp.velocity = Math.min(accelVelocity, decelVelocity);
			}
			break;
		case CONSTANT:
			for(Waypoint wp : waypoints) {
				wp.velocity = vel;
			}
			break;
		case TRAPAZOIDAL:
			for(Waypoint wp : waypoints) {
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
	 * Calculates the time of each waypoint by determining the average velocity and distance
	 * and determining the time between waypoints then adding the times up.
	 */
	private void getTimes() {
		//starting point is at time 0
		waypoints.get(0).time = 0;
		//set previous
		Waypoint lastWaypoint = waypoints.get(0);
		//loop through all but first
		for(int i = 1; i < waypoints.size(); i++) {
			Waypoint currentWaypoint = waypoints.get(i);
			//calculate the distance,velocity, and time between the last and current waypoint
			double distance = currentWaypoint.distance - lastWaypoint.distance;
			double averageVel = (currentWaypoint.velocity + lastWaypoint.velocity) / 2.0;
			double deltaTime = distance / averageVel;
			//calculate the time based on the delta time and last time
			currentWaypoint.time = lastWaypoint.time + deltaTime;
			
			lastWaypoint = currentWaypoint;
		}
	}
	
	//generates a path with a bezier curve
	void genBezierPath(Waypoint start, Waypoint end, int numberOfPoints, double tightness) {
		//get the location of the start and end points
		Point startPoint = start.getPoint();
		Point endPoint = end.getPoint();
		double distance = startPoint.distance(endPoint);
		double gpLength = distance / 2 * tightness;

		Point startOffset = Point.PolarPoint(gpLength, start.rotation);
		Point endOffset =  Point.PolarPoint(-gpLength, end.rotation);

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
	//aligns the waypoints all pointing to each other.
	void alignWaypoints() {
		for (int i = 1; i < waypoints.size() - 1; i++) {
			waypoints.get(i).pointTowards(waypoints.get(i + 1).getPoint());
		}
	}
	
	@Override
	public String toString(){
		String out = "";
		for( Waypoint wp : waypoints){
			out = out + wp.toString() + "\n";
		}
		return out;
	}
}
