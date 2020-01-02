package ie.gmit.sw;

import java.util.Map;

public interface Databaseator {
	public void add(String lang, int kmer);
	public void resize(int max);
	public Language getLanguage(Map<Integer, LanguageEntry> query);
}
