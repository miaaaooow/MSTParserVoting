package mstparser;

import java.util.ArrayList;

/**
 * A group of dependency instances to participate in voting 
 * @author Maria Mateva
 *
 */
public class DependencyInstancesVoting {
	public static final String EQUAL_WEIGHTS_MODE = "equal";
	public static final String ACCURACIES_MODE = "accuracies";
	public static final String AVG_ACCURACIES_MODE = "avg-accuracies";
	
	
	ArrayList<DependencyInstance> instances = new ArrayList<DependencyInstance>();
	ArrayList<Double> parserAccuracies;
	String mode;
	
	public DependencyInstancesVoting(ArrayList<DependencyInstance> instances, ArrayList<Double> parserAccuracies, String mode) {
		this.instances = instances;
		this.parserAccuracies = parserAccuracies;
		this.mode = mode;
	}
	
	/**
	 * Weights of arrow from A to B with label C
	 * 
	 * @param mode
	 * @return
	 */
	public double [][][] buildGraphVotesMatrix(String mode) {
		// sentence_length x sentence_length x LAB
		if (mode.equals(EQUAL_WEIGHTS_MODE)) {
			
		} else if (mode.equals(ACCURACIES_MODE)) {
			
		} else {
			// AVG ACCURACIES MODE
			
		}
		return null;
	}
	
}
