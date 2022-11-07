package preprocessor;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import geometry_objects.Segment;
import geometry_objects.points.Point;
import geometry_objects.points.PointDatabase;
import preprocessor.delegates.ImplicitPointPreprocessor;

class ImplicitPointPreprocessorTest {

	@Test
	void testCompute() {
		
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
		

		Set<Point> set = ImplicitPointPreprocessor.compute(pdb, list);
				
		for(var pt: set)
		{
			pdb.put(pt.getName(), pt.getX(), pt.getY());
			System.out.println(pt.toString());
		}
		
		System.out.println("---");
		
		for(var pt: pdb.getPoints())
		{
			System.out.println(pt.toString());
		}
		
		
		//TODO: Why is name weird? 
	}
}
