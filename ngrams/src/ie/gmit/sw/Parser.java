package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Parser implements Runnable{
	private String file;
	private int k;
	
	public Parser(String file, int k) {
		super();
		this.file = file;
		this.k = k;
	}
	
	public void run() {
		try {
			//BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			// Code adapted to get resource in web application: https://stackoverflow.com/questions/10978380/how-to-read-a-text-file-from-a-web-application
			BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(file)));
			String line = null;
			String[] record = null;
			
			//Start a thread pool of size 2, and loop
			ExecutorService es = Executors.newFixedThreadPool(10);
			
			while((line = br.readLine()) != null) {
				record = line.trim().split("@");
				if (record.length != 2)
					continue;
				
				es.execute(new FileProcessor(record, k));
			}

			es.shutdown();
			
			//Code adapted from: https://stackoverflow.com/questions/1250643/how-to-wait-for-all-threads-to-finish-using-executorservice
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
