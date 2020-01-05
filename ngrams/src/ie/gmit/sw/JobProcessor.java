package ie.gmit.sw;

import java.util.concurrent.ConcurrentHashMap;

public class JobProcessor implements Runnable {
	
	private static JobProcessor instance = new JobProcessor();
	private static ConcurrentHashMap<String, String> outQueueMap = new ConcurrentHashMap<String, String>();

	private JobProcessor() {}
    
    public static JobProcessor getInstance() {
        return instance;
    }
   
	public ConcurrentHashMap<String, String> getOutQueueMap() {
		return outQueueMap;
	}

	@Override
    public void run() {
    	// Only runs when elements in the queue.
        while(true) {
            try {
            	LanguageRequest request = JobProducer.getInQueue().take();
            	
            	long startTime = System.nanoTime(); 
            	String result = new PredictLanguage(request.getQuery(), 4).analyseQuery();
            	System.out.println("\nRunning time (ms): " + (System.nanoTime() - startTime));
            	
            	outQueueMap.put(request.getTaskNum(), result);
            	
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
