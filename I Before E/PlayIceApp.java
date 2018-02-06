import java.util.*;

public class PlayIceApp{

	public static void main(String [] args){

		//DATA FIELD DECLARATIONS
		int mode = 0;
		String alphabet = "";
		ArrayList<String> forbiddenStrings = new ArrayList<String>();
		ArrayList<String[]> exceptions = new ArrayList<String[]>();

		//CREATE SCANNER
		Scanner sc = new Scanner(System.in);
		while(sc.hasNextLine()){
			String line = sc.nextLine();
			//MODE IS 0 FOR FIRST LINE
			if(mode==0){
				//SETS LINE AS ALPHABET
				alphabet = line;
				mode++;
			}
			//MODE IS 1 AFTER FIRST LINE
			else if(mode==1&&!(line.isEmpty())){
				//SPLITS LINE INTO STRINGS, FIRST STRING IS ADDED TO FORBIDDEN STRINGS
				String[] substrings = line.split(" ");
				forbiddenStrings.add(substrings[0]);
				//THE FOLLOWING STRINGS ARE ADDED TO EXCEPTIONS STRINGS ARRAY
				String[] exceptionStrings = new String[substrings.length-1];
            	System.arraycopy(substrings, 1, exceptionStrings, 0, substrings.length-1);
            	//THE EXCEPTIONSTRINGS ARRAY IS ADDED TO THE 2D ARRAYLIST OF EXCEPTIONS
            	exceptions.add(exceptionStrings);
			}
			//MODE IS INCREMENTED IF IT IS THE SECOND LINE OR BEYOND AND IS BLANK
			else if(mode==1&&line.isEmpty()){
				mode++;
			}
			//MODE IS 2 WHEN WE ARE READING FROM THE LINE AFTER THE BLANK ONE
			else if(mode==2){
				PlayIce play = new PlayIce(line, alphabet, forbiddenStrings, exceptions);
				System.out.println(play);
			}
		}

	}

}