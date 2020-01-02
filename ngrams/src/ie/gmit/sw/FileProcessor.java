package ie.gmit.sw;

import java.util.concurrent.BlockingQueue;

public class FileProcessor implements Runnable {
	private BlockingQueue<Kmer> queue;
	private String[] record;
	
	public FileProcessor(BlockingQueue<Kmer> queue, String[] record) {
		super();
		this.queue = queue;
		this.record = record;
	}

	@Override
    public void run() {
        try {
        	parseLine(record[0], record[1]);
        	
        	//finishes
        	queue.put(new Poison(record[0].hashCode(), Language.valueOf(record[1]))); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	private void parseLine(String text, String lang) {

		Language language = Language.valueOf(lang);
		int k = 4;
		
		try {
			for (int i = 0; i < text.length() - k; i++) {
				CharSequence kmer = text.substring(i, i + k);
				queue.put(new Kmer (kmer.hashCode(), language));
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
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
}
