package ie.gmit.sw;

import java.util.Map;

/**
* Interface designed to abstract common functionality from Database class, 
* and allow it to work with the Database Proxy.
* Also it could allow multiple different types of databases to be created.
* 
* @see Database
* @see DatabaseProxy
* @author Matthew Sloyan
*/
public interface Databaseable {
	public void add(String lang, int kmer);
	public void resize(int max);
	public Language getLanguage(Map<Integer, LanguageEntry> query);
}
