package mstparser;

import gnu.trove.TIntIntHashMap;

import java.util.ArrayList;
import static mstparser.VotingParser.*;

/**
 * A group of dependency instances to participate in voting;
 * These are the instances from the chosen parsers over one single sentence.
 * 
 * @author Maria Mateva
 *
 */
public class DependencyInstancesVotingGroup {

	/**
	 * Instances from the chosen parsers
	 */
	public ArrayList<DependencyInstance> instances = new ArrayList<DependencyInstance>();
	
	/**
	 * Corresponding accuracies of the above(weight); so same length
	 */
	ArrayList<Double> chosenParsersAccuracies;
	
	/**
	 * Mode of voting 
	 */
	String mode;
	
	/**
	 * Types alphabet; alphabet of the dependency types
	 */
	Alphabet depAlphabet; 
	
	/**
	 * Alphabet size
	 */
	int depRelAlphaSize;
	
	/**
	 * Labeled or unlabeled mode
	 */
	boolean labeled;
	
	public DependencyInstancesVotingGroup(ArrayList<DependencyInstance> instances, 
			ArrayList<Double> parserAccuracies, String mode, boolean labeled, Alphabet alpha) {
		this.instances = instances;
		this.chosenParsersAccuracies = parserAccuracies;
		this.depAlphabet = alpha;
		this.depRelAlphaSize = depAlphabet.size();
		this.mode = mode;
		this.labeled = labeled;
	}
	
	public DependencyInstancesVotingGroup(DependencyInstancesVotingGroupParameters params, 
			ArrayList<Double> parserAccuracies) {
		this.instances = new ArrayList<DependencyInstance>();
		this.depAlphabet = params.alphabet;
		this.depRelAlphaSize = this.depAlphabet.size();
		this.chosenParsersAccuracies = parserAccuracies;
		this.mode = params.mode;
		this.labeled = params.labeled;
	}
	
	public void setChosenParsersAccuracies(ArrayList<Double> chosenParsersAccuracies) {
		this.chosenParsersAccuracies = chosenParsersAccuracies;
	}
	
	/** Sentence properties that apply to the whole group **/
	public int length() {
		if (instances.get(0) != null) {
			return instances.get(0).length();
		}
		return 0;		
	}
	
	public String [] forms() {
		if (instances.get(0) != null) {
			return instances.get(0).forms;
		}
		return null;
	}
	
	public String [] postags() {
		if (instances.get(0) != null) {
			return instances.get(0).postags;
		}
		return null;
	}
	
