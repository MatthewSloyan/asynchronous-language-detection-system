package ie.gmit.sw;

import java.util.concurrent.ConcurrentHashMap;

public class JobProcessor implements Runnable {
	
	private static JobProcessor instance = null;
	private static ConcurrentHashMap<String, String> outQueueMap = new ConcurrentHashMap<String, String>();

	private JobProcessor() {}
    
    public static JobProcessor getInstance() {
        if (instance == null) {
            instance = new JobProcessor();
        }
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
            	Parser parser = InitialiseDatabase.getInstance().getParser();
            	
            	long startTime = System.nanoTime(); 
            	String result = parser.analyseQuery(request.getQuery());
            	System.out.println("\nRunning time (ms): " + (System.nanoTime() - startTime));
            	
            	outQueueMap.put(request.getTaskNum(), result);
            	
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
