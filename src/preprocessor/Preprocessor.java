package preprocessor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

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
	
	
	
	//TODO: Verify, dead code
	protected Set<Segment> computeImplicitBaseSegments(Set<Point> implicitPoints) {
		Set<Segment> result = new LinkedHashSet<Segment>();
		
		for(var currentSegment: _givenSegments)
		{
			for(var point: implicitPoints)
			{
				if(currentSegment.pointLiesOn(point) && !result.contains(currentSegment))
				{
					result.add(new Segment(point, currentSegment.getPoint1()));
					result.add(new Segment(point, currentSegment.getPoint2()));
				}
			}
		}
		return result;
	}
	
	
	
	protected Set<Segment> identifyAllMinimalSegments(Set<Point> implicitPoints, Set<Segment> givenSegments, Set<Segment> implicitSegments) {
		Set<Segment> result = new LinkedHashSet<Segment>(); 
		
		result.addAll(implicitSegments);
		
		for(var currentSegment: givenSegments)
		{
			for(var point: _pointDatabase.getPoints())
			{
				if(!currentSegment.pointLiesOn(point) && !result.contains(currentSegment)) 
				{
					result.add(currentSegment);
				}
			}
		}
		return result; 
	}
	
	protected Set<Segment> constructAllNonMinimalSegments(Set<Segment> allMinimalSegments) {
		Set<Segment> result = new LinkedHashSet<Segment>(); 
		
		for(var currentSegment: _givenSegments)
		{
			if(!allMinimalSegments.contains(currentSegment)) {result.add(currentSegment);}
		}
		return result;
	}
}
