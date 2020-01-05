package ie.gmit.sw;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class Utilities {
	
	protected int addToFrequency(Map<Integer, LanguageEntry> langDb, int kmer) {
		int frequency = 1;
		
		if (langDb.containsKey(kmer)) {
			frequency += langDb.get(kmer).getFrequency();
		}
		return frequency;
	}
	
	protected Map<Integer, LanguageEntry> scaleByRank(List<LanguageEntry> les, int max) {
		Map<Integer, LanguageEntry> temp = new HashMap<>();
		int rank = 1;
		for (LanguageEntry le : les) {
			le.setRank(rank);
			temp.put(le.getKmer(), le);			
			if (rank == max) break;
			rank++;
		}
		
		return temp;
	}
}
