package csp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class CircuitBoardCSP extends CSP{
	//to store the width and height of the board
	protected int width, height;
	//to store the components. Each component is 
	//represented by a <width, height> pair
	protected List<Pair> components;
	//map to store the corresponding index for a component
	protected HashMap<Pair, Integer> comp2Idx;
	//map to store the corresponding component for an index
	protected HashMap<Integer, Pair> idx2Comp;
	
	
	/*
	 * Note1: the position on the board <x, y> is transfered to
	 * y * boardWidth + x, so that each position can be represented
	 * by a single unique integer
	 * 
	 * Not2: as in this problem there is no restriction for the 
	 * components adjacent, so we can assume that every component
	 * is adjacent to each other.
	 */
	public CircuitBoardCSP(List<List<Integer>> comps, int w, int h){
		width = w;
		height = h;
		components = new ArrayList<Pair>();
		for(List<Integer> comp : comps){
			Pair component = new Pair(comp.get(0), comp.get(1));
			components.add(component);
		}
		
		comp2Idx = new HashMap<>();
		idx2Comp = new HashMap<>();
		for(int i = 0; i < components.size(); i++){
			comp2Idx.put(components.get(i), i);
			idx2Comp.put(i, components.get(i));
		}
		
		//Set CSP's members
		assignment = new int[comps.size()];
		Arrays.fill(assignment, -1);
		varNum = comps.size();
		domain = buildDomain();
		constraint = buildConstraint();
		domainOriginal = domainCopy(domain);
		
		adjPairList = new HashSet<>();
		adjList = new HashMap<>();
		
		for(int i = 0; i < components.size(); i++){
			adjList.put(i, new HashSet<>());
			for(int j = 0; j < components.size(); j++){
				if(i == j) continue;
				adjList.get(i).add(j);
				adjPairList.add(new Pair(i, j));
			}
		}
		
	}
	
	/*
	 * method to build the domain. In this problem for each component,
	 * the domain would be every block in the circuit board except for
	 * those that will make components out of the board's bound
	 */
	private HashMap<Integer, HashSet<Integer>> buildDomain(){
		HashMap<Integer, HashSet<Integer>> builder = new HashMap<>();
		for(Pair comp : components){
			int idx = comp2Idx.get(comp);
			builder.put(idx, new HashSet<>());
			for(int x = 0; x + comp.x - 1 < width; x++){
				for(int y = 0; y + comp.y - 1 < height; y++){
					builder.get(idx).add(y * width + x);
				}
			}
		}
		return builder;
	}
	
	/*
	 * method to build the constraint. The rule in this problem is to
	 * make sure that no two components overlap each other. 
	 */
	private HashMap<Pair, HashSet<Pair>> buildConstraint(){
		HashMap<Pair, HashSet<Pair>> builder = new HashMap<>();
		for(Pair comp1 : components){
			for(Pair comp2 : components){
				if(comp1.equals(comp2)) continue;
				Pair varPair = new Pair(comp2Idx.get(comp1), comp2Idx.get(comp2));
				HashSet<Pair> varConstraints = getPairConstraint(comp1, comp2);
				builder.put(varPair, varConstraints);
			}
		}
		return builder;
	}
	
	/*
	 * helper method used by buildConstraint. Given a pair of components, return
	 * all their allowable value pair.
	 */
	private HashSet<Pair> getPairConstraint(Pair comp1, Pair comp2){
		int compIdx1 = comp2Idx.get(comp1);
		int compIdx2 = comp2Idx.get(comp2);
		HashSet<Pair> pairConstraints = new HashSet<Pair>();
		for(Integer domain1 : domain.get(compIdx1)){
			for(Integer domain2 : domain.get(compIdx2)){
				int x1 = domain1 % width;
				int y1 = domain1 / width;
				double x1_mid = (double)(x1) + (double)(comp1.x) / 2.0;
				double y1_mid = (double)(y1) + (double)(comp1.y) / 2.0;
				int x2 = domain2 % width;
				int y2 = domain2 / width;
				double x2_mid = (double)(x2) + (double)(comp2.x) / 2.0;
				double y2_mid = (double)(y2) + (double)(comp2.y) / 2.0;
				if(Math.abs(x1_mid - x2_mid) >=  (double)(comp1.x + comp2.x) / 2.0 ||
						Math.abs(y1_mid - y2_mid) >= (double)(comp1.y + comp2.y) / 2.0)
					pairConstraints.add(new Pair(domain1, domain2));
			}
		}
		return pairConstraints;
	}
	
	public void printAssignment(){
		char[][] board = new char[height][width];
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++)
				board[i][j] = '.';
		}
		for(int i = 0; i < varNum; i++){
			int x = assignment[i] % width;
			int y = assignment[i] / width;
			int compWidth = idx2Comp.get(i).x;
			int compHeight = idx2Comp.get(i).y;
			for(int j = 0; j < compWidth; j++){
				for(int k = 0; k < compHeight; k++)
					board[y + k][x + j] = (char)('A' + i);
			}
		}
		
		for(int i = 0; i < height; i++)
			System.out.println(Arrays.toString(board[i]));
	}
}
