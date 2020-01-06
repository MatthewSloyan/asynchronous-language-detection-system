package ie.gmit.sw;

/**
* InitialiseDatabase - Middleman class that handles all the setting up of the database.
* It is called on init() of servlet.
*
* @author Matthew Sloyan
*/
public class InitialiseDatabase {

	/**
	* Sets up thread to run Parser which sets up the single instance of Database through a Proxy.
	* Once completed it resizes the database to only the top 300 most frequently occurring Kmers through the Proxy.
	* Also calls Path methods to get the file path from the user or the url to the WiLi file.
	* 
	* @param languageDataSet the path to the Wili text file.
	* @see Path
	* @see Parser
	* @see DatabaseProxy
	*/
	public void initialise(String path) {
		// Get the full file path from the user or url to raw text file.
		Path p = new Path();
		p.getUserEntry();
		
		// start the running time of program to be printed out for user
		long startTime = System.nanoTime(); 
		
		// Start a new thread to Parse the file line by line.
		Thread t = new Thread(new Parser(p.getPath(), 4, p.getOption()));
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
