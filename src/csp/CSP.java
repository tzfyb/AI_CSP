package csp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;

public abstract class CSP {
	//Store the estimated assignment
	protected int[] assignment;
	//to store the number of veriables
	protected int varNum;
	//to store the constraint pairs
	protected HashMap<Pair, HashSet<Pair>> constraint;
	//to store the domain for each variable. It may change
	//during the process of computing
	protected HashMap<Integer, HashSet<Integer>> domain;
	//a copy of the original domain. Will not change once created
	protected HashMap<Integer, HashSet<Integer>> domainOriginal;
	//the adjacent list for each variable
	protected HashMap<Integer, HashSet<Integer>> adjList;
	//to store each pair of adjacent variables
	protected HashSet<Pair> adjPairList;
	//flags to indicate applying corresponding methods or not
	private boolean MRV = false;
	private boolean LCV = false;
	private boolean AC3 = false;
	
	/*
	 * class for Pair, custom equal and hashcode and
	 * compareTo. For storing a pair of variables or
	 * values.
	 */
	public class Pair implements Comparable<Pair>{
		protected int x;
		protected int y;
		
		public Pair(int _a, int _b){
			x = _a; y = _b;
		}
		
		@Override
	    public boolean equals(final Object o) {
	        if (this == o) return true;
	        
	        if (!(o instanceof Pair)) return false;

	        final Pair pair = (Pair) o;
	        if (x != pair.x) return false;
	        if (y != pair.y) return false;
	        return true;
	    }

	    @Override
	    public int hashCode() {
	        int result = x;
	        result = 31 * result + y;
	        return result;
	    }
	    
	    @Override
		public int compareTo(Pair o) {
			return (int) Math.signum(- this.x + o.x);
		}
	    
	    @Override
	    public String toString(){
	    	return '[' + Integer.toString(x) + ',' + Integer.toString(y) + ']';
	    }
	}
	
	//simple dfs with backtrack
	public boolean dfs(){	
		int var = nextVar();
		if(var == -1) return true;
		List<Integer> values = getValues(var);
		HashMap<Integer, HashSet<Integer>> dupDomain;
		for(Integer val : values){
			if(checkConsistent(var, val)){
				//try the assignment and domain
				assignment[var] = val;
				dupDomain = domainCopy(domain);
				domain.get(var).clear();
				domain.get(var).add(val);
				if(AC3) ac_3();
				if(dfs()) return true;
				
				//recover the assignment and the domain
				assignment[var] = -1;
				domain.clear();
				domain = dupDomain;
			}
		}
		
		return false;
	}
	/*
	 * Given a variable and its value , check if the assignment 
	 * can still maintain consistency
	 */
	private boolean checkConsistent(int var, int val){		
		for(int i = 0; i < varNum; i++){
			if(var == i || assignment[i] == -1) continue;
			Pair varPair = new Pair(var, i);
			Pair valPair = new Pair(val, assignment[i]);
			if(constraint.containsKey(varPair)){
				if(!constraint.get(varPair).contains(valPair))
					return false;
			}
		}
		
		return true;
	}
	
	/*
	 * Choose the next variable to be computed. If MRV sets 
	 * to true, then this method will choose the variable 
	 * that has least domain size as the next estimating
	 * variable. Otherwise the first variable that has not
	 * been estimated will be chosen as the next variable
	 */
	int nextVar(){
		if(MRV){
			int minDomainSize = Integer.MAX_VALUE;
			int minVar = -1;
			for(int i = 0; i < varNum; i++){
				if(assignment[i] == -1 && domain.get(i).size() < minDomainSize){
					minDomainSize = domain.get(i).size();
					minVar = i;
				}
			}
			return minVar;
		} else{
			for(int i = 0; i < varNum; i++)
				if(assignment[i] == -1)
					return i;
		}
		return -1;
	}
	
	/*
	 * Get list of estimating values for the current variable. 
	 * if LCV sets to true, the value will be sorted in decreasing order
	 * in terms of the conflict the value rises. Otherwise, this 
	 * method simply return the domain for that variable.
	 */
	private List<Integer> getValues(int var){
		List<Integer> values = new ArrayList<>();
		if(LCV){
			PriorityQueue<Pair> q = new PriorityQueue<Pair>();
			HashSet<Integer> varDomain = domain.get(var);
			for(int val : varDomain){
				int ruledSize = 0;
				assignment[var] = val;
				for(int adjVar : adjList.get(var)){
					if(assignment[adjVar] == -1 &&
							domain.get(adjVar).contains(val))
						ruledSize++;
				}
				assignment[var] = -1;
				Pair pair = new Pair(ruledSize, val);
				q.add(pair);
			}
			
			while(!q.isEmpty())
				values.add(q.poll().y);
		}
		else{
			for(int val : domain.get(var))
				values.add(val);
		}
		return values;
	}
	
	/*
	 * MAC3 method. If AC3 sets to true, then this method will apply before
	 * the dfs dive into next node. This method will remove any conflict 
	 * according to the current estimated values.
	 */
	private void ac_3(){
		Queue<Pair> q = new LinkedList<Pair>();
		for(Pair pair : adjPairList)
			q.add(pair);
		while(!q.isEmpty()){
			Pair cur = q.poll();
			if(assignment[cur.x] != -1) continue;
			if(removeInconVal(cur.x, cur.y)){
				for(int var : adjList.get(cur.x)){
					if(var != cur.y && assignment[var] != -1)
						q.add(new Pair(var, cur.x));
				}
			}
		}
	}
	
	/*
	 * remove inconsistent values method used by ac_3 method
	 */
	private boolean removeInconVal(int var1, int var2){
		boolean removed = false;
		boolean found = false;
		Pair varPair = new Pair(var1, var2);
		if(!constraint.containsKey(varPair)) return false;
		HashSet<Pair> cur = constraint.get(varPair);
		HashSet<Integer> var1Domain = new HashSet<Integer>();
		var1Domain.addAll(domain.get(var1));
		for(int val1 : var1Domain){
			for(int val2 : domain.get(var2)){
				if(cur.contains(new Pair(val1, val2))){
					found = true;
					break;
				}
			}
			if(found == false){
				domain.get(var1).remove(val1);
				removed = true;
			}
			found = false;
		}
		return removed;
	}
	
	/*
	 * Copy the domain and return
	 */
	public HashMap<Integer, HashSet<Integer>> domainCopy(HashMap<Integer, HashSet<Integer>> ori){
		HashMap<Integer, HashSet<Integer>> duplicatedDomain = new HashMap<>();
		for(Entry<Integer, HashSet<Integer>> keyVal : ori.entrySet()){
			int var = keyVal.getKey();
			HashSet<Integer> curSet = new HashSet<>();
			for(Integer val : keyVal.getValue())
				curSet.add(val);
			duplicatedDomain.put(var, curSet);
		}
		return duplicatedDomain;
	}
	
	public void setMRV(boolean mrv){MRV = mrv;}
	
	public void setLCV(boolean lcv){LCV = lcv;}
	
	public void setAC3(boolean ac3){AC3 = ac3;}
	
	//reset all the states.
	public void reset(){
		Arrays.fill(assignment, -1);
		domain = domainCopy(domainOriginal);
	}
}