	/**
	 * Says if all sentences in this group have the same length.
	 * Applied to the whole corpus will indicate consistency of data.
	 * @return true if valid
	 */
	public boolean validateGroup() {
		int length = length();
		for (DependencyInstance depInst : instances) {
			if (depInst.length() != length) {
				System.err.println("Inaccurate Group!");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Weights of arrow from A to B
	 * a x b
	 * [a1][b1] means a1 -> b1
	 * 
	 * UNLABELED
	 * 
	 * @param mode
	 * @return
	 */
	public double [][] buildGraphVotesMatrixUnlabeled() {
		int length = length();
		double [][] scores = new double [length][length];
		int [][] counts = new int [length][length];
		
		// sentence_length x sentence_length x LAB
		if (mode.equals(EQUAL_WEIGHTS_MODE)) {
			for (DependencyInstance depInst : instances) {
				for (int i = 0; i < depInst.forms.length; i++) {
					if (depInst.heads[i] > -1) { // not a root index
						scores [depInst.heads[i]][i] += 1;
					}
				}
			}

		} else if (mode.equals(ACCURACIES_MODE)) {
			int count = 0;
			for (DependencyInstance depInst : instances) {
				for (int i = 0; i < depInst.forms.length; i++) {
					if (depInst.heads[i] > -1) { // not a root index
						scores [depInst.heads[i]][i] += chosenParsersAccuracies.get(count);
					}
				}
				count += 1;
			}

		} else {
			/** AVG ACCURACIES MODE **/
			
			int count = 0;
			for (DependencyInstance depInst : instances) {
				for (int i = 0; i < depInst.forms.length; i++) {
					if (depInst.heads[i] > -1) { // not a root index
						scores [depInst.heads[i]][i] += chosenParsersAccuracies.get(count);
						counts [depInst.heads[i]][i] += 1;
					}
				}
				count += 1;
			}
			for (int i = 0; i < scores.length; i++) {
				for (int j = 0; j < scores.length; j++) {
					if (counts[i][j] != 0) {
						scores [i][j] = ((double) scores[i][j]) / counts[i][j];
					}
				}
			}
		}	
		return scores;
	}
	
	/**
	 * Weights of arrow from A to B with label C
	 * a x b x c
	 * 
	 * LABELED
	 * 
	 * @param mode
	 * @return
	 */
	public double [][][] buildGraphVotesMatrixLabeled() {
		int length = length();
		double [][][]scores = new double [length][length][depAlphabet.size()];
		if (mode.equals(EQUAL_WEIGHTS_MODE)) {
			for (DependencyInstance depInst : instances) {
				for (int i = 0; i < depInst.forms.length; i++) {
					int index = depAlphabet.lookupIndex(depInst.deprels[i]);
					scores [depInst.heads[i]][i][index] += 1;
					
					// TODO compress matrix
				}
			}
		} else if (mode.equals(ACCURACIES_MODE)) {

		} else {
			/** AVG ACCURACIES MODE **/
			
			int parserIndex = 0;
			int [][][] counts = new int [length][length][];
			for (DependencyInstance depInst : instances) {
				double parserScore = chosenParsersAccuracies.get(parserIndex);
				for (int i = 0; i < depInst.forms.length; i++) {
					int index = depAlphabet.lookupIndex(depInst.deprels[i]);
					scores [depInst.heads[i]][i][index] += parserScore;
					counts [depInst.heads[i]][i][index] += 1;
				}
				parserIndex += 1;
			}

		}
		return scores;
	}
	
	private double [][] compressMatrix(double [][][] scores) {
		return null ;
	}
	
	public DependencyInstance getVotedDependencyInstance() {
		validateGroup();		
		int len = length();

		DependencyInstance resultDI = null;
		
		/** Chu-Liu-Edmonds Defaults */
		boolean [] currentNodes = new boolean[len];
		TIntIntHashMap[] reps = new TIntIntHashMap[len];
		for (int i = 0; i < len; i++){
			currentNodes[i] = true;
			reps[i] = new TIntIntHashMap();
			reps[i].put(i, 0);
		}
		int [][] oldI = new int [len][len];
		int [][] oldO = new int [len][len];
		
		for (int i = 0; i < len; i++) {
			for (int j = 0; j < len; j++) {
				oldI[i][j] = i;
				oldO[i][j] = j;
				if (i == j || j == 0) {
					continue; // no self loops of i --> 0
				}
			}
		}
		/** Invoke Chu-Liu-Edmonds **/
		/**
		 * private static TIntIntHashMap chuLiuEdmonds(double[][] scoreMatrix,
			boolean[] currentNodes, int[][] oldI, int[][] oldO, boolean print,
			TIntIntHashMap finalEdges, TIntIntHashMap[] reps)
		 */
		if (labeled) {
			double [][][] scoreMatrix = buildGraphVotesMatrixLabeled();
		} else {
		
			double [][] scores = buildGraphVotesMatrixUnlabeled();
			TIntIntHashMap result = 
					DependencyDecoder.chuLiuEdmonds(scores, currentNodes, oldI, oldO, false, new TIntIntHashMap(), reps);
			int[] parse = new int[len];
			int[] res = result.keys();
			for (int i = 0; i < res.length; i++) {
				int ch = res[i];
				int pr = result.get(res[i]);
				parse[ch] = pr;
			}
			resultDI = new DependencyInstance(forms(), postags(), postags(), parse);
			//System.out.println(resultDI);
		}
		return resultDI;
		
	}
	
	
}
