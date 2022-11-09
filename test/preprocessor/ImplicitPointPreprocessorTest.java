package preprocessor;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import geometry_objects.Segment;
import geometry_objects.points.Point;
import geometry_objects.points.PointDatabase;
import input.InputFacade;
import preprocessor.delegates.ImplicitPointPreprocessor;

class ImplicitPointPreprocessorTest {

	@Test
	void testComputeCrossingSymmetricTriangle() {
		
		//crossing symmetric triangle test
		Point pt1 = new Point("D", 0, 0);
		Point pt2 = new Point("E", 6, 0);
		Point pt3 = new Point("B", 2, 4);
		Point pt4 = new Point("C", 4, 4);
		Point pt5 = new Point("A", 3, 6); 
		
		Segment s1 = new Segment(pt5, pt3); 
		Segment s2 = new Segment(pt5, pt4); 
		
		Segment s3 = new Segment(pt3, pt4); 
		Segment s4 = new Segment(pt3, pt1);
		Segment s5 = new Segment(pt3, pt2); 
		
		Segment s6 = new Segment(pt4, pt1);
		Segment s7 = new Segment(pt4, pt2); 
		
		Segment s8 = new Segment(pt1, pt2); 
		
		List<Segment> list = new ArrayList<Segment>(); 
		
		list.add(s1); list.add(s2); list.add(s3);
		list.add(s4); list.add(s5); list.add(s6);
		list.add(s7); list.add(s8);
		
		PointDatabase pdb = new PointDatabase();
		
		pdb.put("D", 0, 0);		
		pdb.put("E", 6, 0);
		pdb.put("B", 2, 4);
		pdb.put("C", 4, 4);
		pdb.put("A", 3, 6);
		
		//compute implicit point x in crossing symmetric triangle
		Set<Point> implicitPointSet = ImplicitPointPreprocessor.compute(pdb, list);
				
		for(Point pt: implicitPointSet)
		{
			//only 1 implicit point
			pdb.put(pt.getName(), pt.getX(), pt.getY());
			assertEquals("__UNNAMED", pt.getName());
			assertEquals(3.0, pt.getX());
			assertEquals(3.0, pt.getY());
			assertTrue(pdb.contains(new Point(3.0, 3.0)));
		}
	}
	
	@Test
	void testComputeSailboat() {
		Map.Entry<PointDatabase, Set<Segment>> geometryMap = InputFacade.toGeometryRepresentation("JSON/sailboat.json");
		PointDatabase sailboatPointDB = geometryMap.getKey();
		Set<Segment> sailboatSegmentSet = geometryMap.getValue();
		
		List<Segment> sailboatSegmentList = new ArrayList<Segment>();
		for (Segment s:sailboatSegmentSet) {
			sailboatSegmentList.add(s);
		}
		
		Set<Point> implicitPointSet2 = ImplicitPointPreprocessor.compute(sailboatPointDB, sailboatSegmentList);
		PointDatabase pdb = new PointDatabase();
		
		for(Point pt: implicitPointSet2)
		{
			//add 3 implicit sailboat points to database
			pdb.put(pt.getName(), pt.getX(), pt.getY());
			//System.out.println(pdb.getPoint(pt));
		}
		
		assertTrue(pdb.contains(new Point(6.0, 3.0)));
		assertTrue(pdb.contains(new Point(7.0, 3.0)));
		assertTrue(pdb.contains(new Point(7.0, 1.5)));

		
		
	}
	
	@Test
	void testEightPointSquare() {
		Map.Entry<PointDatabase, Set<Segment>> geometryMap = InputFacade.toGeometryRepresentation("JSON/eight_point_square.json");
		PointDatabase squarePointDB = geometryMap.getKey();
		Set<Segment> squareSegmentSet = geometryMap.getValue();
		
		//convert segment set to list
		List<Segment> squareSegmentList = new ArrayList<Segment>();
		for (Segment s:squareSegmentSet) {
			squareSegmentList.add(s);
		}
		
		//compute implicit points of square (16 total)
		Set<Point> implicitPointSet2 = ImplicitPointPreprocessor.compute(squarePointDB, squareSegmentList);
		PointDatabase pdb = new PointDatabase();
		
		for(Point pt: implicitPointSet2)
		{
			//add lots of implicit square points to database
			pdb.put(pt.getName(), pt.getX(), pt.getY());
			System.out.println(pdb.getPoint(pt));
		}
		
		assertTrue(pdb.contains(new Point(2.0, 3.0)));
		assertTrue(pdb.contains(new Point(0.8, 1.6)));
		assertTrue(pdb.contains(new Point(1.33333333333333, 1.33333333333)));
	}
}
