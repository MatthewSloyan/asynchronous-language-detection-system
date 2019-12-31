package ie.gmit.sw;

import java.util.concurrent.ConcurrentHashMap;

public class JobProcessor implements Runnable {
	
	private static JobProcessor instance = null;
	private static ConcurrentHashMap<String, String> outQueue = new ConcurrentHashMap<String, String>();

	private JobProcessor() {}
    
    public static JobProcessor getInstance() {
        if (instance == null) {
            instance = new JobProcessor();
        }
        return instance;
    }
   
	public ConcurrentHashMap<String, String> getOutQueue() {
		return outQueue;
	}

	@Override
    public void run() {
    	// Only runs when elements in the queue.
        while(true) {
            try {
            	LanguageRequest request = QueryProducer.getInQueue().take();
                //Do stuff
            	Parser parser = InitialiseDatabase.getInstance().getParser();
            	String result = parser.analyseQuery(request.getQuery());
            	
            	outQueue.put(request.getTaskNum(), result);
            	
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
