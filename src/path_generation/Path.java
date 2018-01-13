package path_generation;

import java.util.ArrayList;

import utilities.Logging;

public class Path {
	static final double maxVel = 4.0;
	static final double maxAccel = 5.0;
	static final int defaultPoints = 200;
	static final VelocityMode defaultMode = VelocityMode.TRIANGULAR;
	
	//a path consists of a list of waypoints.
	public ArrayList<Waypoint> waypoints;
	
	public double endTime;
	public double endPos;
	
	//the different modes of generating profiles
	public enum VelocityMode{
		TRIANGULAR,
		CONSTANT
	}
	
	public Path(Waypoint start, Waypoint end){
		this(start, end, defaultPoints, maxVel, maxAccel, defaultMode);
	}
	
	//create a path from two waypoints
	public Path(Waypoint start, Waypoint end, int numberOfPoints, double velocity, double accel,
			VelocityMode mode) {
		waypoints = new ArrayList<Waypoint>();
		genBezierPath(start, end, numberOfPoints, 0.2);
		endPos = getPositions();
		getTimes(velocity, accel, mode);
	}
	
	//calculate the distances of each point, return end position
	private double getPositions() {
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
		return distanceAccumulator;
	}
	
	//set the times for every waypoint based on distance
	private void getTimes(double vel, double accel, VelocityMode mode) {
		switch(mode) {
		case TRIANGULAR:
			endTime = Math.sqrt(2 * endPos / accel);
			break;
		case CONSTANT:
			endTime = endPos / vel;
			break;
		}
		for(Waypoint w : waypoints) {
			w.time = getTime(vel, accel, w.distance, mode);
		}
	}
	
	//get the time for an individual waypoint based on distance
	private double getTime(double vel, double accel, double distance, VelocityMode mode) {
		switch(mode) {
		case TRIANGULAR:
			if(distance <= endPos / 2.0) {
				return Math.sqrt(2 * distance / accel);
			}else {
				//wow this is all terrible I have to redo it.
				return (-accel * endTime + Math.sqrt(Math.pow((accel * endTime), 2) - 4.0 * (-accel / 2.0) * (-distance + (accel * Math.pow(endTime, 2) /2)))) / -accel;
			}
		case CONSTANT:
			return distance / maxVel;
		default:
			Logging.e("Couldn't find velocity profile mode.");
			return 0;
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
		Point endOffset =  Point.PolarPoint(gpLength, end.rotation);

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
