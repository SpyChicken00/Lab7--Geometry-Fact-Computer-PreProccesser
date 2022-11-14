package preprocessor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import geometry_objects.points.Point;
import geometry_objects.points.PointDatabase;
import preprocessor.delegates.ImplicitPointPreprocessor;
import geometry_objects.Segment;

public class Preprocessor
{
	// The explicit points provided to us by the user.
	// This database will also be modified to include the implicit
	// points (i.e., all points in the figure).
	protected PointDatabase _pointDatabase;

	// Minimal ('Base') segments provided by the user
	protected Set<Segment> _givenSegments;

	// The set of implicitly defined points caused by segments
	// at implicit points.
	protected Set<Point> _implicitPoints;

	// The set of implicitly defined segments resulting from implicit points.
	protected Set<Segment> _implicitSegments;

	// Given all explicit and implicit points, we have a set of
	// segments that contain no otherSegment subsegments; these are minimal ('base') segments
	// That is, minimal segments uniquely define the figure.
	protected Set<Segment> _allMinimalSegments;

	// A collection of non-basic segments
	protected Set<Segment> _nonMinimalSegments;

	// A collection of all possible segments: maximal, minimal, and everything in between
	// For lookup capability, we use a map; each <key, value> has the same segment object
	// That is, key == value. 
	protected Map<Segment, Segment> _segmentDatabase;
	public Map<Segment, Segment> getAllSegments() { return _segmentDatabase; }

	public Preprocessor(PointDatabase points, Set<Segment> segments)
	{
		_pointDatabase  = points;
		_givenSegments = segments;
		
		_segmentDatabase = new HashMap<Segment, Segment>();
		
		analyze();
	}

	/**
	 * Invoke the precomputation procedure.
	 */
	public void analyze()
	{
		//
		// Implicit Points
		//
		_implicitPoints = ImplicitPointPreprocessor.compute(_pointDatabase, _givenSegments.stream().toList());

		//
		// Implicit Segments attributed to implicit points
		//
		_implicitSegments = computeImplicitBaseSegments(_implicitPoints);

		//
		// Combine the given minimal segments and implicit segments into a true set of minimal segments
		//     *givenSegments may not be minimal
		//     * implicitSegmen
		//
		_allMinimalSegments = identifyAllMinimalSegments(_implicitPoints, _givenSegments, _implicitSegments);

		//
		// Construct all segments inductively from the base segments
		//
		_nonMinimalSegments = constructAllNonMinimalSegments(_allMinimalSegments);

		//
		// Combine minimal and non-minimal into one package: our database
		//
		_allMinimalSegments.forEach((segment) -> _segmentDatabase.put(segment, segment));
		_nonMinimalSegments.forEach((segment) -> _segmentDatabase.put(segment, segment));
	}
	
	
	/**
	 * Creates new subsegments based on an input set of points that lie on a single segment
	 * Will not add self pairs or segments that are already contained in implicitBaseSegments
	 * @param segmentPoints
	 * @param implicitBaseSegments
	 */
	private void createSubSegments(SortedSet<Point> segmentPoints, Set<Segment> implicitBaseSegments, Set<Point> implicitSegmentPoints) 
	{
		if (segmentPoints.size() == 2) return; //minimal segment already
		
		//add to implicitSegment set if points are different and segment is a minimal segment
		for (Point implicitPointA:segmentPoints) 
		{
			for (Point implicitPointB:segmentPoints) 
			{
				Segment implicitSegment = new Segment(implicitPointA, implicitPointB);
				if ((!implicitPointA.equals(implicitPointB)) && implicitSegment.isMinimalSegment(implicitSegmentPoints)) 
				{
					implicitBaseSegments.add(implicitSegment);
				}	
			}
		}
	}
	
	/**
	 * Calculates all the implicit base segments based on input implicit points
	 * @param implicitPoints
	 * @return a set of implicit base segments
	 */
	protected Set<Segment> computeImplicitBaseSegments(Set<Point> implicitPoints) 
	{
		Set<Segment> implicitBaseSegments = new LinkedHashSet<Segment>();
		for(var currentSegment: _givenSegments)
		{
			//get all points on the current segment
			SortedSet<Point> implicitSegmentPoints = currentSegment.collectOrderedPointsOnSegment(implicitPoints);
			SortedSet<Point> segmentPoints = implicitSegmentPoints;
			segmentPoints.add(currentSegment.getPoint1());
			segmentPoints.add(currentSegment.getPoint2());
			//create implicit segments and add to set if valid
			createSubSegments(segmentPoints, implicitBaseSegments, implicitSegmentPoints);
		}
		
		return implicitBaseSegments;
	}
	
	
	
	/**
	 * Determines which given segments are and are not minimal, and adds the minimal ones to a set of minimal segments
	 * @param implicitPoints
	 * @param givenSegments
	 * @param implicitSegments 
	 * @return the set of all minimal segments in the figure
	 */
	protected Set<Segment> identifyAllMinimalSegments(Set<Point> implicitPoints, Set<Segment> givenSegments, Set<Segment> implicitSegments)
	{		
		//add all implicit segments to minimal segments
		Set<Segment> minimalSegments = new LinkedHashSet<Segment>(); 
		minimalSegments.addAll(implicitSegments);
		
		//create set of all points
		Set<Point> allPoints =  new HashSet<Point>();
		allPoints.addAll(_pointDatabase.getPoints());
		allPoints.addAll(implicitPoints);

		//need to verify if givenSegment is minimal
		for (Segment currentSegment:givenSegments) {
			//get all points on the current segment										
			SortedSet<Point> segmentPoints = currentSegment.collectOrderedPointsOnSegment(allPoints);
			//if minimal should only contain endpoints (size 2)
			//check for duplicate segments
			if (segmentPoints.size() <= 2 && !minimalSegments.contains(currentSegment)) 
			{
				minimalSegments.add(currentSegment);
			}
		}
		
		return minimalSegments;
	}
	
	private void createNonMinimalSubSegments(Set<Point> segmentPoints, Set<Segment> nonMinimalSegments, Set<Segment> allMinimalSegments) {
		//add to implicitSegment set if points are different and segment not already contained
		for (Point PointA:segmentPoints) {
			for (Point PointB:segmentPoints) {
				//create new potential nonMinimalSegment
				Segment newSegment = new Segment(PointA, PointB);
				if ((!PointA.equals(PointB)) && !allMinimalSegments.contains(newSegment)) 
				{
					nonMinimalSegments.add(newSegment);
				}
			}
		}
	}
	
	/**
	 * Constructs all nonMinimalSegments 
	 * @param allMinimalSegments
	 * @return
	 */
	protected Set<Segment> constructAllNonMinimalSegments(Set<Segment> allMinimalSegments) {
		Set<Segment> nonMinimalSegments = new LinkedHashSet<Segment>(); 
		//construct all possible segments
		//dont add those which are minimal segments
		for(var currentSegment: _givenSegments)
		{
			//get all points on the current segment
			SortedSet<Point> segmentPoints = currentSegment.collectOrderedPointsOnSegment(_implicitPoints);
			segmentPoints.add(currentSegment.getPoint1());
			segmentPoints.add(currentSegment.getPoint2());
			//create implicit segments and add to set if valid
			createNonMinimalSubSegments(segmentPoints, nonMinimalSegments, allMinimalSegments);
		}
		return nonMinimalSegments;
		
	}
}
