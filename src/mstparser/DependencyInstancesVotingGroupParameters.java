package mstparser;

import java.util.ArrayList;

/**
 * A group of dependency instances to participate in voting;
 * These are the instances from the chosen parsers over one single sentence.
 * 
 * @author Maria Mateva
 *
 */
public class DependencyInstancesVotingGroupParameters {
	Alphabet alphabet;
	String mode;
	boolean labeled;
	boolean weightedEdges;
	ArrayList<Double> parserAccuracies;
	public DependencyInstancesVotingGroupParameters(Alphabet alphabet,
			String mode, boolean labeled, boolean weighted) {
		super();
		this.alphabet = alphabet;
		this.mode = mode;
		this.labeled = labeled;
		this.weightedEdges = weighted;
	}	
}
