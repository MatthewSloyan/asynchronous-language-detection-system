package ie.gmit.sw;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ConcurrentHashMap;

/**
* This is the implementation of the real database.
* I decided there should only be one instance of this, as then it can be used across any number of servlets once it's set up.
* This database could be placed on a server (VM) easily, and implements Databaseable which could be used with other types of databases.
* To achieved this I implemented a Singleton design pattern. This also the class composed in the DatabaseProxy, so that the client
* will not be able to access it directly.
* 
* I have used a ConcurrentHashMap for the local db as it's the fastest type (search, insert and delete functions are O(1)).
* Also it allows for concurrency between multiple threads as an Thread Pool is used to add Kmers.
* 
* @see Databaseable
* @author Matthew Sloyan (Methods supplied by John Healy)
*/
class Database implements Databaseable{
	
	private static Database instance = null;
	private static Map<Language, Map<Integer, LanguageEntry>> db = new ConcurrentHashMap<>();

	private Database() {}
	
	/**
	* As Database is accessed in a threaded manner, it can affect the Singleton nature as multiple threads might access it together.
	* Code adapted from to implemented a double checking lock which reduces overhead. 
	* https://www.geeksforgeeks.org/java-singleton-design-pattern-practices-examples/
	* 
	* @return Database
	*/
    public static Database getInstance() {
    	if (instance == null)  
        { 
          synchronized (Database.class) 
          { 
            if(instance==null) 
            { 
              instance = new Database(); 
            } 
          } 
        } 
        return instance;
    }
	
    /**
	* Adds kmer to local ConcurrentHashMap with the updated frequency.
	* Calls Utilities class to add to frequency which is also used when parsing the user input query in the client. (DRY)
	* Running time: O(1) as HashMap's search, insert and delete functions are O(1)
	* 
	* @param lang String name of language which saves space rather than passing whole object.
	* @param kmer hashcode of kmer
	*
	* @see LanguageEntry
	* @see Utilities
	*/
	public void add(String lang, int kmer) {
		Language language = Language.valueOf(lang);
		Map<Integer, LanguageEntry> langDb = getLanguageEntries(language);
		
		langDb.put(kmer, new LanguageEntry(kmer, new Utilities().addToFrequency(langDb, kmer)));
	}
	
    /**
	* Gets the inner Map in db if exists already. If the language doesn't exist then it adds a new map.
	* Running time: O(1) as HashMap's search, insert and delete functions are O(1)
	* 
	* @param lang Language object to find in db.
	* 
	* @return langDb inner map of db.
	*/
	private Map<Integer, LanguageEntry> getLanguageEntries(Language lang){
		Map<Integer, LanguageEntry> langDb = null; 
		if (db.containsKey(lang)) {
			langDb = db.get(lang);
		}else {
			langDb = new ConcurrentHashMap<Integer, LanguageEntry>();
			db.put(lang, langDb);
		}
		return langDb;
	}
	
	/**
	* Removes all but the languages with the highest frequency. Max determines how many to keep.
	* Loop through all languages and get the top kmers for each language, then overwrite the db to update it.
	* Running time: O(N)
	* 
	* @param max number of kmers records to keep in each language entry.
	*/
	public void resize(int max) {
		Set<Language> keys = db.keySet();
		for (Language lang : keys) {
			Map<Integer, LanguageEntry> top = getTop(max, lang);
			db.put(lang, top);
		}
	}
	
	/**
	* Get the top kmers in the Map based on the highest frequency by sorting. This is called in resize.
	* Then call Utilities class to rank the top kmers which is also used when parsing the user input query in the client. (DRY)
	* Running time: O(N) because scaleByRank() contains a loop.
	* However sort is O(n log(n))
	* 
	* @param max number of kmers records to keep in each language entry.
	* @param lang Language object to find in db.
	* @return temp temporary Map which is overwrites the db in resize()
	*
	* @see Utilities
	*/
	private Map<Integer, LanguageEntry> getTop(int max, Language lang) {
		
		List<LanguageEntry> les = new ArrayList<>(db.get(lang).values());
		Collections.sort(les);
		
		Map<Integer, LanguageEntry> temp = new Utilities().scaleByRank(les, max);
		
		return temp;
	}
	
	/**
	* Used to predict the correct language. I wanted to abstract this to the PredictLanguage class 
	* but then a getDb would be needed and that would break the encapsulation of the local ConcurrentHashMap.
	* 
	* Loop through all the languages in the db and add OutOfPlaceMetric result to the new ConcurrentSkipListSet which is ordered, 
	* and is safe for threading. Then return the top result which is the predicted language.
	* Running time: O(N^2) because getOutOfPlaceDistance() contains an inner loop.
	* 
	* @param query - Map sent from the PredictLanguage class.
	* @return Language - object which is used to display predicted result to client.
	*/
	public Language getLanguage(Map<Integer, LanguageEntry> query) {
		ConcurrentSkipListSet<OutOfPlaceMetric> oopm = new ConcurrentSkipListSet<>();
		
		Set<Language> langs = db.keySet();
		for (Language lang : langs) {
			oopm.add(new OutOfPlaceMetric(lang, getOutOfPlaceDistance(query, db.get(lang))));
		}
		return oopm.first().getLanguage();
	}
	
	/**
	* Used to get the getOutOfPlaceDistance value to be added to the new instance of OutOfPlaceMetric in getLanguage()
	* but then a getDb would be needed and that would break the encapsulation of the local ConcurrentHashMap.
	* 
	* Loop through all the Kmers in the query map. If it's not in the db's language then set to 301.
	* If it's in db take the query rank from the db rank and add to overall distance.
	* 
	* @param query - User Map sent from the PredictLanguage class.
	* @param subject - Language Map from the db.
	* @return distance - distance value for each kmer in one language compared to the query Map.
	*/
	private int getOutOfPlaceDistance(Map<Integer, LanguageEntry> query, Map<Integer, LanguageEntry> subject) {
		int distance = 0;
		
		ConcurrentSkipListSet<LanguageEntry> les = new ConcurrentSkipListSet<>(query.values());		
		for (LanguageEntry q : les) {
			LanguageEntry s = subject.get(q.getKmer());
			if (s == null) {
				distance += subject.size() + 1;
			}else {
				distance += s.getRank() - q.getRank();
			}
		}
		return distance;
	}
}