package voting;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Maria's simple stats on BTB
 * 
 */
public class BTBStat {
	private static final String DELIM_A = "->";
	private static final String DELIM_B = "###";
	private static final String DELIM_C = "=";
	
	/**
	 * File of statistics on BTB. 
	 * The format being 
	 * R->Nc###prepcomp=19946
	 */
	private static String statLabFile = "stat_rel.txt";
	private static String statUnlFile = "stat_pos.txt";
	
	private static Map<String, Integer> statLabeled = new HashMap<String, Integer>();
	private static Map<String, Integer> statUnlabeled = new HashMap<String, Integer>();
	
	static {
		try {
			BufferedReader brPos = new BufferedReader(new FileReader(statUnlFile));
			BufferedReader brRel = new BufferedReader(new FileReader(statLabFile));
			String line = "";
			while ((line = brPos.readLine()) != null) {
				String [] parts = line.split(DELIM_C);
				statUnlabeled.put(parts[0], Integer.parseInt(parts[1]));
			}
			while ((line = brRel.readLine()) != null) {
				String [] parts = line.split(DELIM_C);
				statLabeled.put(parts[0], Integer.parseInt(parts[1]));
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		// load the relations stats
	}
	
	public int getLabeledEdgeCount(String headPOS, String endPOS, String label) {
		String key = headPOS + DELIM_A + endPOS + DELIM_B + label;
		Integer res = statLabeled.get(key);
		if (res != null) {
			return res;
		} else {
			return 0;
		}
	}
	
	public int getUnlabeledEdgeCount(String headPOS, String endPOS) {
		String key = headPOS + DELIM_A + endPOS;
		Integer res = statUnlabeled.get(key);
		if (res != null) {
			return res;
		} else {
			return 0;
		}
	}

}
