package geometry_objects;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import geometry_objects.delegates.LineDelegate;
import geometry_objects.delegates.SegmentDelegate;
import geometry_objects.delegates.intersections.IntersectionDelegate;
import geometry_objects.points.Point;
import utilities.math.MathUtilities;
import utilities.math.analytic_geometry.GeometryUtilities;

public class Segment extends GeometricObject
{
	protected Point _point1;
	protected Point _point2;

	protected double _length;
	protected double _slope;

	public Point getPoint1() { return _point1; }
	public Point getPoint2() { return _point2; }
	public double length() { return _length; }
	public double slope()
	{
		try { return GeometryUtilities.slope(_point1, _point2); }
		catch(ArithmeticException ae) { return Double.POSITIVE_INFINITY; }
	}

	public Segment(Segment in) { this(in._point1, in._point2); }
	public Segment(Point p1, Point p2)
	{
		_point1 = p1;
		_point2 = p2;
	}

	/*
	 * @param that -- a segment (as a segment: finite)
	 * @return the midpoint of this segment (finite)
	 */
	public Point segmentIntersection(Segment that) {  return IntersectionDelegate.segmentIntersection(this, that); }

	/*
	 * @param pt -- a point
	 * @return true / false if this segment (finite) contains the point
	 */
	public boolean pointLiesOn(Point pt) { return this.pointLiesOnSegment(pt); }

	/*
	 * @param pt -- a point
	 * @return true / false if this segment (finite) contains the point
	 */
	public boolean pointLiesOnSegment(Point pt) { return SegmentDelegate.pointLiesOnSegment(this, pt); }

	/*
	 * @param pt -- a point
	 * @return true if the point is on the segment (EXcluding endpoints); finite examination only
	 */
	public boolean pointLiesBetweenEndpoints(Point pt) { return SegmentDelegate.pointLiesBetweenEndpoints(this, pt); }

	/**
	 * Does this segment contain a subsegment:
	 * A-------B-------C------D
	 * A subsegment is: AB, AC, AD, BC, BD, CD
	 * 
	 * @param candidate
	 * @return true if this segment contains a subsegment.
	 */
	public boolean hasSubSegment(Segment candidate)
	{
		//check if candidate endpoints lie between this segments endpoints
		if (!(GeometryUtilities.between(candidate.getPoint1(), _point1, _point2))) return false;
		if (!(GeometryUtilities.between(candidate.getPoint2(), _point1, _point2))) return false;
		
		return true;
	}

	/**
	 * Determines if this segment that segment share an endpoint
	 * @param s -- a segment
	 * @return the shared endpoint
	 *         returns null if they are the same segment
	 */
	public Point sharedVertex(Segment that)
	{
		if (this.equals(that)) return null;

		if (_point1.equals(that._point1)) return _point1;
		if (_point1.equals(that._point2)) return _point1;
		if (_point2.equals(that._point1)) return _point2;
		if (_point2.equals(that._point2)) return _point2;
		return null;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null) return false;
		
		if (!(obj instanceof Segment)) return false;
		Segment that = (Segment)obj;

		return this.has(that.getPoint1()) && this.has(that.getPoint2());
	}

	/*
	 * @param that -- another segment
	 * @return true / false if the two lines (infinite) are collinear
	 */
	public boolean isCollinearWith(Segment that) { return LineDelegate.areCollinear(this, that); }

	/*
	 * @param pt -- a point
	 * @return true if @pt is one of the endpoints of this segment
	 */
	public boolean has(Point pt) { return _point1.equals(pt) || _point2.equals(pt); }

	/*
	 * @return true if this segment is horizontal (by analysis of both endpoints having same y-coordinate)
	 */
	public boolean isHorizontal() { return MathUtilities.doubleEquals(_point1.getY(), _point2.getY()); }

	/*
	 * @return true if this segment is vertical (by analysis of both endpoints having same x-coordinate)
	 */
	public boolean isVertical() { return MathUtilities.doubleEquals(_point1.getX(), _point2.getX()); }

	/*
	 * @param pt -- one of the endpoints of this segment
	 * @return the 'other' endpoint of the segment (null if neither endpoint is given)
	 */
	public Point other(Point p)
	{
		if (p.equals(_point1)) return _point2;
		if (p.equals(_point2)) return _point1;

		return null;
	}

	@Override
	public int hashCode()
	{
		return _point1.hashCode() +_point2.hashCode();
	}
	
	/*
	 * @param that -- a segment
	 * @return true if the segments coincide, but do not overlap:
	 *                    this                  that
	 *             ----------------           ---------
	 * Note: the segment MAY share an endpoint
	 */
	public boolean coincideWithoutOverlap(Segment that)
	{
		//check if identical
		if (this.equals(that)) return false;
		//points should have same slope/y-intercept
		if (!(collinearWithGap(that))) return false;
		
		//check if either of that segment's endpoints lie between this segment's endpoints OR if endpoints are equal
		//if that endpoint lies between this segments endpoints AND is not an endpoint return false;
		if (GeometryUtilities.between(that.getPoint1(), _point1, _point2) && 
				(!(that.getPoint1().equals(_point1) || that.getPoint1().equals(_point2)))) return false;
		if (GeometryUtilities.between(that.getPoint2(), _point1, _point2) &&
				(!(that.getPoint2().equals(_point1) || that.getPoint2().equals(_point2)))) return false;
		
		return true;
	}
	
	/**
	 * Checks whether two segments are collinear, but also factors in whether there is a gap
	 * of space between the segments or not
	 * @param that
	 * @return True if collinear with/without a gap
	 */
	private boolean collinearWithGap(Segment that) {
		//if share endpoint check with dr alvin's collinear method 
		if (isSharedVertex(that)) return isCollinearWith(that);
		
		//check if coincide with gap of space between segments
		return MathUtilities.doubleEquals(GeometryUtilities.distance(_point1, that.getPoint2()),
				GeometryUtilities.distance(_point1, _point2) + 
				GeometryUtilities.distance(that.getPoint1(), that.getPoint2()) +
				GeometryUtilities.distance(_point2, that.getPoint1()));
		
	}

	/**
	 * Checks whether the current segment and input segment share a vertex
	 * @param that
	 * @return True if share a vertex
	 */
	private boolean isSharedVertex(Segment that) {
		return (_point1.equals(that._point1) || (_point1.equals(that._point2))
				|| (_point2.equals(that._point1)) || (_point2.equals(that._point2)));
		
	}
	
	/**
	 * Returns a set of ordered points that lie on this segment
	 * @return the set of Points that lie on this segment (ordered lexicographically)
	 */
	public SortedSet<Point> collectOrderedPointsOnSegment(Set<Point> points)
	{
		SortedSet<Point> pointsOn = new TreeSet<Point>();

        //find points from given set that lie on segment 
		for (Point point:points) {
			if (pointLiesOnSegment(point)) pointsOn.add(point);
		}

		return pointsOn;
	}
	
	
	/**
	 * Returns this segment as a readable string
	 * @return
	 */
	public String toString() {
		
		return _point1.getName() + _point2.getName() + ": " + _point1.toString() + ", " + _point2.toString();
		
	}
	
	/**
	 * Checks whether this segment is a minimal segment based on an input set of points
	 * Will only return false if points lie BETWEEN the segments endpoints
	 * @param points
	 * @return true if minimal segment (AKA no points lie between endpoints)
	 */
	public boolean isMinimalSegment(Set<Point> points) {
		//loop over all points, if any points lie between then not minimal 
		for (Point p:points) {
			if (pointLiesBetweenEndpoints(p)) return false;
		}
		
		return true;
			
	}
}