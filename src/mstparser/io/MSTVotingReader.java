package mstparser.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import mstparser.DependencyInstance;
import mstparser.DependencyInstancesVoting;

/** 
 * Maria Mateva: 
 * this is a class for the input for the {@link DependencyInstancesVoting}
 * (obviously based on {@link MSTReader})
 * 
 * 
 * The expected format is:
 * line1: N_parsers
 * line2: weight1 weight2 ... weight_Nparsers
 * line3: empty
 * line4: sentence words
 * line5: pos tags
 * line6: labels/heads
 * line7: heads or empty(if in unlabeled mode)
 * line8: empty or next dependency(if in unlabeled mode) 
 **/
public class MSTVotingReader extends DependencyReader {
	
	/** number of total parsers that we choose **/
	private int N; 
	
	/** indiceOfChosenParsers **/
	private int [] chosenParsers;
	
	@Override
	public DependencyInstance getNext() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public DependencyInstancesVoting getNextVotingGroup() {
		return null;
	}
	
	@Override
	public boolean startReading(String file) throws IOException {
		labeled = fileContainsLabels(file);
		inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
		
		String line = inputReader.readLine();
		N = Integer.parseInt(line);
				
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
