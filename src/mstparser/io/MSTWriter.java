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
 * A writer to create files in MST format.
 * 
 * <p>
 * Created: Sat Nov 10 15:25:10 2001
 * </p>
 * 
 * @author Jason Baldridge
 * @version $Id: MSTWriter.java 94 2007-01-17 17:05:12Z jasonbaldridge $
 * @see mstparser.io.DependencyWriter
 */
public class MSTWriter extends DependencyWriter {

	public MSTWriter(boolean labeled) {
		this.labeled = labeled;
	}

	public void write(DependencyInstance instance) throws IOException {
		writer.write(Util.join(instance.forms, '\t') + "\n");
		writer.write(Util.join(instance.postags, '\t') + "\n");
		if (labeled) {
			writer.write(Util.join(instance.deprels, '\t') + "\n");
		}
		writer.write(Util.join(instance.heads, '\t') + "\n\n");
	}
	
	/**
	 * Removing initial <root>, <postag>, etc.
	 * @param instance
	 * @throws IOException
	 */
	public void writeRaw(DependencyInstance instance) throws IOException {
		int newLen = instance.forms.length - 1;
		String[] forms = new String[newLen];
		String[] postags = new String[newLen];
		String[] deprels = new String[newLen];
		int[] heads = new int[newLen];
		
		for (int i = 0; i < newLen; i++) {
			forms[i] = instance.forms[i + 1];
			postags[i] = instance.postags[i + 1];
			if (labeled) {
				deprels[i] = instance.deprels[i + 1];
			}
			heads[i] = instance.heads[i + 1];
		}
		writer.write(Util.join(forms, '\t') + "\n");
		writer.write(Util.join(postags, '\t') + "\n");
		if (labeled)
			writer.write(Util.join(deprels, '\t') + "\n");
		writer.write(Util.join(heads, '\t') + "\n\n");
	}
	

}
