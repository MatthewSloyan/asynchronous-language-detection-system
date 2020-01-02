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
        	//queue.put(new Poison(record[0].hashCode(), Language.valueOf(record[1]))); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	private void parseLine(String text, String lang) throws InterruptedException {

		//Language language = Language.valueOf(lang);
		int k = 4;
		
		try {
//			for (int i = 0; i < text.length() - k; i++) {
//				CharSequence kmer = text.substring(i, i + k);
//				//Database.getInstance().add(language, kmer.hashCode());
//				queue.put(new Kmer (kmer.hashCode(), lang));
//				//System.out.println("Queue Put: " + kmer);
//			}
//			for (int i = 0; i <= k; i++) {
//				for (int j = 0; j < text.length() - i; j++) {
//					CharSequence kmer = text.substring(j, j + i);
//					queue.put(new Kmer (kmer.hashCode(), language));
//				}
//			}
			for (int i = 1; i <= k; i++) {
				for (int j = 0; j < text.length() - i; j+=i) {
					CharSequence kmer = text.substring(j, j + i);
					queue.put(new Kmer (kmer.hashCode(), lang));
					//Database.getInstance().add(lang, kmer.hashCode());
					//System.out.println("Queue Put: " + kmer);
				}
			}
		} catch (Exception e) {
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
