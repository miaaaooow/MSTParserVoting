package mstparser;

import java.util.ArrayList;
import static mstparser.VotingParser.*;

/**
 * A group of dependency instances to participate in voting 
 * @author Maria Mateva
 *
 */
public class DependencyInstancesVotingGroup {

	ArrayList<DependencyInstance> instances = new ArrayList<DependencyInstance>();
	ArrayList<Double> parserAccuracies;
	String mode;
	Alphabet depAlphabet; // types alphabet; alphabet of the dependency types
	
	int length;
	int depRelAlphaSize;
	
	public DependencyInstancesVotingGroup(ArrayList<DependencyInstance> instances, 
			ArrayList<Double> parserAccuracies, String mode, Alphabet alpha) {
		this.instances = instances;
		this.parserAccuracies = parserAccuracies;
		this.mode = mode;
		this.length = instances.get(0).length();
	}
	
	/**
	 * Says if all sentences in this group have the same length.
	 * Applied to the whole corpus will indicate consistency of data.
	 * @return true if valid
	 */
	public boolean validateGroup() {
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
	 * 
	 * UNLABELED
	 * 
	 * @param mode
	 * @return
	 */
	public double [][] buildGraphVotesMatrixUnlabeled(String mode) {
		double [][] scores = new double [length][length];
		// sentence_length x sentence_length x LAB
		if (mode.equals(EQUAL_WEIGHTS_MODE)) {

		} else if (mode.equals(ACCURACIES_MODE)) {

		} else {
			/** AVG ACCURACIES MODE **/

		}
	
		
		return null;
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
		return null;
	}
	
	
	
}
