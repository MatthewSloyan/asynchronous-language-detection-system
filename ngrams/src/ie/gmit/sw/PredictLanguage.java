package ie.gmit.sw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PredictLanguage {
	private Map<Integer, LanguageEntry> queryMap = null;
	
	public String analyseQuery(String text) {
		try {
			queryMap = new HashMap<Integer, LanguageEntry>();
			int k = 4;
			
			System.out.println(text);
			
//			for (int i = 0; i < text.length() - k; i++) {
//				CharSequence kmer = text.substring(i, i + k);
//				add(kmer);
//			}
			
//			for (int i = 0; i <= k; i++) {
//				for (int j = 0; j < text.length() - i; j++) {
//					CharSequence kmer = text.substring(j, j + i);
//					System.out.println(kmer);
//					add(kmer);
//				}
//			}
			
			for (int i = 1; i <= k; i++) {
				for (int j = 0; j < text.length() - i; j+=i) {
					CharSequence kmer = text.substring(j, j + i);
					add(kmer);
				}
			}
			
			getTop(400);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Test: " + Database.getInstance().getLanguage(queryMap).toString());
		
		return Database.getInstance().getLanguage(queryMap).toString();
	}
	
	public void add(CharSequence s) {
		int kmer = s.hashCode();
		
		int frequency = 1;
		if (queryMap.containsKey(kmer)) {
			frequency += queryMap.get(kmer).getFrequency();
		}
		queryMap.put(kmer, new LanguageEntry(kmer, frequency));
	}
	
	public void getTop(int max) {
		Map<Integer, LanguageEntry> temp = new HashMap<>();
		List<LanguageEntry> les = new ArrayList<>(queryMap.values());
		Collections.sort(les);
		
		int rank = 1;
		for (LanguageEntry le : les) {
			le.setRank(rank);
			temp.put(le.getKmer(), le);			
			if (rank == max) 
				break;
			rank++;
		}
		
		queryMap = temp;
	}
}
