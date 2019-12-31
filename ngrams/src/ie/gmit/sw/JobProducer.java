package ie.gmit.sw;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class JobProducer {
	
	private static JobProducer instance = null;
	private static BlockingQueue<LanguageRequest> inQueue = null;
	
	private JobProducer() {}
    
    public static JobProducer getInstance() {
        if (instance == null) {
            instance = new JobProducer();
        }
        return instance;
    }
    
    public static BlockingQueue<LanguageRequest> getInQueue() {
		return inQueue;
	}
    
    private void initialize() {
        if (inQueue == null) {
        	inQueue = new ArrayBlockingQueue<>(10);
            new Thread(JobProcessor.getInstance()).start();
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
}
