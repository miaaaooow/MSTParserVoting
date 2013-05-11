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
	ArrayList<Double> parserAccuracies;
	boolean labeled;
	public DependencyInstancesVotingGroupParameters(Alphabet alphabet,
			String mode, ArrayList<Double> parserAccuracies, boolean labeled) {
		super();
		this.alphabet = alphabet;
		this.mode = mode;
		this.parserAccuracies = parserAccuracies;
		this.labeled = labeled;
	}	
}
