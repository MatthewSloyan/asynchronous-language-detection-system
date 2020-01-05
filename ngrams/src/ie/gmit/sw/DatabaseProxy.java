package ie.gmit.sw;

import java.util.Map;

/**
* I have decided to implement a proxy to access the database.
* This allows the developer to control the container service without the client knowing as the proxy is implemented as a layer of encapsulation in front of it. 
* The proxy works even if database isn’t ready or is not available.
* Open/Close Principle (OCP). You can introduce new proxies without changing the real database or client.
* You can also make changes to the database or add new databases without affecting the proxy or the client.

* To achieve this I have composed the real database as an instance variable, 
* and then delegate the calls to the real database which will stores the kmers.
* When the constructor is called, it gets the only instance of the Database so data is consistent.
* 
* @see Databaseable
* @see Database
* @author Matthew Sloyan
*/

public class DatabaseProxy implements Databaseable{
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
