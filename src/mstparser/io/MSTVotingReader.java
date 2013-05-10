package mstparser.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import mstparser.DependencyInstance;
import mstparser.DependencyInstancesVoting;

/** 
 * Maria Mateva: 
 * this is a class for the input for the {@link DependencyInstancesVoting}
 * (obviously based on {@link MSTReader})
 * 
 * 
 * The expected format is as follows; \t is a separator for compatibility:
 * line1: N_parsers
 * line2: weight1 weight2 ... weight_Nparsers
 * line3: ID1 ID2 IDM of the parsers we choose for this experiment
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

	/** weights of all parsers **/
	private double [] weightsOfParsers;
	
	/** indexes of chosen parsers **/
	private int [] chosenParsers;
	
//	@Override
//	public DependencyInstance getNext() throws IOException {
//		
//		return null;
//	}
	
	public DependencyInstancesVoting getNextVotingGroup() throws IOException {
		int M = chosenParsers.length; // number of parsers in the group
		ArrayList<DependencyInstance> trees = new ArrayList<DependencyInstance>(M);
		for (int i = 0; i < M; i++) {
			trees.add(getNext());
		}
		
		return null;
	}
	
	@Override
	public boolean startReading(String file) throws IOException {
		labeled = fileContainsLabels(file);
		inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
		
		String line = inputReader.readLine();
		N = Integer.parseInt(line);
		line = inputReader.readLine();
		String [] parsersWeights = line.split("\\t");
		if (parsersWeights.length != N) {
			System.err.println("Wrong number of parsers' weights");
		}
		for (int i = 0; i < N; i++) {
			weightsOfParsers[i] = Double.parseDouble(parsersWeights[i]);
		}
		line = inputReader.readLine();
		
		String [] chosenParsers = line.split("\\t");
		return labeled;
	}

	@Override
	protected boolean fileContainsLabels(String filename) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(filename));
		for (int i = 0; i < 5; i++) {
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
