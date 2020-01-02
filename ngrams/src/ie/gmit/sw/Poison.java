package ie.gmit.sw;

/**
* Poison class - used to signify the end of the Wili file so the threads can be stopped.
*
* @author Matthew Sloyan
*/
public class Poison extends Kmer{

	/**
	* Constructor for class
	* 
	* @param language name 
	* @param kmer as a hashcode
	*/
	public Poison(int kmer, String language) {
		super(kmer, language);
		System.out.println("Poison");
	}
}

