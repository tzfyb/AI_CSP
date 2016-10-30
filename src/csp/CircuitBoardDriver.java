package csp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CircuitBoardDriver {
	public static void main(String[] args){
		
		/*
		 * Test case for given sample
		 */
		/*List<List<Integer>> components = new ArrayList<>();
		components.add(Arrays.asList(3,2));
		components.add(Arrays.asList(5,2));
		components.add(Arrays.asList(2,3));
		components.add(Arrays.asList(7,1));
		int width = 10;
		int height = 3;*/
		
		/*
		 * Test case for large scale tight sample
		 */
		/*List<List<Integer>> components = new ArrayList<>();
		components.add(Arrays.asList(10,5));
		components.add(Arrays.asList(15,7));
		components.add(Arrays.asList(5,1));
		components.add(Arrays.asList(5,4));
		components.add(Arrays.asList(20,1));
		components.add(Arrays.asList(25,1));
		components.add(Arrays.asList(3,7));
		components.add(Arrays.asList(2,8));
		int width = 30;
		int height = 9;*/
		
		/*
		 * Test case for large scale sparse sample
		 */
		List<List<Integer>> components = new ArrayList<>();
		components.add(Arrays.asList(10,5));
		components.add(Arrays.asList(15,7));
		components.add(Arrays.asList(5,1));
		components.add(Arrays.asList(5,4));
		components.add(Arrays.asList(20,1));
		int width = 30;
		int height = 9;
		
		
		CircuitBoardCSP cbcsp = new CircuitBoardCSP(components, width, height);

		testCBCSP(cbcsp, false, false, false);
		
		testCBCSP(cbcsp, true, false, false);
		
		testCBCSP(cbcsp, false, true, false);
		
		testCBCSP(cbcsp, false, false, true);
		
		testCBCSP(cbcsp, true, true, false);
		
		testCBCSP(cbcsp, true, false, true);
		
		testCBCSP(cbcsp, false, true, true);
		
		testCBCSP(cbcsp, true, true, true);
	}
	
	//Test function
	public static void testCBCSP(CircuitBoardCSP testObject, boolean MRV, boolean LCV, boolean AC3){
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
