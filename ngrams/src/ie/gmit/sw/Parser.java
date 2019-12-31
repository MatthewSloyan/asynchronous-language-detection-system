package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Parser implements Runnable{
	private Map<Integer, LanguageEntry> queryMap = null;
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
			//BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			// Code adapted to get resource in web application: https://stackoverflow.com/questions/10978380/how-to-read-a-text-file-from-a-web-application
			BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(file)));
			String line = null;
			
			//Start a thread pool of size 2, and loop
			//ExecutorService es = Executors.newFixedThreadPool(5);
			
			while((line = br.readLine()) != null) {
//				String[] record = line.trim().split("@");
//				if (record.length != 2) 
//					continue;
//				
//				es.execute(new Processor(record));
				String[] record = line.trim().split("@");
				if (record.length != 2) 
					continue;
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
		
//		//Start a thread pool of size 2, and loop
//		ExecutorService es = Executors.newFixedThreadPool(2);
//		
//		for (int i = 0; i <= k; i++) {
//			es.execute(new Processor(text, language, i));
//		}
		
		//Start a single thread executor for ShingleTaker, and return fileMap
		//ExecutorService es1 = Executors.newSingleThreadExecutor();
		//Future<ConcurrentHashMap<Integer, List<Index>>> fileMap = es1.submit(new ShingleTaker(queue, files.length));
		
		// Will thread for speed
//		for (int i = 0; i <= k; i++) {
//			for (int j = 0; j < text.length() - i; j++) {
//				CharSequence kmer = text.substring(j, j + i);
//				db.add(kmer, language);
//			}
//		}
	}
	
	public String analyseQuery(String text) {
		try {
			queryMap = new HashMap<Integer, LanguageEntry>();
			
			System.out.println(text);
			
			for (int i = 0; i < text.length() - k; i++) {
				CharSequence kmer = text.substring(i, i + k);
				add(kmer);
			}
			
//			for (int i = 0; i <= k; i++) {
//				for (int j = 0; j < text.length() - i; j++) {
//					CharSequence kmer = text.substring(j, j + i);
//					System.out.println(kmer);
//					add(kmer);
//				}
//			}
			
			getTop(400);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Test: " + db.getLanguage(queryMap).toString());
		
		return db.getLanguage(queryMap).toString();
	}
	
	public void add(CharSequence s) {
		int kmer = s.hashCode();
		
		int frequency = 1;
		if (queryMap.containsKey(kmer)) {
			frequency += queryMap.get(kmer).getFrequency();
		}
		queryMap.put(kmer, new LanguageEntry(kmer, frequency));
	}
	
	public void getTop(int max) {
		Map<Integer, LanguageEntry> temp = new HashMap<>();
		List<LanguageEntry> les = new ArrayList<>(queryMap.values());
		Collections.sort(les);
		
		int rank = 1;
		for (LanguageEntry le : les) {
			le.setRank(rank);
			temp.put(le.getKmer(), le);			
			if (rank == max) 
				break;
			rank++;
			
			System.out.println(le.getFrequency());
		}
		
		queryMap = temp;
	}
	
	private class Processor implements Runnable {
		private String[] record;
		
		public Processor(String[] record) {
			super();
			this.record = record;
		}

		@Override
	    public void run() {
            try {
            	parse(record[0], record[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
	    }
	}
	
//	private class Processor implements Runnable {
//		private String text;
//		private Language language;
//		private int k;
//		
//		public Processor(String text, Language language, int k) {
//			super();
//			this.text = text;
//			this.language = language;
//			this.k = k;
//		}
//
//		@Override
//	    public void run() {
//            try {
//            	for (int i = 0; i < text.length() - k; i++) {
//            		CharSequence kmer = text.substring(i, i + k);
//    				db.add(kmer, language);
//    			}
//            	
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//	    }
//	}

//	public static void main(String[] args) {
//		//Parser.class.getClassLoader().getResourceAsStream("DBProperty.properties");
//		//URL url = Thread.currentThread().getContextClassLoader().getResource("wili-2018-Small-11750-Edited.txt");  
//		//Parser p = new Parser(url.toString(), 1);
//		
//		Parser p = new Parser("wili-2018-Small-11750-Edited.txt", 4);
//		
//		Database db = new Database();
//		p.setDb(db);
//		Thread t = new Thread(p);
//		t.start();
//		
//		try {
//			t.join();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		
//		db.resize(300);
//		
//		//String s = "'S e baile ann am Moireabh a tha ann an Inbhir Èireann (Beurla: Findhorn). Tha am baile suidhichte dìreach deas air Linne Mhoireibh. Tha Inbhir Èireann mu 3 mìltean iar-thuath air Cinn Lois agus mu 9 mìltean air falbh bho Fharrais. 'S e seo na co-chomharran aige: 57° 39′ 32.4″ Tuath agus 3° 36′ 39.6″ Iar.";
//		//String s = "Go into the bathroom and wash yourself, thank you! Good sir you are the best.";
//		String s = "Разположено е в полите на планината Плана и източния край на Самоковското поле. Също така се намира в близост до планините Витоша, Рила и Верила, като всяка една от тях може да бъде видяна от високите части на селото.";
//		
//		p.analyseQuery(s);
//	}
}
