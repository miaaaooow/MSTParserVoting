package mstparser;

import java.io.IOException;
import java.util.Arrays;

import mstparser.io.MSTVotingReader;
import mstparser.io.MSTWriter;

/**
 * A pipeline for voting, where after the voting of several parses,
 * @author Maria Mateva
 *
 */
public class VotingParser {
	
	public static final String EQUAL_WEIGHTS_MODE = "equal";
	public static final String ACCURACIES_MODE = "accuracies";
	public static final String AVG_ACCURACIES_MODE = "avg-accuracies";
	/** Options to create and run this parser */
	private ParserOptions options;
	
	/** Alphabet of dependency relation types */
	private Alphabet typeAlphabet;
	
	private boolean labeled; // TODO
	
	/** indexes of chosen parsers **/
//	private int [] chosenParsers; 
	
	private MSTVotingReader votingReader;
	private MSTWriter mstWriter;
	
	public VotingParser() {
		this.typeAlphabet = new Alphabet();	
	}
	
	private void setup (ParserOptions options) throws IOException {
		createAlphabetAndVotingInstances(options.testfile);
		int [] chosenParsers = getChosenParsersIndexes(options.votingParser);
		votingReader = new MSTVotingReader(chosenParsers);
		labeled = votingReader.startReading(options.testfile);
		
		mstWriter = new MSTWriter(labeled);
		mstWriter.startWriting(options.outfile);
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
	
	
	/**
	 * Final step; writing files
	 */
	public void evaluate() throws IOException {
		if (options.eval) {
			System.out.println("\nEVALUATION PERFORMANCE:");
			DependencyEvaluator.evaluate(options.goldfile, options.outfile, "MST");
		}
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
			opts = defaultUnlabeledOptions();
		}
		VotingParser algorithm = new VotingParser();
		algorithm.setup(opts);		
		
		// do voting
	
		algorithm.mstWriter.finishWriting();
		algorithm.evaluate();
	}
	
	/** default labeled options for test **/
	private static ParserOptions defaultLabeledOptions() {
		String [] paramsForABetterWorld = {
				"voting-on:true", "voting-mode:equal",
				"voting-parsers:1,3,7,11,14",
				"test-file:all-parsers-labeled-all.mst", 
				"output-file:voting-labeled-1_3_7_11_14.mst", 
				"eval", "gold-file:gold-labeled-all.mst"
			 };
			return new ParserOptions(paramsForABetterWorld) ;
	}
	
	/** default unlabeled options for test **/
	private static ParserOptions defaultUnlabeledOptions() {
		String [] paramsForABetterWorld = {
				"voting-on:true", "voting-mode:equal",
				"voting-parsers:1,3,7,11,14",
				"test-file:all-parsers-unlabeled-all.mst", 
				"output-file:voting-unlabeled-1_3_7_11_14.mst", 
				"eval", "gold-file:gold-unlabeled-all.mst"
			 };
			return new ParserOptions(paramsForABetterWorld) ;
	}
}
