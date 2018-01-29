/**
*   @Author: Jung Woo (Noel) Park
*   Student ID: 1162424
*/

import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {

		JFrame frame = new JFrame("Quilting Bee");
        DisplayPanel mainContentPane = new DisplayPanel();

        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(mainContentPane);
        frame.pack();
        frame.setVisible(true);

        mainContentPane.setOpaque(true);

    	Scanner sc = new Scanner(System.in);

    	while(sc.hasNextLine()) {
			String line = sc.nextLine();
			String[] inputs = line.split(" ");
			
            mainContentPane.addSquareData(inputs);
    	}

        mainContentPane.determineBestSize();
        mainContentPane.generateSquares();

    	mainContentPane.render();
	}
}