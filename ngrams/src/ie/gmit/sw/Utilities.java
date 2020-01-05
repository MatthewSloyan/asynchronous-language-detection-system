package ie.gmit.sw;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* Utilities methods which are abstracted and implemented from both the PredictLanguage and FileParser class.
* 
* @see FileParser
* @see PredictLanguage
* @author Matthew Sloyan
*/
public class Utilities {
	
	/**
	* Gets the frequency from the map passed in.
	* Running time: O(1)
	* 
	* @param langDb Map passed in to get frequency.
	* @param kmer hashcode of kmer.
	* @return frequency number of kmers.
	*
	* @see JobProducer
	* @see JobProcessor
	*/
	protected int addToFrequency(Map<Integer, LanguageEntry> langDb, int kmer) {
		int frequency = 1;
		
		if (langDb.containsKey(kmer)) {
			frequency += langDb.get(kmer).getFrequency();
		}
		return frequency;
	}
	
	/**
	* Ranks the sorted list numerically and cuts the list to the max value.
	* Running time: O(N)
	* 
	* @param lEntry List of Kmers.
	* @param max max value.
	* @return temp Temporary map which is ranked and shortened.
	*
	* @see JobProducer
	* @see JobProcessor
	*/
	protected Map<Integer, LanguageEntry> scaleByRank(List<LanguageEntry> lEntry, int max) {
		Map<Integer, LanguageEntry> temp = new HashMap<>();
		int rank = 1;
		
		for (LanguageEntry entry : lEntry) {
			entry.setRank(rank);
			temp.put(entry.getKmer(), entry);			
			if (rank == max) 
				break;
			
			rank++;
		}
		
		return temp;
	}
}
