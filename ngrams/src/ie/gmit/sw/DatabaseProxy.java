package ie.gmit.sw;

import java.util.Map;

public class DatabaseProxy implements Databaseator{
	private Database database;
	
	public DatabaseProxy() {
		super();
		this.database = Database.getInstance();
	}

	@Override
	public void add(String lang, int kmer) {
		this.database.add(lang, kmer);
	}

	@Override
	public void resize(int max) {
		this.database.resize(max);
	}

	@Override
	public Language getLanguage(Map<Integer, LanguageEntry> query) {
		return this.database.getLanguage(query);
	}
}
