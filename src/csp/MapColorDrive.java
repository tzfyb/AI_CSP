package csp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MapColorDrive {
	public static void main(String[] args){
		List<String> city = new ArrayList<String>();
		city.add("WA");	city.add("NT");	city.add("SA");
		city.add("Q");	city.add("NSW");city.add("V");
		city.add("T");
		
		List<String> color = new ArrayList<>();
		color.add("Red");
		color.add("Blue");
		color.add("Green");
		
		HashMap<String, List<String>> adjList = new HashMap<>();
		adjList.put("WA", Arrays.asList("SA", "NT"));
		adjList.put("NT", Arrays.asList("WA", "SA", "Q"));
		adjList.put("SA", Arrays.asList("WA", "NT", "Q", "NSW", "V"));
		adjList.put("Q", Arrays.asList("NT", "SA", "NSW"));
		adjList.put("NSW", Arrays.asList("Q", "NSW", "V"));
		adjList.put("V", Arrays.asList("SA", "NSW"));
		adjList.put("T", new ArrayList<>());
		
		MapColorCSP mccsp = new MapColorCSP(city, color, adjList);
		
		testColorCSP(mccsp, false, false, false);
		
		testColorCSP(mccsp, true, false, false);
		
		testColorCSP(mccsp, false, true, false);
		
		testColorCSP(mccsp, false, false, true);
	}
	
	//Test function for map coloring problem
	public static void testColorCSP(MapColorCSP testObject, boolean MRV, boolean LCV, boolean AC3){
		System.out.println("MRV:" + Boolean.toString(MRV) + " LCV:" + Boolean.toString(LCV) + " AC3:" + Boolean.toString(AC3));
		testObject.setMRV(MRV);
		testObject.setLCV(LCV);
		testObject.setAC3(AC3);
		long startTime = 0;
		long elapsedTime = 0;
		startTime = System.currentTimeMillis();
		if(testObject.dfs())
			testObject.printAssignment();
		else
			System.out.println("Cannot find the answer!");
		elapsedTime = System.currentTimeMillis() - startTime;
		
		testObject.setMRV(false);
		testObject.setLCV(false);
		testObject.setAC3(false);
		testObject.reset();
		
		System.out.println("Run Time: " + Long.toString(elapsedTime) + " ms");
		System.out.println();
	}
}
