package preprocessor.delegates;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import geometry_objects.Segment;
import geometry_objects.points.Point;
import geometry_objects.points.PointDatabase;

public class ImplicitPointPreprocessor
{
	/**
	 * It is possible that some of the defined segments intersect
	 * and points that are not named; we need to capture those
	 * points and name them.
	 * 
	 * Algorithm:
	 *    
	 *    check all segments and see if they intersect with each other
	 *
	 *    if they do check whether the points where lines intersect 
	 *    already exist in the pointDatabase
	 *    
	 *    if not add them to the list of implicit points
	 */
	
	/**
	 * Computes the set of implied points 
	 * @param givenPoints
	 * @param givenSegments
	 * @return
	 */
	public static Set<Point> compute(PointDatabase givenPoints, List<Segment> givenSegments)
	{
		Set<Point> implicitPoints = new LinkedHashSet<Point>();

		for(var current: givenSegments)
		{
			for(var other: givenSegments)
			{
				if(!current.equals(other))
				{
					//find intersection
					Point intersect = current.segmentIntersection(other);
					//if intersect is not in given points, it is an implied point
					if(intersect != null && !givenPoints.contains(intersect)) implicitPoints.add(intersect);
				}
			}
		}
		return implicitPoints;
	}
}

