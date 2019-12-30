package ie.gmit.sw;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class QueryProducer {
	
	private static QueryProducer instance = null;
	private static BlockingQueue<LanguageRequest> inQueue = null;
	
	private QueryProducer() {}
    
    public static QueryProducer getInstance() {
        if (instance == null) {
            instance = new QueryProducer();
        }
        return instance;
    }
    
    public static BlockingQueue<LanguageRequest> getInQueue() {
		return inQueue;
	}
    
    private void initialize() {
        if (inQueue == null) {
        	inQueue = new ArrayBlockingQueue<>(10);
            new Thread(new JobProcessor()).start();
        }
    }
    
    public void putJobInQueue(LanguageRequest request) {
        try {
        	initialize();
			inQueue.put(request);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    class JobProcessor implements Runnable {
        @Override
        public void run() {
        	// Only runs when elements in the queue.
            while(true) {
                try {
                	LanguageRequest eventData = inQueue.take();
                    //Do stuff
                	
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
