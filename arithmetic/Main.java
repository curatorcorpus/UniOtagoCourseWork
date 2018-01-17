/**
*	@Author Jung Woo (Noel) Park.
	Student ID: 1162424.
*/
import java.util.Scanner;

public class Main {

    public static void main(String args[]) {

    	Arithmetic arth = new Arithmetic();
        Scanner sc = new Scanner(System.in);

    	while(sc.hasNextLine()) {
			
            String input = sc.nextLine();
            String target = sc.nextLine();
            
            System.out.println(arth.search(input, target));
    	}
    }
}
