package mstparser;

import java.io.IOException;

import mstparser.io.MSTVotingReader;

/**
 * A pipeline for voting, where after the voting of several parses,
 * @author Maria Mateva
 *
 */
public class VotingParser {
	
	public static final String EQUAL_WEIGHTS_MODE = "equal";
	public static final String ACCURACIES_MODE = "accuracies";
	public static final String AVG_ACCURACIES_MODE = "avg-accuracies";
	
	/** Alphabet of dependency relation types */
	private Alphabet typeAlphabet;
	
	private boolean labeled; // TODO
	
	private MSTVotingReader votingReader;
	
	public VotingParser() {
		this.typeAlphabet = new Alphabet();
		this.votingReader = new MSTVotingReader();
	}

	
	/**
	 * Run voting style with Chu-Liu-Edmonds
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		ParserOptions opts;
		if (args.length > 1) {
			opts = new ParserOptions(args);
		} else {
			opts = defaultOptions();
		}
		VotingParser algorithm = new VotingParser();
		algorithm.createAlphabetAndVotingInstances(opts.testfile);
		
	}
	
	private final void createAlphabetAndVotingInstances(String file) throws IOException {
		System.out.print("Creating Dep Rel Alphabet ... ");
		labeled = votingReader.startReading(file);

		DependencyInstance instance = votingReader.getNext();
		while (instance != null) {
			String[] labs = instance.deprels;
			for (int i = 0; i < labs.length; i++) {
				typeAlphabet.lookupIndex(labs[i]);
			}
			instance = votingReader.getNext();
		}
		typeAlphabet.stopGrowth();

		System.out.println("Done.");
	}
	
	private static ParserOptions defaultOptions() {
		String [] paramsForABetterWorld = {
				"voting-on:true", "voting-mode:equal",
				"test-file:btb-all.mst", 
				"output-file:voting-result.txt", 
				"eval",  "gold-file:btb-gold.mst",
				"format:MST" };
			return new ParserOptions(paramsForABetterWorld) ;
	}
}
