///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2007 University of Texas at Austin and (C) 2005
// University of Pennsylvania and Copyright (C) 2002, 2003 University
// of Massachusetts Amherst, Department of Computer Science.
//
// This software is licensed under the terms of the Common Public
// License, Version 1.0 or (at your option) any subsequent version.
// 
// The license is approved by the Open Source Initiative, and is
// available from their website at http://www.opensource.org.
///////////////////////////////////////////////////////////////////////////////

package mstparser.io;

import mstparser.DependencyInstance;
import mstparser.Util;
import java.io.*;

/**
 * A reader for files in MST format.
 * 
 * <p>
 * Created: Sat Nov 10 15:25:10 2001
 * </p>
 * 
 * @author Jason Baldridge
 * @version $Id: MSTReader.java 94 2007-01-17 17:05:12Z jasonbaldridge $
 * @see mstparser.io.DependencyReader
 */
public class MSTReader extends DependencyReader {
	
	boolean weighted;
	public MSTReader(boolean weighted){
		super();
		this.weighted = weighted;
	}
	
	public DependencyInstance getNext() throws IOException {
		
		String line = inputReader.readLine();
		String posLine = inputReader.readLine();
		String deprelLine = labeled ? inputReader.readLine() : posLine;
		String headsLine = inputReader.readLine();
		String weightsLine = weighted ? inputReader.readLine() : null;
		
		inputReader.readLine(); // blank line

		if (line == null) {
			inputReader.close();
			return null;
		}

		String[] forms = line.split("\t");
		String[] pos = posLine.split("\t");
		String[] deprels = deprelLine.split("\t");
		String[] weights = weighted ? weightsLine.split("\t") : null;
		int[] heads = Util.stringsToInts(headsLine.split("\t"));

		String[] formsNew = new String[forms.length + 1];
		String[] posNew = new String[pos.length + 1];
		String[] deprelsNew = new String[deprels.length + 1];
		double[] weightsNew = weighted ? new double[weights.length + 1] : null;
		int[] headsNew = new int[heads.length + 1];

		// Shift to the right to add the ROOT as id = 0
		formsNew[0] = "<root>";
		posNew[0] = "<root-POS>";
		deprelsNew[0] = "<no-type>";
		headsNew[0] = -1;
		if (weighted) {
			weightsNew[0] = 0;
		}
		for (int i = 0; i < forms.length; i++) {
			formsNew[i + 1] = normalize(forms[i]);
			posNew[i + 1] = pos[i];
			deprelsNew[i + 1] = labeled ? deprels[i] : "<no-type>";
			headsNew[i + 1] = heads[i];
			if (weighted) {
				weightsNew[i + 1] = Double.parseDouble(weights[i]);
			}
		}

		DependencyInstance instance = new DependencyInstance(formsNew,
				posNew, deprelsNew, headsNew, weightsNew);

		// set up the course pos tags as just the first letter of the
		// fine-grained ones
		String[] cpostags = new String[posNew.length];
		cpostags[0] = "<root-CPOS>";
		for (int i = 1; i < posNew.length; i++) {
			cpostags[i] = posNew[i].substring(0, 1);
		}
		instance.cpostags = cpostags;

		// set up the lemmas as just the first 5 characters of the forms
		String[] lemmas = new String[formsNew.length];
		cpostags[0] = "<root-LEMMA>";
		for (int i = 1; i < formsNew.length; i++) {
			int formLength = formsNew[i].length();
			lemmas[i] = formLength > 5 ? formsNew[i].substring(0, 5) : formsNew[i];
		}
		instance.lemmas = lemmas;
		instance.feats = new String[0][0];

		return instance;
	}

	protected boolean fileContainsLabels(String file) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(file));
		in.readLine();
		in.readLine();
		in.readLine();
		String line = in.readLine();
		in.close();

		if (line!= null && line.trim().length() > 0) {
			return true;
		} else {
			return false;
		}
	}

}
