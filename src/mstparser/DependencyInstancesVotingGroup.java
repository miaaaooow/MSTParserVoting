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
		this.depAlphabet = params.alphabet;
		this.depRelAlphaSize = this.depAlphabet.size();
		this.chosenParsersAccuracies = parserAccuracies;
		this.mode = params.mode;
		this.labeled = params.labeled;
	}
	
	
	/**
	 * Says if all sentences in this group have the same length.
	 * Applied to the whole corpus will indicate consistency of data.
	 * @return true if valid
	 */
	public boolean validateGroup() {
		int length = this.instances.get(0).length();
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
		int length = this.instances.get(0).length();
		double [][] scores = new double [length][length];
		// sentence_length x sentence_length x LAB
		if (mode.equals(EQUAL_WEIGHTS_MODE)) {
			for (DependencyInstance depInst : instances) {
				for (int i = 0; i < depInst.forms.length; i++) {
					scores [depInst.heads[i]][i] += 1;
				}
			}

		} else if (mode.equals(ACCURACIES_MODE)) {

		} else {
			/** AVG ACCURACIES MODE **/

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
	public double [][][] buildGraphVotesMatrixLabeled(String mode) {
		int length = this.instances.get(0).length();
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
		
		/** Invoke Chu-Liu-Edmonds **/
		/**
		 * private static TIntIntHashMap chuLiuEdmonds(double[][] scoreMatrix,
			boolean[] currentNodes, int[][] oldI, int[][] oldO, boolean print,
			TIntIntHashMap finalEdges, TIntIntHashMap[] reps)
		 */
		
		double [][][] scoreMatrix = buildGraphVotesMatrixLabeled(mode);
		return null;
		
	}
	
	
}
