package mstparser.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import mstparser.DependencyInstance;
import mstparser.DependencyInstancesVotingGroup;
import mstparser.DependencyInstancesVotingGroupParameters;

/** 
 * Maria Mateva: 
 * this is a class for the input for the {@link DependencyInstancesVoting}
 * (obviously based on {@link MSTReader})
 * 
 * 
 * The expected format is as follows; "\t" is the standard separator for compatibility:
 * line1: N_parsers
 * line2: weight1 weight2 ... weight_Nparsers   // Labeled or Unlabeled
 * 	(e.g. we have 12 parsers and want to vote No.1, No.3 and No.12)
 * 		If "weighted" edges version - this line is just empty
 * line3: empty
 * line4: sentence words
 * line5: POS tags
 * line6: labels or heads
 * line7: heads or empty(if in unlabeled mode)
 * line8: weights or empty(weights go with labeled)
 * line8: empty or next dependency(if in unlabeled mode) 
 **/
public class MSTVotingReader extends MSTReader {
	boolean getAplphabetOnly = false;
	
	/** number of total parsers that we choose **/
	private int N; 

	/** weights of all parsers - LABELED or UNLABELED **/
	private double [] weightsOfParsers;
	private ArrayList<Double> weightsOfChosenParsers;
	
	/** indexes of chosen parsers **/
	private int [] chosenParsers;
	private Set<Integer> chosenParsersSet;
	
	private int instancesCount;
	private DependencyInstancesVotingGroup currentVotingGroup;
	private ArrayList<DependencyInstancesVotingGroup> votingGroups;
	private DependencyInstancesVotingGroupParameters votingParams;
	
	/**
	 * If the reader should read an extra line of weights. 
	 * In this case also the initial lines are different.
	 */
	private boolean weighted;
	
	public MSTVotingReader (int [] chosenParsers, boolean weighted) {
		super(weighted);
		this.getAplphabetOnly = false;
		this.instancesCount = 0;
		this.chosenParsers = chosenParsers;
		this.chosenParsersSet = getSet(chosenParsers);
		this.votingGroups = new ArrayList<DependencyInstancesVotingGroup>(chosenParsers.length);
		this.weighted = weighted;
	}
	
	/**
	 * A constructor for building the alphabet of relations
	 * @param file
	 */
	public MSTVotingReader (String file, boolean weighted) {
		super(weighted);
		this.getAplphabetOnly = true;
		this.instancesCount = 0;
		this.chosenParsers = getArrayOfIndexes(lookupNumberOfParsers(file));
		this.chosenParsersSet = getSet(chosenParsers);
	}
	
	public void setVotingParams(
			DependencyInstancesVotingGroupParameters votingParams) {
		this.votingParams = votingParams;
		this.currentVotingGroup = new DependencyInstancesVotingGroup(
				votingParams, weightsOfChosenParsers);
	}

	public int getN() {
		return N;
	}
	
	public DependencyInstance getNext() throws IOException {
		DependencyInstance depInst = super.getNext();
		if (depInst != null && !getAplphabetOnly) {
//			if (weighted) {
//				String [] weightsLine = inputReader.readLine().split("\t");
//				double [] weights = new double[weightsLine.length + 1];
//				weights[0] = 0; // empty cell
//				for (int i = 0; i < weightsLine.length; i++){
//					weights[i+1] = Double.parseDouble(weightsLine[i]);
//				}
//				depInst.setWeights(weights);
//			}
//			System.out.println("Instance: " + instancesCount);
			int numberOfParser = 1 + instancesCount % N;
			if (chosenParsersSet.contains(numberOfParser)) {
				currentVotingGroup.instances.add(depInst);
			}
			if (numberOfParser == N) {
				currentVotingGroup.validateGroup();
				votingGroups.add(currentVotingGroup);
				currentVotingGroup = new DependencyInstancesVotingGroup(votingParams, weightsOfChosenParsers);
			}
		} 
		instancesCount += 1;
		return depInst;
	}
	
	public ArrayList<DependencyInstancesVotingGroup> getVotingGroups() throws IOException {
		return votingGroups;
	}
	
	public ArrayList<Double> getChosenParsersWeights() {
		ArrayList<Double> res = new ArrayList<Double>();
		for (int i : chosenParsers) {
			res.add(weightsOfParsers[i - 1]); // the index starts from 1, not 0
		}
		return res;
	}
	
	/**
	 * Returns if labeled or not;
	 * also sets most of the parameter's values:
	 * labeled, inputReader, N, weightsOfParserLAB, weightsOfParserULAB
	 */
	@Override
	public boolean startReading(String file) throws IOException {
		labeled = fileContainsLabels(file);
		inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
		
		// number of parsers
		String line = inputReader.readLine();
		N = Integer.parseInt(line);
		weightsOfParsers = new double[N];

		line = inputReader.readLine();
		String [] parsersWeightsL = line.split("\\t"); // labeled
		if (parsersWeightsL.length != N) {
			System.err.println("Wrong number of parsers' weights");
		}
		for (int i = 0; i < N; i++) {
			weightsOfParsers[i] = Double.parseDouble(parsersWeightsL[i]);
		} 

		inputReader.readLine();
		return labeled;
	}
	
	public void setUp() {
		weightsOfChosenParsers = getChosenParsersWeights();
		currentVotingGroup.setChosenParsersAccuracies(weightsOfChosenParsers);
	}

	@Override
	protected boolean fileContainsLabels(String filename) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(filename));
		int border; // line on which are the labels
//		if (weighted) {
//			border = 7;
//		} else {
			border = 6;
		//}
		for (int i = 0; i < border; i++) {
			in.readLine();
		}
		String line = in.readLine();
		in.close();
		if (line.trim().length() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	private Set<Integer> getSet(int [] readyGo) {
		Set<Integer> set = new HashSet<Integer>();
		for (int entity : readyGo) {
			set.add(entity);
		}
		return set;
	}
	
	/**
	 * Reads the first line to get how many parsers they are
	 * @return
	 */
	public static int lookupNumberOfParsers(String file) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			return Integer.parseInt(in.readLine());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public static int [] getArrayOfIndexes(int N) {
		int [] arr = new int [N];
		for (int i = 0; i < N; i++) {
			arr[i] = i;
		}
		return arr;
	}
}
