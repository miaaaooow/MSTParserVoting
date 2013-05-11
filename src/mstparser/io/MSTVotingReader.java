package mstparser.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
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
 * line3: empty
 * line4: sentence words
 * line5: POS tags
 * line6: labels/heads
 * line7: heads or empty(if in unlabeled mode)
 * line8: empty or next dependency(if in unlabeled mode) 
 **/
public class MSTVotingReader extends MSTReader {

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
	
	
	public MSTVotingReader (int [] chosenParsers) {
		this.instancesCount = 0;
		this.chosenParsers = chosenParsers;
		this.chosenParsersSet = getSet(chosenParsers);
		this.votingGroups = new ArrayList<DependencyInstancesVotingGroup>(chosenParsers.length);
	}
	
	public void setVotingParams(
			DependencyInstancesVotingGroupParameters votingParams) {
		this.votingParams = votingParams;
		this.currentVotingGroup = new DependencyInstancesVotingGroup(votingParams);
	}

	public int getN() {
		return N;
	}
	
	public DependencyInstance getNext() throws IOException {
		DependencyInstance depInst = super.getNext();
		if (depInst != null) {
			System.out.println("Instance: " + instancesCount);
			int numberOfParser = 1 + instancesCount % N;
			if (chosenParsersSet.contains(numberOfParser)) {
				currentVotingGroup.instances.add(depInst);
			}
			if (numberOfParser == N) {
				votingGroups.add(currentVotingGroup);
				currentVotingGroup = new DependencyInstancesVotingGroup(votingParams);
			}
		} 
		instancesCount += 1;
		return depInst;
	}
	
	public DependencyInstancesVotingGroup getVotingGroups() throws IOException {
		int M = chosenParsers.length; // number of parsers in the group
		ArrayList<DependencyInstance> trees = new ArrayList<DependencyInstance>(M);
		for (int i = 0; i < M; i++) {
			trees.add(getNext());
		}
		
		return null;
	}
	
	public ArrayList<Double> getChosenParsersWeights() {
		ArrayList<Double> res = new ArrayList<Double>();
		for (int i : chosenParsers) {
			res.add(weightsOfParsers[i]);
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

		// weights - labeled; unlabeled
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

	@Override
	protected boolean fileContainsLabels(String filename) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(filename));
		for (int i = 0; i < 7; i++) {
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

}
