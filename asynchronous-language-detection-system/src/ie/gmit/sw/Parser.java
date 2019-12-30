package ie.gmit.sw;

import java.io.*;
import java.util.Map;
public class Parser implements Runnable{
	private Database db = null;
	private String file;
	private int k;
	
	public Parser(String file, int k) {
		super();
		this.file = file;
		this.k = k;
	}

	public void setDb(Database db) {
		this.db = db;
	}
	
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line = null;
			
			while((line = br.readLine()) != null) {
				String[] record = line.trim().split("@");
				if (record.length != 2) continue;
				parse(record[0], record[1]);
			}
			
			br.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parse(String text, String lang, int... ks) {
		Language language = Language.valueOf(lang);
		
		for (int i = 0; i < text.length() - k; i++) {
			CharSequence kmer = text.substring(i, i + k);
			db.add(kmer, language);
		}
	}
	
	private void analyseQuery(String s) {
		for (int i = 0; i < s.length() - k; i++) {
			CharSequence kmer = s.substring(i, i + k);
			add(kmer, language);
		}
	}
	
	public void add(CharSequence s) {
		int kmer = s.hashCode();
		Map<Integer, LanguageEntry> langDb = getLanguageEntries(lang);
		
		int frequency = 1;
		if (langDb.containsKey(kmer)) {
			frequency += langDb.get(kmer).getFrequency();
		}
		langDb.put(kmer, new LanguageEntry(kmer, frequency));
	}

	public static void main(String[] args) {
		Parser p = new Parser("wili-2018-Small-11750-Edited.txt", 1);
		
		Database db = new Database();
		p.setDb(db);
		Thread t = new Thread(p);
		t.start();
		
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		db.resize(300);
		
		String s = "Gabh isteach sa seomra folctha agus nigh tú féin";
		//String s = "Go into the bathroom and wash yourself";
		
		p.analyseQuery(s);
	}
}
