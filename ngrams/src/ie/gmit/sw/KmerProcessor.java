package ie.gmit.sw;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class KmerProcessor implements Runnable{
	
	private BlockingQueue<Kmer> queue;
	
	public KmerProcessor(BlockingQueue<Kmer> queue) {
		super();
		this.queue = queue;
	}

	@Override
	public void run() {
		boolean running = true;
		
		while(running) {
			try {
				//take Kmer object off queue
				Kmer k = queue.take();
				
				if(k instanceof Poison){
					running = false;
					System.out.println("Completed 1");
				}
				else{
					Database.getInstance().add(k.getLanguage(), k.getKmer());
					//System.out.println(k.getLanguage());
					//System.out.println("Queue Take: " + k.getKmer());
				}
			} catch (InterruptedException e) {
				System.out.println("Error occured: " + e.getMessage());
			}
		}

		System.out.println("Completed");
	}
}
