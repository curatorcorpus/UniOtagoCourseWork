/**
*	@Author Jung Woo (Noel) Park.
	Student ID: 1162424.
*/
import java.util.Scanner;

public class Main {

    public static void main(String args[]) {

        Scanner sc = new Scanner(System.in);

    	while(sc.hasNextLine()) {
			
            String input = sc.nextLine();
            String target = sc.nextLine();
            
            if(input.equals("") && target.equals("")) {

            } else {
                ArithmeticTree arth = new ArithmeticTree();
                arth.setExpectedTarget(target);
                System.out.println(arth.search(input));
            }
    	}
    }
}
