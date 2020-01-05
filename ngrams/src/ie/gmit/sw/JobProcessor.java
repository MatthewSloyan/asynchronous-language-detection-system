package ie.gmit.sw;

import java.util.concurrent.ConcurrentHashMap;

/**
* This class processes the jobs on the inQueue which is added by the JobProducer.
* It also calls the PredictLanguage() class which returns the predicted language to be added to the outqueue.
* I decided there should only be one instance of this, as there should only ever be one in and out queue.
* To achieved this I implemented a Singleton design pattern. 
* This queue could be used for different type of jobs as it just contains a String key and String object.
* 
* I have used a ConcurrentHashMap for the outQueueMap as it's the fastest type (search, insert and delete functions are O(1)).
* Also it allows for concurrency between multiple threads as a thread is running to check process jobs in the queue.
* 
* @author Matthew Sloyan
*/
public class JobProcessor implements Runnable {
	
	private static JobProcessor instance = new JobProcessor();
	private static ConcurrentHashMap<String, String> outQueueMap = new ConcurrentHashMap<String, String>();

	private JobProcessor() {}
    
    public static JobProcessor getInstance() {
        return instance;
    }
   
    /**
	* Get the out queue map object to check in the ServiceHandler if the job is completed.
	* Running time: O(1) 
	* 
	* @return outQueueMap instance of the out queue.
	*/
	public ConcurrentHashMap<String, String> getOutQueueMap() {
		return outQueueMap;
	}

	/**
	* Runnable thread that is spawned from the JobProducer to ensure it's always running.
	* Takes job (LanguageRequest) from the in queue and predicts the language, once completed it is added to to the outQueueMap.
	* Running time: O(1) 
	* 
	* @see LanguageRequest
	* @see JobProducer
	* @see PredictLanguage
	*/
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
