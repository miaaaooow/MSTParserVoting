package mstparser.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import mstparser.DependencyInstance;
import mstparser.DependencyInstancesVotingGroup;

/** 
 * Maria Mateva: 
 * this is a class for the input for the {@link DependencyInstancesVoting}
 * (obviously based on {@link MSTReader})
 * 
 * 
 * The expected format is as follows; "\t" is the standard separator for compatibility:
 * line1: N_parsers
 * line2: weight1 weight2 ... weight_Nparsers   // Labeled
 * line3: weight1 weight2 ... weight_Nparsers   // Unlabeled
 * 	(e.g. we have 12 parsers and want to vote No.1, No.3 and No.12)
 * line4: empty
 * line5: sentence words
 * line6: pos tags
 * line7: labels/heads
 * line8: heads or empty(if in unlabeled mode)
 * line9: empty or next dependency(if in unlabeled mode) 
 **/
public class MSTVotingReader extends MSTReader {
	
	/** number of total parsers that we choose **/
	private int N; 

	/** weights of all parsers - LABELED **/
	private double [] weightsOfParsersLAB;
	
	/** weights of all parsers - UNLABELED **/
	private double [] weightsOfParsersULAB;
	
	/** indexes of chosen parsers **/
	private int [] chosenParsers;
	
	private Set<Integer> chosenParsersSet;
	
	private int instancesCount = 0;
	
	private ArrayList<DependencyInstancesVotingGroup> votingGroups;
	
	public MSTVotingReader () {
		this.votingGroups = new ArrayList<DependencyInstancesVotingGroup>();

	}
	public DependencyInstance getNext() throws IOException {
		instancesCount += 1;
		DependencyInstance depInst = super.getNext();
		if (depInst != null) {
			System.out.println("Instance: " + instancesCount);
			
		}
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
	
	/**
	 * Returns if labeled or not
	 */
	@Override
	public boolean startReading(String file) throws IOException {
		labeled = fileContainsLabels(file);
		inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
		
		// number of parsers
		String line = inputReader.readLine();
		N = Integer.parseInt(line);
		weightsOfParsersLAB = new double[N];
		weightsOfParsersULAB = new double[N];

		// weights - labeled; unlabeled
		line = inputReader.readLine();
		String [] parsersWeightsL = line.split("\\t"); // labeled
		line = inputReader.readLine();
		String [] parsersWeightsU = line.split("\\t"); // unlabeled
		if (parsersWeightsL.length != N || parsersWeightsU.length != N ||
			(parsersWeightsL.length != parsersWeightsU.length)) {
			System.err.println("Wrong number of parsers' weights");
		}
		for (int i = 0; i < N; i++) {
			weightsOfParsersLAB[i] = Double.parseDouble(parsersWeightsL[i]);
			weightsOfParsersULAB[i] = Double.parseDouble(parsersWeightsU[i]);
		}
		
		// chosen parsers (M of all N)
		line = inputReader.readLine();
		String [] chosenParsersStr = line.split("\\t");
		int M = chosenParsersStr.length;
		if (M < 2 || M > N) {
			System.err.println("Wrong number of chosen parsers");
		}
		chosenParsers = new int [M];
		for (int i = 0; i < M; i++) {
			chosenParsers[i] = Integer.parseInt(chosenParsersStr[i]);
			if (chosenParsers[i] > N) {
				System.err.println("Suspicuous chosen parser id");
			}
		}
		Arrays.sort(chosenParsers);
		System.out.println(chosenParsers.toString());
		return labeled;
	}

	@Override
	protected boolean fileContainsLabels(String filename) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(filename));
		for (int i = 0; i < 9; i++) {
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

}
