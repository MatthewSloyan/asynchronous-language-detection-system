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
	
	/**
	* Tested method to use instead of hashing kmers when processing. The reason was to eliminate possible hash collisions.
	* There is some downsides however, as longs use 8 bytes compared to 4 bytes used in a int the space complexity was increased.
	* Also it made the overall process about %25 slower and the results weren't as accurate from multiple tests (surprisingly).
	* So I have decided to not implement it in this build.
	* Running time: O(N)
	* 
	* @param s CharSequence of passed in kmer.
	* @return sequence encoded kmer
	* 
	* @author John Healy
	*/
	protected long encode(CharSequence s) throws Exception {
		if(s.length() < 1 || s.length() > 4) throw new Exception("Can only encode n-grams with 1-4 characters."); 
		
		long sequence = 0x0000000000000000L; //Set of 64 bits to zero
		for (int i = 0; i < s.length(); i++) {
			sequence <<=16; //Shift bits left by 16 bits
			sequence |= s.charAt(i); //Bitwise OR. Sets the first (rigth-most) bits to the Unicode binary value.
		}
		return sequence; //return chars encoded as a long.
	}
}
