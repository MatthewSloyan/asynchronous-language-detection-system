package ie.gmit.sw;

public class InitialiseDatabase {

	public void initialise(String languageDataSet) {
		// start the running time of program to be printed out for user
		long startTime = System.nanoTime(); 
		
		Thread t = new Thread(new Parser(languageDataSet, 4));
		t.start();
		
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Database.getInstance().resize(300);
		
		// running time
		System.out.println("\nRunning time (ms): " + (System.nanoTime() - startTime));
		final long usedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		System.out.println("Used memory: " + usedMem + "\n");
	}
}
