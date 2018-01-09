import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

    public static void main(String args[]) {

		String filename = "";

		// determine we have a filename by args.
		try {

		    filename = args[0] + ".txt";	    
	        } catch(ArrayIndexOutOfBoundsException e) {
		    System.err.println("Input file not provided by argument!");
		    return;
		}


		// read each line of each unformatted phone number.
		try(BufferedReader br = new BufferedReader(new FileReader(filename))) {

		    TelephoneFormatter tf = new TelephoneFormatter();
		    StringBuilder sb = new StringBuilder();
		    String line = "";
		    
		    while((line = br.readLine()) != null) {

			tf.format(line);
		    }

		} catch(FileNotFoundException e) {
		    System.err.println("File not found!");
		} catch(IOException e) {
		    System.err.println("Incorrect Input");
		}
    }
}
