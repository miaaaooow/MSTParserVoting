package mstparser;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

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
	
	/** indexes of chosen parsers **/
//	private int [] chosenParsers; 
	
	private MSTVotingReader votingReader;
	
	public VotingParser(ParserOptions opts) {
		this.typeAlphabet = new Alphabet();	
	}
	
	private void setup (ParserOptions options) throws IOException {
		createAlphabetAndVotingInstances(options.testfile);
		int [] chosenParsers = getChosenParsersIndexes(options.votingParser);
		votingReader = new MSTVotingReader(chosenParsers);
		labeled = votingReader.startReading(options.testfile);
	}
	
	/**
	 * Comma separated list of voting parsers indexes.
	 * 
	 * @param listOfIndexes
	 */
	private int [] getChosenParsersIndexes(String listOfIndexes) {
		String [] chosenParsersStr = listOfIndexes.split(",");
		int M = chosenParsersStr.length;
		if (M < 2) {
			System.err.println("Wrong number of chosen parsers");
		}
		System.out.println("Parsers chosen for voting: ");
		int [] chosenParsers = new int [M];
		for (int i = 0; i < M; i++) {
			System.out.print(chosenParsers[i]);
			chosenParsers[i] = Integer.parseInt(chosenParsersStr[i]);
			if (chosenParsers[i] > votingReader.getN()) {
				System.err.println("Suspicuous chosen parser id");
			}
		}
		Arrays.sort(chosenParsers);
		return chosenParsers;
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
		VotingParser algorithm = new VotingParser(opts);
		
		
	}
	
	/**
	 * Sets up typeAlphabet and labeled
	 * @param file
	 * @throws IOException
	 */
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
				"format:MST", "voting-parsers:1,3,7,11,14" };
			return new ParserOptions(paramsForABetterWorld) ;
	}
}
