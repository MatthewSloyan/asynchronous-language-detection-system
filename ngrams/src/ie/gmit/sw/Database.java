package ie.gmit.sw;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class Database implements Databaseator{
	
	private static Database instance = new Database();
	private static ConcurrentMap<Language, Map<Integer, LanguageEntry>> db = new ConcurrentSkipListMap<>();

	private Database() {}
    
    public static Database getInstance() {
        return instance;
    }
	
	public void add(String lang, int kmer) {
		Language language = Language.valueOf(lang);
		Map<Integer, LanguageEntry> langDb = getLanguageEntries(language);
		
		langDb.put(kmer, new LanguageEntry(kmer, new Utilities().addToFrequency(langDb, kmer)));
		db.put(language, langDb);
	}

	private Map<Integer, LanguageEntry> getLanguageEntries(Language lang){
		Map<Integer, LanguageEntry> langDb = null; 
		if (db.containsKey(lang)) {
			langDb = db.get(lang);
		}else {
			langDb = new ConcurrentSkipListMap<Integer, LanguageEntry>();
			db.put(lang, langDb);
		}
		return langDb;
	}
	
	public void resize(int max) {
		Set<Language> keys = db.keySet();
		for (Language lang : keys) {
			Map<Integer, LanguageEntry> top = getTop(max, lang);
			db.put(lang, top);
		}
	}
	
	private Map<Integer, LanguageEntry> getTop(int max, Language lang) {
		
		List<LanguageEntry> les = new ArrayList<>(db.get(lang).values());
		Collections.sort(les);
		
		Map<Integer, LanguageEntry> temp = new Utilities().scaleByRank(les, max);
		
		return temp;
	}
	
	public Language getLanguage(Map<Integer, LanguageEntry> query) {
		ConcurrentSkipListSet<OutOfPlaceMetric> oopm = new ConcurrentSkipListSet<>();
		
		Set<Language> langs = db.keySet();
		for (Language lang : langs) {
			oopm.add(new OutOfPlaceMetric(lang, getOutOfPlaceDistance(query, db.get(lang))));
		}
		return oopm.first().getLanguage();
	}
	
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

//	@Override
//	public String toString() {
//		
//		StringBuilder sb = new StringBuilder();
//		
//		int langCount = 0;
//		int kmerCount = 0;
//		Set<Language> keys = db.keySet();
//		for (Language lang : keys) {
//			langCount++;
//			sb.append(lang.name() + "->\n");
//			 
//			 Collection<LanguageEntry> m = new ConcurrentSkipListSet<>(db.get(lang).values());
//			 kmerCount += m.size();
//			 for (LanguageEntry le : m) {
//				 sb.append("\t" + le + "\n");
//			 }
//		}
//		sb.append(kmerCount + " total k-mers in " + langCount + " languages"); 
//		return sb.toString();
//	}
}