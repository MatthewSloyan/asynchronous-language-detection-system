package ie.gmit.sw;

/**
* FileProcessor class that processes each line in the WiliText file.
* However it could be used to parse any number of different types of files E.g HTML pages.
* Also implements Processable which could be used again in different types of parsers.
* 
* @see Processable
* @author Matthew Sloyan
*/
public class FileProcessor implements Runnable, Processable {
	private String[] record;
	private int k;
	
	/**
	* Constructor for class
	* 
	* @param record the line in the file
	* @param k the max number of Kmers
	*/
	public FileProcessor(String[] record, int k) {
		super();
		this.record = record;
		this.k = k;
	}

	/**
	* Runnable thread that is spawned from a Thread Pool.
	* The line is already split into the text and the language name at the @ delimiter.
	* This class processes a single line and adds all the possible Kmers (1-4) to the database.
	* 
	* I did at one stage have this working with a queue where each Kmer was being added to a queue here, 
	* but as previously mentioned it added complexity and slowed down the processing considerably. 
	* 
	* Running time: O(N^2) as getKmers() contains a loop & getKmer() contains an inner loop.
	* T(n) = 2^n + 2
	* 
	* @see DatabaseProxy
	*/
	@Override
    public void run() {
        try {
        	getKmers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	/**
	* The outer loop controls the current kmer number, so when i is 1 it is adding kmers of length 1 to the database.
	*/
	public void getKmers() {
		for (int i = 1; i <= k; i++) {
			getKmer(i);
		}
	}

	/**
	* The loop adds each specific kmer with it's length determined above to the database.
	* So if i is 2, it gets every kmer length 2 in the string one after the other.
	* By getting a substring the Kmer is simply retrieved and passed through the proxy to the Database.
	* CharSequence is also used as it uses less memory than a String.
	* 
	* I initially had it where it would tile across the string for all kmers 1-4, which would include more kmers
	* but it was quite slow and the results weren't as accurate.
	*/
	public void getKmer(int i) {
		for (int j = 0; j < record[0].length() - i; j+=i) {
			CharSequence kmer = record[0].substring(j, j + i);
			new DatabaseProxy().add(record[1], kmer.hashCode());
		}
	}
}
