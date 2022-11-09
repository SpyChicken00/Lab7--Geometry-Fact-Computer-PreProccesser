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
	 *    TODO
	 *    
	 *    //check all segments and see if they intersect with each other
	 *
	 *    //if they do check that the points where they overlap exist, 
	 *    if not add them to the list of implicit points
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
					Point intersect = current.segmentIntersection(other);
					if(intersect != null && !givenPoints.contains(intersect)) implicitPoints.add(intersect);
				}
			}
		}
		return implicitPoints;
	}
}

