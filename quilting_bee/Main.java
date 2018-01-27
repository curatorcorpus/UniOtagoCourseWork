
import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {

		JFrame frame = new JFrame("Iota Display");
        DisplayPanel mainContentPane = new DisplayPanel(600, 600);

        mainContentPane.setOpaque(true);

        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(mainContentPane);
        frame.pack();
        frame.setVisible(true);

    	Scanner sc = new Scanner(System.in);

    	while(sc.hasNextLine()) {
			String line = sc.nextLine();
			String[] inputs = line.split(" ");
			
			float scale = Float.parseFloat(inputs[0]);

			int r = Integer.parseInt(inputs[1]);
			int g = Integer.parseInt(inputs[2]);
			int b = Integer.parseInt(inputs[3]);

			mainContentPane.setSquare(scale, r, g, b);
    	}

    	mainContentPane.rerender();
	}
}