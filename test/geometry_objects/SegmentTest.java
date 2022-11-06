package geometry_objects;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

import org.junit.jupiter.api.Test;

import geometry_objects.points.Point;

class SegmentTest {

	
	@Test
	void hasSubSegmentTest() {
		//check with integers
		Point A = new Point("A", 1, 3);
		Point B = new Point("B", 2, 3);
		Point C = new Point("C", 2, 4);
		Point D = new Point("D", 5, 2);
		Point E = new Point("E", 5, 3);
		Point F = new Point("F", 3, 3);
		Point Zero = new Point("zero", 0, 0);
		Point One = new Point("one", 1, 1);
		Point Half = new Point("half", 1.5, 1.5);
		Point Two = new Point("two", 2, 2);
		Segment testSegment1 = new Segment(A, E);
		Segment identicalSegment1 = new Segment(A, E);
		Segment flatSubSegment1 = new Segment(A, B);
		Segment flatSubSegment2 = new Segment(B, E);
		Segment invalidSubSegment1 = new Segment(A, C);
		Segment diagonalSegment = new Segment(Zero, Two);
		Segment diagonalSubSegment1 = new Segment(One, Two);
		Segment diagonalSubSegment2 = new Segment(One, Half);
		
		
		
		
		
		//check segments that overlap one way but not the other
		//check segments that arent subsegments
		
		
		//check identical segments
		assertFalse(testSegment1.hasSubSegment(identicalSegment1));
		//check segments with different slopes 
		
	
		//check segments with 1 identical endpoint
		assertTrue(testSegment1.hasSubSegment(flatSubSegment1));
		assertTrue(testSegment1.hasSubSegment(flatSubSegment2));
		//check points that arent collinear
		assertFalse(testSegment1.hasSubSegment(invalidSubSegment1));
		
		//check valid subsegment
		//TODO failing same slope check
		assertTrue(diagonalSegment.hasSubSegment(diagonalSubSegment1));
	}
	
	
	
	@Test
	void collectOrderedPointsOnSegmentTest() {
		//check with integers
		Point A = new Point("A", 1, 3);
		Point B = new Point("B", 2, 5);
		Point C = new Point("C", 1, 8);
		Point D = new Point("D", 5, 2);
		Point E = new Point("E", 5, 3);
		Point F = new Point("F", 3, 3);
		
		Set<Point> points = new HashSet<Point>();
		
		points.add(A);
		points.add(B);
		points.add(C);
		points.add(D);
		points.add(E);
		points.add(F);
		
		Segment testSegment1 = new Segment(A, E);
		SortedSet<Point> sortedPoints = testSegment1.collectOrderedPointsOnSegment(points);
		
		assertEquals("[A (1.0, 3.0), F (3.0, 3.0), E (5.0, 3.0)]", sortedPoints.toString());
		
		points.clear();
		sortedPoints.clear();
		
		//test with doubles 
		Point G = new Point("G", 1.3, 7.3);
		Point H = new Point("H", 2.6, 7.3);
		Point I = new Point("I", 1.4, 7.3);
		Point J = new Point("J", 5.6, 7.3);
		Point K = new Point("K", 5.1, 7.1);
		Point L = new Point("L", 3.3, 7.2);
		
		points.add(G);
		points.add(H);
		points.add(I);
		points.add(J);
		points.add(K);
		points.add(L);
		
		Segment testSegment2 = new Segment(G, H);
		sortedPoints = testSegment2.collectOrderedPointsOnSegment(points);
		
		assertEquals("[G (1.3, 7.3), I (1.4, 7.3), H (2.6, 7.3)]", sortedPoints.toString());
	}
	
	
	

}
