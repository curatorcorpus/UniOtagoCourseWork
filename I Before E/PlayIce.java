
import java.util.*;

public class PlayIce{

	private String input;
	private char[] alphabet;
	private ArrayList<String> forbiddenStrings;
	private ArrayList<String[]> exceptions;
	private String validity;

	public PlayIce(String in, String alph, ArrayList<String> fs, ArrayList<String[]> exc){
		input = in;
		alphabet = alph.toCharArray();
		forbiddenStrings = fs;
		exceptions = exc;
	}

	public String toString(){
		return "INPUT: " + input;
	}
}