package ie.gmit.sw;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
* This class processes the requests and adds them to the inQueue for the JobProcessor to process.
* SRP is upheld as all it does is add to the queue, and start the JobProcessor running if it's not already.
* I decided there should only be one instance of this, as there should only ever be one in and out queue.
* To achieved this I implemented a Singleton design pattern. 
* This queue could be used for different type of jobs as it just contains a String key and String object.
* 
* I have used a BlockingQueue as the loop in the JobProcessor to take from the queue will only run when there is jobs in the queue.
* Also it will block automatically and wait if too many jobs are processed at once.
* 
* @author Matthew Sloyan
*/
public class JobProducer {
	
	private static JobProducer instance = new JobProducer();
	private static BlockingQueue<LanguageRequest> inQueue = null;
	
	private JobProducer() {}
    
    public static JobProducer getInstance() {
        return instance;
    }
    
    /**
	* Get the in queue object to check in the Processor if there's a job in the queue.
	* Running time: O(1) 
	* 
	* @return inQueue BlockingQueue.
	*/
    public static BlockingQueue<LanguageRequest> getInQueue() {
		return inQueue;
	}
    
    /**
	* Checks if the queue is initialised or not, if so then the JobProcessor is already running.
	* If not then create a new instance and start the processor so it is always looking for jobs.
	* Running time: O(1) 
	* 
	* @return inQueue BlockingQueue.
	*/
    private void initialize() {
        if (inQueue == null) {
        	inQueue = new ArrayBlockingQueue<>(10);
            new Thread(JobProcessor.getInstance()).start();
        }
    }
    
    /**
	* Checks if the Processor is running and puts the request in the queue.
	* It will only put it in if the queue has space, otherwise it will wait.
	* Running time: O(1) 
	*/
    public void putJobInQueue(LanguageRequest request) {
        try {
        	initialize();
			inQueue.put(request);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
}
