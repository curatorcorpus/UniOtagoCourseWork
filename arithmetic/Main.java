/**
*	@Author Jung Woo (Noel) Park.
	Student ID: 1162424.
*/
import java.util.Scanner;

public class Main {

    public static void main(String args[]) {

    	ArithmeticTree arth = new ArithmeticTree();
        Scanner sc = new Scanner(System.in);

    	while(sc.hasNextLine()) {
			
            String input = sc.nextLine();
            String target = sc.nextLine();
            
            if(input.equals("") && target.equals("")) {

            } else {                
                arth.setExpectedTarget(target);
                System.out.println(arth.evaluate(input));
            }
    	}
    }
}
