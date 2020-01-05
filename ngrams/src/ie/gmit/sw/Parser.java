package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
* Parser class - Used to parse any file, and add kmers to database.
*
* @author Matthew Sloyan
*/
public class Parser implements Runnable{
	private String file;
	private int k;
	
	/**
	* Constructor for class
	* 
	* @param file the location of the wili file
	* @param k the max number of Kmers
	*/
	public Parser(String file, int k) {
		super();
		this.file = file;
		this.k = k;
	}
	
	/**
	* Runnable thread that uses a file (Wili) and parses it line by line.
	* Each line is split into the text and the language name at the @ delimiter.
	* Using an ExecutorService a thread pool is created and each line is processed by the FileProcessor as a single runnable thread.
	* The thread then waits till all threads in the ES are completed as it was causing quite a few problems if let run concurrently.
	*
	* For simplicity and SRP this class only parses the files and handles the ES
	* I also tried to get the application working with a BlockingQueue where each line would be parsed, 
	* then each Kmer would be added to queue and a separate class would take each kmer off the queue and add to the database.
	* This worked but it was considerably slower than the approach below due to the number of lines in the text file and 
	* number of possible kmers (1-4) on each line.
	* 
	* Running time: Linear O(N);
	* T(n) = n + 7
	* 
	* @see FileProcessor
	*/
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			// Code adapted to get resource in web application: https://stackoverflow.com/questions/10978380/how-to-read-a-text-file-from-a-web-application
			//BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(file)));
			String line = null;
			String[] record = null;
			
			//Start a thread pool of size 2, and loop
			ExecutorService es = Executors.newFixedThreadPool(8);
			
			while((line = br.readLine()) != null) {
				record = line.trim().split("@");
				if (record.length != 2)
					continue;
				
				es.execute(new FileProcessor(record, k));
			}
			
			es.shutdown();
			
			// Code adapted from below to wait for an ExecutorService to finish running, 
			// as problems where encountered when trying to do it in the background on startup of server.
			// https://stackoverflow.com/questions/1250643/how-to-wait-for-all-threads-to-finish-using-executorservice
			try {
				es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			br.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
