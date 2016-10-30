package csp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

public class MapColorCSP extends CSP{
	//map to store which city is represented by which index
	private HashMap<Integer, String> idx2City;
	//map to store which index is the representation of the city
	private HashMap<String, Integer> city2Idx;
	//map to store which color is represented by which index
	private HashMap<Integer, String> idx2Color;

	
	public MapColorCSP(List<String> cities, List<String> colors, HashMap<String, List<String>> adj){
		idx2City = new HashMap<>();
		city2Idx = new HashMap<>();
		idx2Color = new HashMap<>();
		
		
		for(int i = 0; i < cities.size(); i++){
			idx2City.put(i, cities.get(i));
			city2Idx.put(cities.get(i), i);
		}
		
		for(int i = 0; i < colors.size(); i++)
			idx2Color.put(i, colors.get(i));
		
		//Set CSP's members
		
		adjPairList = new HashSet<>();
		adjList = new HashMap<>();
		
		for(Entry<String, List<String>> keyVal : adj.entrySet()){
			int cityIdx1 = city2Idx.get(keyVal.getKey());
			adjList.put(cityIdx1, new HashSet<>());
			for(String city2 : keyVal.getValue()){
				int cityIdx2 = city2Idx.get(city2);
				adjPairList.add(new Pair(cityIdx1, cityIdx2));
				adjList.get(cityIdx1).add(cityIdx2);
			}
		}
		
		assignment = new int[cities.size()];
		Arrays.fill(assignment, -1);
		varNum = cities.size();
		domain = buildDomain(cities.size(), colors.size());
		domainOriginal = domainCopy(domain);
		constraint = buildConstraint();
	}
	
	/*
	 * This method builds the domain. In this problem all the variables
	 * have the domain of all the colors.
	 */
	private HashMap<Integer, HashSet<Integer>> buildDomain(int cityNum, int colorNum){
		HashMap<Integer, HashSet<Integer>> builder = new HashMap<>();
		for(int i = 0; i < cityNum; i++){
			builder.put(i, new HashSet<Integer>());
			for(int j = 0; j < colorNum; j++)
				builder.get(i).add(j);
		}
		return builder;
	}
	
	/*
	 * This method build the constraint map including all the allowable pairs of values 
	 * for pairs of variables. For two variables that are adjacent, each pair of different
	 * colors is allowed
	 */
	private HashMap<Pair, HashSet<Pair>> buildConstraint(){
		HashMap<Pair, HashSet<Pair>> builder = new HashMap<>();
		for(Pair cityPair : adjPairList){
			HashSet<Pair> allPair = getAllColorPair(cityPair);
			builder.put(cityPair, allPair);
		}
		return builder;
	}
	
	/*
	 * helper function used by buildConstraint
	 */
	private HashSet<Pair> getAllColorPair(Pair cityPair){
		HashSet<Pair> allPair = new HashSet<>();
		HashSet<Integer> var1Domain = domain.get(cityPair.x);
		HashSet<Integer> var2Domain = domain.get(cityPair.y);
		for(Integer color1 : var1Domain){
			for(Integer color2 : var2Domain){
				if(color1 == color2) continue;
				Pair pair = new Pair(color1, color2);
				allPair.add(pair);
			}
		}
		return allPair;
	}
	
	public void printAssignment(){
		for(int i = 0; i < varNum; i++){
			System.out.print(idx2City.get(i) + "->" + idx2Color.get(assignment[i]) + " ");
		}
		System.out.println();
	}
}
