package ie.gmit.sw;

public class Kmer {
	private int kmer;
	private String language;
	
	public Kmer(int kmer, String language) {
		super();
		this.kmer = kmer;
		this.language = language;
	}

	public int getKmer() {
		return kmer;
	}

	public String getLanguage() {
		return language;
	}
}
