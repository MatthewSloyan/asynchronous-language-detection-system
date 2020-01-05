package ie.gmit.sw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PredictLanguage implements Processable {
	private Map<Integer, LanguageEntry> queryMap = null;
	private String query;
	private int k;
	
	public PredictLanguage(String query, int k) {
		super();
		this.query = query;
		this.k = k;
	}

	/**
	* Creates a query map and passes to the Database to get results.
	* I have done this procedurally rather than using threading as seen in the Parser as all queries are cut 
	* to the first 400 characters which is all that is required to determine the language.
	* This is done at the Servlet level so no user slows the system by adding a massive block of text.
	* 
	* I have used a HashMap as it's the fastest type (search, insert and delete functions are O(1)).
	* 
	* @return String predicted results from Database through Proxy.
	*/
	public String analyseQuery() {
		try {
			// Set the local map to a new instance.
			queryMap = new HashMap<Integer, LanguageEntry>();
			
			// Process the query and add to the map.
			getKmers();
			
			// Shorten the map and rank values.
			getTop(400);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Pass map to Proxy to get the predicted result and return to client.
		return new DatabaseProxy().getLanguage(queryMap).toString();
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
	* By getting a substring the Kmer is simply retrieved and passed through the proxy to the Database.
	* CharSequence is also used as it uses less memory than a String.
	*/
	public void getKmer(int i) {
		for (int j = 0; j < query.length() - i; j+=i) {
			CharSequence kmer = query.substring(j, j + i);
			add(kmer.hashCode());
		}
	}
	
	/**
	* Adds kmer to local HashMap with the updated frequency.
	* Calls Utilities class to add to frequency which is also used when parsing the Wili file in the Database. (DRY)
	* Running time: O(1) as HashMap's search, insert and delete functions are O(1)
	* 
	* I wanted to implement the two methods using the Databaseable interface but it was adding unnecessary complexity 
	* as the parameters required are different.
	* 
	* @param kmer hashcode of kmer
	*
	* @see LanguageEntry
	* @see Utilities
	*/
	private void add(int kmer) {
		queryMap.put(kmer, new LanguageEntry(kmer, new Utilities().addToFrequency(queryMap, kmer)));
	}
	
	/**
	* Get the top kmers in the Map based on the highest frequency by sorting.
	* Then call Utilities class to rank the top kmers which is also used in the Database. (DRY)
	* Running time: O(N) because scaleByRank() contains a loop.
	* However sort is O(n log(n))
	* 
	* @param max number of kmers records to keep in each language entry.
	* @return temp temporary Map which is overwrites the local map.
	*
	* @see Utilities
	*/
	private void getTop(int max) {
		List<LanguageEntry> les = new ArrayList<>(queryMap.values());
		Collections.sort(les);
		
		Map<Integer, LanguageEntry> temp = new Utilities().scaleByRank(les, max);
		
		queryMap = temp;
	}
}
