package mstparser.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import mstparser.DependencyInstance;

public class MSTVotingReader extends DependencyReader {

	@Override
	public DependencyInstance getNext() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean fileContainsLabels(String filename) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(filename));
		in.readLine();
		in.readLine();
		in.readLine();
		String line = in.readLine();
		in.close();

		if (line.trim().length() > 0) {
			return true;
		} else {
			return false;
		}
	}

}
