package ie.gmit.sw;

public class Kmer {
	private int kmer;
	private Language language;
	
	public Kmer(int kmer, Language language) {
		super();
		this.kmer = kmer;
		this.language = language;
	}

	public Language getLanguage() {
		return language;
	}

	public int getKmer() {
		return kmer;
	}
}
