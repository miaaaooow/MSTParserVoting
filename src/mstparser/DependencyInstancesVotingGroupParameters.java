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
	ArrayList<Double> parserAccuracies;
	public DependencyInstancesVotingGroupParameters(ArrayList<Double> parsersAccuracies, Alphabet alphabet,
			String mode, boolean labeled) {
		super();
		this.parserAccuracies = parsersAccuracies;
		this.alphabet = alphabet;
		this.mode = mode;
		this.labeled = labeled;
	}	
}
