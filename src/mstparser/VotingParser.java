package mstparser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
	private MSTWriter votingWriter;
	
	public VotingParser() {
		this.typeAlphabet = new Alphabet();	
	}
	
	private void setUp (ParserOptions options) throws IOException {
		//System.out.println(options.toString());
		this.options = options;
		int [] chosenParsers = getChosenParsersIndexes(options.votingParsers);
		String targetFile = options.testfile;
		createStaticAlphabet(); // for speed up
		//createAlphabet(targetFile);
		votingReader = new MSTVotingReader(chosenParsers, options.weightedEdges);
		labeled = votingReader.startReading(targetFile);
		
		DependencyInstancesVotingGroupParameters votingParameters = 
				new DependencyInstancesVotingGroupParameters(typeAlphabet, 
						options.votingMode, labeled, options.weightedEdges);
		votingReader.setVotingParams(votingParameters);	
		votingReader.setUp();
		createVotingInstances(options.testfile);
		
		votingWriter = new MSTWriter(labeled);
		votingWriter.startWriting(options.outfile);
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
			chosenParsers[i] = Integer.parseInt(chosenParsersStr[i]);
			System.out.print(chosenParsers[i] + " ");
		}
		System.out.println();
		Arrays.sort(chosenParsers);
		return chosenParsers;
	}
	
	/**
	 * Sets up typeAlphabet 
	 * @param file - file with all examples
	 * @throws IOException
	 */
	private final void createAlphabet(String file) throws IOException {
		System.out.print("Creating Dep Rel Alphabet ... ");
		
		MSTVotingReader getAlphaReader = new MSTVotingReader(file);
		getAlphaReader.startReading(file);
		DependencyInstance instance = getAlphaReader.getNext();
		while (instance != null) {
			String[] labs = instance.deprels;
			for (int i = 0; i < labs.length; i++) {
				typeAlphabet.lookupIndex(labs[i]);
			}
			instance = getAlphaReader.getNext();
		}
		System.out.println(typeAlphabet.size());
		
		typeAlphabet.stopGrowth();
	}
	
	
	/**
	 * Sets up typeAlphabet and labeled
	 * @param file
	 * @throws IOException
	 */
	private final void createStaticAlphabet() {
		System.out.print("Creating Dep Rel Alphabet ... ");
		typeAlphabet.lookupIndex("pragadjunct");
		typeAlphabet.lookupIndex("clitic");
		typeAlphabet.lookupIndex("ROOT");
		typeAlphabet.lookupIndex("indobj");
		typeAlphabet.lookupIndex("marked");
		typeAlphabet.lookupIndex("xsubj");
		typeAlphabet.lookupIndex("<no-type>");
		typeAlphabet.lookupIndex("subj");
		typeAlphabet.lookupIndex("adjunct");
		typeAlphabet.lookupIndex("xmod");
		typeAlphabet.lookupIndex("conjarg");
		typeAlphabet.lookupIndex("xprepcomp");
		typeAlphabet.lookupIndex("prepcomp");
		typeAlphabet.lookupIndex("obj");
		typeAlphabet.lookupIndex("xcomp");
		typeAlphabet.lookupIndex("xadjunct");
		typeAlphabet.lookupIndex("mod");
		typeAlphabet.lookupIndex("conj");
		typeAlphabet.lookupIndex("comp");
		typeAlphabet.lookupIndex("punct");
		System.out.println(typeAlphabet.size());
		
		typeAlphabet.stopGrowth();
	}
	
	
	/**
	 * Sets up typeAlphabet and labeled
	 * @param file
	 * @throws IOException
	 */
	private final void createVotingInstances(String file) throws IOException {
		System.out.print("Creating Instances ... ");
		DependencyInstance instance = votingReader.getNext();
		while (instance != null) {
			instance = votingReader.getNext();
		}
		System.out.println("Done.");
	}
	
	public void vote() throws IOException {
		ArrayList<DependencyInstancesVotingGroup> groups = votingReader.getVotingGroups();
		for (DependencyInstancesVotingGroup group : groups) {
			DependencyInstance result = group.getVotedDependencyInstance();
			votingWriter.writeRaw(result);
		}
		votingWriter.finishWriting();
	}
	
	/**
	 * Final step; writing files
	 */
	public void evaluate() throws IOException {
		//System.out.println(options);
		//System.out.println(options.eval);
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

		if (args.length > 1) {
			String parsers = args[0];
			runTheParser(parsers);
		} else {
//			String[] a = { "1,3,7,11,14", "3,7,9,11,14", "7,8,9,11,14", "7,8,9,11",
//					"7,9,11", "8,9,11", "1,2,3,4,5,6,7,8,9,10,11,12,13,14" };
//			for (String parsers: a) {
//				runTheParser(parsers);
//			}
//			BufferedReader combinationsReader = new BufferedReader(new InputStreamReader(
//					new FileInputStream("combinations_2_3_5_7_pt2.txt"), "UTF8"));
			String line = "";
//			
//			while((line = combinationsReader.readLine()) != null) {
//				runTheParser(line);
//			}
//			combinationsReader.close();
			BufferedReader combinationsReader1 = new BufferedReader(new InputStreamReader(
					new FileInputStream("combinations_2_3_5_7_pt1.txt"), "UTF8"));
			
			while((line = combinationsReader1.readLine()) != null) {
				runTheParser(line);
			}
			combinationsReader1.close();
			
		}
	}
	
	public static void runTheParser(String parsers) throws IOException {
		ParserOptions opts = defaultLabeledOptions(parsers);

		VotingParser algorithm = new VotingParser();
		algorithm.setUp(opts);
		algorithm.vote();

		// algorithm.votingWriter.finishWriting();
		algorithm.evaluate();
	}
	
	/** default labeled options for test **/
	private static ParserOptions defaultLabeledOptions(String parsers) {
		String [] paramsForABetterWorld = {
				"voting-on:true", "voting-mode:avg-accuracies",
				"voting-parsers:" + parsers,
				"test-file:all-parsers-labeled-all.mst", 
				"output-file:voting-labeled-equal-" + parsers + ".mst", 
				"eval", "gold-file:gold-labeled-all.mst"
			 };
			return new ParserOptions(paramsForABetterWorld) ;
	}
	
	/** default unlabeled options for test **/
	private static ParserOptions defaultUnlabeledOptions(String commaSepParsersList) {
		String [] paramsForABetterWorld = {
				"voting-on:true", "voting-mode:avg-accuracies",
				"voting-parsers:" + commaSepParsersList,
				"test-file:all-parsers-unlabeled-all.mst", 
				"output-file:voting-unlabeled-avg-acc-" + commaSepParsersList + ".mst", 
				"eval", "gold-file:gold-unlabeled-all.mst"
			 };
			return new ParserOptions(paramsForABetterWorld) ;
	}
}
