package mstparser;

import java.io.*;

/**
 * Unlabeled Accuracy: accuracy of words, unlabeled
 * Unlabeled Complete Correct: accuracy of sentences, unlabeled
 * Labeled Accuracy: accuracy of words, labeled
 * Labeled Complete Correct: accuracy of sentences, labeled
 *
 *
 */

public class DependencyEvaluator {

	public static void evaluate(String actual_file, String predicted_file)
			throws IOException {
		boolean labeled = false;
		BufferedReader actual_in = new BufferedReader(new FileReader(actual_file));
		actual_in.readLine();
		actual_in.readLine();
		actual_in.readLine();
		String l = actual_in.readLine();
		if (l.trim().length() > 0)
			labeled = true;

		int total = 0;
		int correct = 0;
		int correctL = 0;
		int numsent = 0;
		int corrsent = 0;
		int corrsentLabel = 0;

		actual_in = new BufferedReader(new FileReader(actual_file));
		BufferedReader predicted_in = new BufferedReader(new FileReader(predicted_file));

		actual_in.readLine();
		//String[] pos = act_in.readLine().split("\t");
		predicted_in.readLine();
		predicted_in.readLine();
		String actual_lab = labeled ? actual_in.readLine().trim() : "";
		String actual_dep = actual_in.readLine().trim();
		String predicted_lab = labeled ? predicted_in.readLine().trim() : "";
		String predicted_dep = predicted_in.readLine().trim();
		actual_in.readLine();
		predicted_in.readLine();

		while (actual_dep != null) {

			String[] actual_labels = null;
			String[] predicted_labels = null;
			if (labeled) {
				actual_labels = actual_lab.split("\t");
				predicted_labels = predicted_lab.split("\t");
			}
			String[] actual_deps = actual_dep.split("\t");
			String[] predicted_deps = predicted_dep.split("\t");
			if (actual_deps.length != predicted_deps.length)
				System.out.println("Lengths do not match");

			boolean whole = true;
			boolean wholeLabel = true;

			for (int i = 0; i < actual_deps.length; i++) {
				if (predicted_deps[i].equals(actual_deps[i])) {
					correct++;
					if (labeled) {
						if (actual_labels[i].equals(predicted_labels[i]))
							correctL++;
						else
							wholeLabel = false;
					}
				} else {
					whole = false;
					wholeLabel = false;
				}
			}
			total += actual_deps.length;

			if (whole)
				corrsent++;
			if (wholeLabel)
				corrsentLabel++;
			numsent++;

			actual_in.readLine();
//			try {
//				pos = act_in.readLine().split("\t");
//			} catch (Exception e) {
//			}
			predicted_in.readLine();
			predicted_in.readLine();
			actual_lab = labeled ? actual_in.readLine() : "";
			actual_dep = actual_in.readLine();
			predicted_lab = labeled ? predicted_in.readLine() : "";
			predicted_dep = predicted_in.readLine();
			actual_in.readLine();
			predicted_in.readLine();
		}

		System.out.println("Tokens: " + total);
		System.out.println("Correct: " + correct);
		System.out.println("Unlabeled Accuracy: " + ((double) correct / total));
		System.out.println("Unlabeled Complete Correct: "
				+ ((double) corrsent / numsent));
		if (labeled) {
			System.out.println("Labeled Accuracy: " + ((double) correctL / total));
			System.out.println("Labeled Complete Correct: "
					+ ((double) corrsentLabel / numsent));
		}

	}
}
