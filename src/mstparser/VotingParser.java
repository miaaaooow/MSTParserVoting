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
	
	private boolean labeled;
	
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
		createStaticAlphabet(); // for speed up on BTB
		
		//createAlphabet(targetFile, options.weightedEdges);
		
		votingReader = new MSTVotingReader(chosenParsers, options.weightedEdges);
		labeled = votingReader.startReading(targetFile);
		
		DependencyInstancesVotingGroupParameters votingParameters = 
				new DependencyInstancesVotingGroupParameters(typeAlphabet, 
						options.votingMode, labeled, options.weightedEdges);
		votingReader.setVotingParams(votingParameters);	
		votingReader.setUp();
		if (targetFile != null) {
			createVotingInstances(options.testfile);
		}
		
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
	private final void createAlphabet(String file, boolean weighted) throws IOException {
		System.out.print("Creating Dep Rel Alphabet ... ");
		
		MSTVotingReader getAlphaReader = new MSTVotingReader(file, weighted);
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
	
	
	public static void runTheParser(String parsers) throws IOException {
		ParserOptions opts =  defaultWeightedOptions(parsers);
		// defaultLabeledOptions(parsers);
		VotingParser algorithm = new VotingParser();
		algorithm.setUp(opts);
		algorithm.vote();

		//algorithm.votingWriter.finishWriting();
		algorithm.evaluate();
	}
	
	/** default labeled options for test **/
	private static ParserOptions defaultLabeledOptions(String parsers) {
		String [] paramsForABetterWorld = {
				"voting-on:true", "voting-mode:equal",
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
	
	/** default weighted options for test **/
	private static ParserOptions defaultWeightedOptions(String commaSepParsersList) {
		String [] paramsForABetterWorld = {
				"voting-on:true", "voting-mode:avg-accuracies",
				"voting-parsers:" + commaSepParsersList,
				"test-file:MaxTest.mst", 
				"output-file:max-voting-weighted.mst",
				//"output-file:mult-voting-weighted-" + commaSepParsersList + ".mst",
				"eval", "gold-file:gold-weighted.mst", 
				"weighted-edges:true"
			 };
			return new ParserOptions(paramsForABetterWorld) ;
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
	 * Run voting style with Chu-Liu-Edmonds
	 * @param args
	 */
	public static void main(String[] args) throws IOException {

//		if (args.length > 1) {
//			String parsers = args[0];
//			runTheParser(parsers);
//		} else {
//			String[] a = { "1,2,3,4,5,6,7,8,9,10,11,12,13,14" ,
//					"10,11,13", "9,10,11,13", "7,9,10,11,13" 
//					 };
//			for (String parsers: a) {
//				runTheParser(parsers);
//			}
			String[] combinations = { "combinations3_5_7_of_14.txt", 
					"combinations_2_14.txt", //"combinations_3_14.txt", 
					"combinations_4_14.txt",
					"combinations_6_14.txt", "combinations_8_14.txt",
					"combinations_9_14.txt", "combinations_10_14.txt",
					"combinations_11_14.txt", "combinations_12_14.txt",
					"combinations_13_14.txt" };
			for (String combo: combinations) {
			
				BufferedReader combinationsReader = new BufferedReader(new InputStreamReader(
						new FileInputStream(combo), "UTF8"));
				String line = "";
				
				while((line = combinationsReader.readLine()) != null) {
					runTheParser(line);
				}
				
				combinationsReader.close();
//			}
			
		}
	}
}
