package ie.gmit.sw;

/**
* Supplied LanguageEntry class that is added to the db.
* Contains all hashcode values for speed and low space complexity rather than using Strings.
* 
* @author John Healy (Modified by Matthew Sloyan)
*/
public class LanguageEntry implements Comparable<LanguageEntry> {
	private int kmer;
	private int frequency;
	private int rank;

	/**
	* Constructor for class
	* 
	* @param kmer hashcode of the kmer
	* @param frequency is the occurrence of the kmer
	*/
	public LanguageEntry(int kmer, int frequency) {
		super();
		this.kmer = kmer;
		this.frequency = frequency;
	}

	public int getKmer() {
		return kmer;
	}

	public void setKmer(int kmer) {
		this.kmer = kmer;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	/**
	* Objects will be sorted by frequency in decending order. Used when getting the top 300 values of the Database.
	*/
	@Override
	public int compareTo(LanguageEntry next) {
		// Decending order
		return - Integer.compare(frequency, next.getFrequency());
	}
}