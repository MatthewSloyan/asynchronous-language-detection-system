package ie.gmit.sw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PredictLanguage implements Processable {
	private Map<Integer, LanguageEntry> queryMap = null;
	private String query;
	private int k;
	
	public PredictLanguage(String query, int k) {
		super();
		this.query = query;
		this.k = k;
	}

	public String analyseQuery() {
		try {
			queryMap = new HashMap<Integer, LanguageEntry>();
			
			getKmers();
			
			getTop(400);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new DatabaseProxy().getLanguage(queryMap).toString();
	}
	
	public void getKmers() {
		for (int i = 1; i <= k; i++) {
			getKmer(i);
		}
	}

	public void getKmer(int i) {
		for (int j = 0; j < query.length() - i; j+=i) {
			CharSequence kmer = query.substring(j, j + i);
			add(kmer.hashCode());
		}
	}
	
	private void add(int kmer) {
		queryMap.put(kmer, new LanguageEntry(kmer, new Utilities().addToFrequency(queryMap, kmer)));
	}
	
	private void getTop(int max) {
		List<LanguageEntry> les = new ArrayList<>(queryMap.values());
		Collections.sort(les);
		
		Map<Integer, LanguageEntry> temp = new Utilities().scaleByRank(les, max);
		
		queryMap = temp;
	}
}
