/**
*	@Author Jung Woo (Noel) Park.
	Student ID: 1162424.
*/

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

    public static void main(String args[]) {

		TelephoneFormatter tf = new TelephoneFormatter();
    	Scanner sc = new Scanner(System.in);

    	while(sc.hasNextLine()) {
			String line = sc.nextLine();
			System.out.println(tf.format(line));
    	}
    }
}
