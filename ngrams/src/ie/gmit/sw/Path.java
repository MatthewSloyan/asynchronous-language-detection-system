package ie.gmit.sw;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

public class Path {
	private int option;
	private String path;
	
	public Path() {
		super();
	}

	public int getOption() {
		return option;
	}

	public String getPath() {
		return path;
	}
	
	/**
	* Allows the administrator of the application to set the location of the Wili text file so it's secure.
	*/
	public void getUserEntry() {
		Scanner console = new Scanner(System.in);
		boolean isValid;
		String option;
		
		do {
			System.out.println("\nPlease select an option:\n (1) Use WiLi File\n (2) Use WiLi URL");
			option = console.nextLine();
			
			isValid = true;
			
			if (Integer.parseInt(option) == 1) {
				do {
					System.out.println("Please enter full path to Wili File E.g C:/data/wili-2018-Edited.txt");
					path = console.nextLine();

					File f = new File(path);
					isValid = true;
				
					//check if file exists, keeps asking till it is valid
					if (f.exists()) {
						isValid = false;
					} else {
						System.out.println("File does not exist, please try again.");
					}
				} while (isValid);
			} else if (Integer.parseInt(option) == 2) {
				do {
					System.out.println("Please Enter URL to raw WiLi text file.");
					path = console.nextLine();

					isValid = true;

					//check if URL exists, keeps asking till it is valid
					try {
						new URL(path).toURI();
						isValid = false;
					} catch (URISyntaxException | MalformedURLException e) {
						isValid = true;	
					}
				} while (isValid);
			} else {
				System.out.println("Invalid option, please try again.");
			} 
		} while (isValid);
		
		console.close();
	}
}