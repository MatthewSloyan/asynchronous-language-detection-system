package ie.gmit.sw;

import java.io.File;
import java.util.Scanner;

public class InitialiseDatabase {
	private Scanner console = new Scanner(System.in);
	private boolean isValid;
	private String filePath;

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
