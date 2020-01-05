package ie.gmit.sw;

import java.io.File;
import java.util.Scanner;

/**
* InitialiseDatabase - Middleman class that handles all the setting up of the database.
* It is called on init() of servlet.
*
* @author Matthew Sloyan
*/
public class InitialiseDatabase {
	private Scanner console = new Scanner(System.in);
	private boolean isValid;
	private String filePath;

	/**
	* Sets up thread to run Parser which sets up the single instance of Database through a Proxy.
	* Once completed it resizes the database to only the top 300 most frequently occurring Kmers through the Proxy.
	* Also allows the administrator of the application to set the location of the Wili text file so it's secure.
	* 
	* @param languageDataSet the path to the Wili text file.
	* @see Parser
	* @see DatabaseProxy
	*/
	public void initialise(String languageDataSet) {
//		do {
//			System.out.println("Please enter path to Wili File.");
//			filePath = console.nextLine();
//
//			File f = new File(filePath);
//			isValid = true;
//		
//			//check if file exists, keeps asking till it is valid
//			if (f.exists()) {
//				isValid = false;
//			} else {
//				System.out.println("File does not exist, please try again.");
//			}
//		} while (isValid);
		
		// start the running time of program to be printed out for user
		long startTime = System.nanoTime(); 
		
		// Start a new thread to Parse the file line by line.
		Thread t = new Thread(new Parser(languageDataSet, 4));
		t.start();
		
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		new DatabaseProxy().resize(300);
		
		// running time
		System.out.println("\nRunning time (ms): " + (System.nanoTime() - startTime));
		final long usedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		System.out.println("Used memory: " + usedMem + "\n");
	}
}
