package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Parser implements Runnable{
	private String file;
	private int k;

	BlockingQueue <Kmer> queue = new ArrayBlockingQueue<>(10);
	
	public Parser(String file, int k) {
		super();
		this.file = file;
		this.k = k;
	}
	
	public void run() {
		try {
			//BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			// Code adapted to get resource in web application: https://stackoverflow.com/questions/10978380/how-to-read-a-text-file-from-a-web-application
			BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(file)));
			String line = null;
			String[] record = null;
			
			//Start a single thread executor for QueryFileParser, and return queryMap
			ExecutorService es1 = Executors.newSingleThreadExecutor();
			es1.submit(new KmerProcessor(queue));
			
			//Start a thread pool of size 2, and loop
			ExecutorService es = Executors.newFixedThreadPool(10);
			
			while((line = br.readLine()) != null) {
				record = line.trim().split("@");
				if (record.length != 2) 
					continue;
				
				es.execute(new FileProcessor(queue, record));
				
//				if (br.readLine() == null) {
//					queue.put(new Poison(record[0].hashCode(), record[1]));
//				}
			}

			es.shutdown();
			
			//Code adapted from: https://stackoverflow.com/questions/1250643/how-to-wait-for-all-threads-to-finish-using-executorservice
			try {
				es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			queue.put(new Poison(record[0].hashCode(), record[1]));
			es1.shutdown();
//			
//			try {
//				es1.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			
			//Start a single thread executor for QueryFileParser, and return queryMap
//			ExecutorService es1 = Executors.newSingleThreadExecutor();
//			es1.submit(new KmerProcessor(queue));
			
//			Thread t = new Thread(new KmerProcessor(queue));
//			t.start();
			
//			try {
//				t.join();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			
			br.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	private void parseLine(String text, String lang) {
//		int k = 4;
//		
//		try {
//			for (int i = 0; i < text.length() - k; i++) {
//				CharSequence kmer = text.substring(i, i + k);
//				queue.put(new Kmer (kmer.hashCode(), lang));
//				//System.out.println(kmer);
//			}
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
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
	//}
	
	
//	private class Processor implements Runnable {
//		private String[] record;
//		
//		public Processor(String[] record) {
//			super();
//			this.record = record;
//		}
//
//		@Override
//	    public void run() {
//            try {
//            	parse(record[0], record[1]);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//	    }
//	}
	
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
