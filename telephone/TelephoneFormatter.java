/**
*	@Author Jung Woo (Noel) Park.
	Student ID: 1162424.
*/

import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;

public class TelephoneFormatter {
    
    // STATIC FIELDS.

    private static Map<Character, String> telephoneKeypadLookup;
    static {
	
		telephoneKeypadLookup = new HashMap<Character, String>();

		telephoneKeypadLookup.put('A', "2");
    	telephoneKeypadLookup.put('B', "2");
    	telephoneKeypadLookup.put('C', "2");
    	telephoneKeypadLookup.put('D', "3");
    	telephoneKeypadLookup.put('E', "3");
    	telephoneKeypadLookup.put('F', "3");
    	telephoneKeypadLookup.put('G', "4");
    	telephoneKeypadLookup.put('H', "4");
    	telephoneKeypadLookup.put('I', "4");
    	telephoneKeypadLookup.put('J', "5");
    	telephoneKeypadLookup.put('K', "5");
    	telephoneKeypadLookup.put('L', "5");
    	telephoneKeypadLookup.put('M', "6");
    	telephoneKeypadLookup.put('N', "6");
    	telephoneKeypadLookup.put('O', "6");
    	telephoneKeypadLookup.put('P', "7");
    	telephoneKeypadLookup.put('Q', "7");
    	telephoneKeypadLookup.put('R', "7");
    	telephoneKeypadLookup.put('S', "7");
    	telephoneKeypadLookup.put('T', "8");
    	telephoneKeypadLookup.put('U', "8");
    	telephoneKeypadLookup.put('V', "8");
    	telephoneKeypadLookup.put('W', "8");
    	telephoneKeypadLookup.put('X', "9");
    	telephoneKeypadLookup.put('Y', "9");
    	telephoneKeypadLookup.put('Z', "9");
    }

    private Map<String, Boolean> duplicatesLookup;

    // ENUM TYPES.
    
    private enum Category { FREEPHONE, MOBILE, LANDLINE, INVALID }
    private enum Identity { F0508, F0800, F0900, L02, L03, L04, L06, L07,
    						L09, M021, M022, M027, M025, UNKNOWN }

	// DATA FIELDS.

    private String freephoneRegx    = "(0508|0800|0900)";
    private String mobileRegx       = "(021|022|025|027)";
    private String landlineRegx     = "(02|03|04|05|06|07|09)";
    private String uppercaseRegx	= ".*[A-Z]+.*";

    // CONSTRUCTORS.
    
    /*
    *	Default constructor.
    */
    public TelephoneFormatter() {

    	this.duplicatesLookup = new HashMap<String, Boolean>();
    }

    // PUBLIC METHODS.

    /*
    *	Method checks category, identity and validity of telephone number. 
    *	Formats number accordingly.
    */
    public String format(String teleNumber) {

    	if(teleNumber.equals("")) {
    		return " INV";
    	}

    	boolean isDuplicate = false;

    	Category category = determineCategory(teleNumber);
		String prefix = "";
		String number = "";
		String originalNumber = teleNumber;

		// remove parenthesis if it exists.
		if(teleNumber.contains("(") || teleNumber.contains(")")) {

		    // extract prefix code if within parenthesis.
		    teleNumber = teleNumber.replaceAll("[()]", "");
		}

		// split prefix and number depending on its category.
		switch(category) {
			case FREEPHONE:
				prefix = teleNumber.substring(0,4);
    			number = teleNumber.substring(4);
				break;
			case MOBILE:
				prefix = teleNumber.substring(0,3);
    			number = teleNumber.substring(3);				
				break;
			case LANDLINE:
				prefix = teleNumber.substring(0,2);
    			number = teleNumber.substring(2);				
				break;
			case INVALID:
				originalNumber += " INV";
				return originalNumber;		
		}

		// determine identity of number.
		Identity id = determineIdentity(prefix);

		// determine placement spaces and dashes.
		boolean isAppropriate = determineAppropriateSpaceAndDashes(category, id, originalNumber, prefix, number);

		if(!isAppropriate) {

			originalNumber += " INV";
			if(isDuplicate) {
				originalNumber += " DUP";
			}
			return originalNumber;
		}

    	// filter prefix, spaces and dashes.
		teleNumber = teleNumber.replaceAll(" ","");
    	teleNumber = teleNumber.replaceAll("-","");
    	number = number.replaceAll(" ","");
    	number = number.replaceAll("-","");

    	// determine validity of number.
		boolean isValid = determineValidity(teleNumber, prefix, category, id);

		if(!isValid) {

			originalNumber += " INV";
			if(isDuplicate) {
				originalNumber += " DUP";
			}
			return originalNumber;
		}

		teleNumber = buildFormat(prefix, number, id);
		// check if this number was already called in the batch file.
		if(duplicatesLookup.containsKey(teleNumber)) {
			isDuplicate = true;
		} else {
			duplicatesLookup.put(teleNumber, true);
		}

		if(isDuplicate) {
			teleNumber += " DUP";
		}
		return teleNumber;
    }

    /// PRIVATE METHODS.


    /*
    *	Method determines category of number (mobile, landline, freephone).
    */
    private Category determineCategory(String number) {

		// check parentheses, if there are parenthesis, we can assume the telephone
		// number code will be between the parenthesis.
		if(number.contains("(") || number.contains(")")) {

		    // extract prefix code if within parenthesis.
		    String prefix = number.replaceAll(".*\\(|\\).*", "");

		    if(prefix.length() == 4) {
				return Category.FREEPHONE;
		    } else if(prefix.length() == 3) {
				return Category.MOBILE;
		    } else {
				return Category.LANDLINE;
		    }
		}

		// is it a freephone number?
		String prefix = number.substring(0,4);
		if(prefix.matches(freephoneRegx)) {
		    return Category.FREEPHONE;
		}

		// is it a mobile number?
		prefix = prefix.substring(0,3);
		if(prefix.matches(mobileRegx)) {
		    return Category.MOBILE;
		}

		// is it a landline number?
		prefix = prefix.substring(0,2);
		if(prefix.matches(landlineRegx)) {
		    return Category.LANDLINE;
		}

		return Category.INVALID;
	}

	private boolean determineAppropriateSpaceAndDashes(Category cat, Identity id, String originalNumber, String prefix, String number) {

		String dashRegx = "^([0-9]*-[0-9]*){1}$";
		String spaceRegx = "^([0-9]* [0-9]*){1}$";
		String dashBetweenLettersRegx = "[A-Z]-[A-Z]";

		String withoutSpaceOrDash = number.replaceAll(" ","");
    	withoutSpaceOrDash = number.replaceAll("-","");
    	int lengthWithoutSpaceOrDash = withoutSpaceOrDash.length();

    	int noOfDashes = 0;
    	int noOfSpaces = 0;

    	for(int i = 0; i < originalNumber.length(); i++) {

    		if(originalNumber.charAt(i) == ' ') {
    			noOfSpaces++;
    		}
    		if(originalNumber.charAt(i) == '-') {
    			noOfDashes++;
    		}
    	}

		if(prefix.contains("-")) {
			return false;
		}
		else if(noOfDashes > 1) {
			return false;
		}
		else if(noOfSpaces > 2) {
			return false;
		}

		if(noOfDashes == 1) {
			
			if(number.matches(uppercaseRegx)) {
				return false;
			}
			else if(lengthWithoutSpaceOrDash == 5) {
				return false;
			} else if(lengthWithoutSpaceOrDash == 6 || lengthWithoutSpaceOrDash == 7) {
				if(number.indexOf("-") != 3) {
					return false;
				}
			} else if(lengthWithoutSpaceOrDash == 8) {
				if(number.indexOf("-") != 4) {
					return false;
				}
			}
			return true;
}
		else if(noOfSpaces == 1 || noOfSpaces == 2) {
			if(lengthWithoutSpaceOrDash == 5) {
				return false;
			} else if(lengthWithoutSpaceOrDash == 6 || lengthWithoutSpaceOrDash == 7) {
				if(number.indexOf(" ") == 0) {
					return true;
				} else if(number.indexOf(" ") != 3) {
					return false;
				}
			} else if(lengthWithoutSpaceOrDash == 8) {
				if(number.indexOf(" ") == 0) {
					return true;
				} else if(number.indexOf(" ") != 4) {
					return false;
				}
			} 
			return true;
		}
		else if(noOfSpaces == 0 && noOfDashes == 0) {
			return true;
		}
		return false;
	}

	/*
	*	Method determine specific type of number prefix.
	*/
	private Identity determineIdentity(String prefix) {

		// determines identity based on prefix.
		switch(prefix) {

			// Freephone
			case "0508": return Identity.F0508;
			case "0800": return Identity.F0800;
			case "0900": return Identity.F0900;

			// Landline
			case "02":	 return Identity.L02;
			case "03":	 return Identity.L03;
			case "04":	 return Identity.L04;
			case "06":	 return Identity.L06;
			case "07":	 return Identity.L07;
			case "09":	 return Identity.L09;

			// Mobile
			case "021":	 return Identity.M021;
			case "022":	 return Identity.M022;
			case "027":	 return Identity.M027;
			case "025":	 return Identity.M025;

			default: 	 return Identity.UNKNOWN;
		}
	}

	private String truncate(String number, Identity id) {
		    	// determines if numbers follow length rules.
		switch(id) {

			// Freephone
			case F0508:
				if(number.length() > 6) {
					number = number.substring(0,6);
				}
				break;

			case F0800:
				if(number.length() > 7) {
					number = number.substring(0,7);
				}
				break;

			case F0900:
				if(number.length() > 5) {
					number = number.substring(0,5);
				}
				break;

			// Mobile
			case M021:
				if(number.length() > 8) {
					number = number.substring(0,8);
				}
				break;
			
			case M022:
				if(number.length() > 7) {
					number = number.substring(0,7);
				}
				break;
			
			case M027:
				if(number.length() > 7) {
					number = number.substring(0,7);
				}
				break;
			
			case M025:
				if(number.length() > 6) {
					number = number.substring(0,7);
				}
				break;

			// default to landline. landline codes similar rules.
			default:
				number = number.substring(0,7);
		}

		return number;
	}

	/*
	*	Method determines if number is consistent with length rules of standard format. 	
	*/
    private boolean determineValidity(String number, String prefix, Category cat, Identity id) {

    	String alphabetRegx		   = ".*[a-zA-Z]+.*";
    	String lowercaseRegx 	   = ".*[a-z]+.*";
    	String landlineNumRuleRegx = "[2-9]";

		int firstSpace = number.indexOf(" ");

    	// Preliminary checks. Determines validity by applying group rules.
    	if(cat == Category.FREEPHONE) {
    		number = number.substring(4);
    		if(number.matches(lowercaseRegx)) {
    			return false;
    		}else if(number.matches(uppercaseRegx)) {

    			// determines number of uppercase characters in free phone (less than or equal to 9).
    			int counter = 0;
    			for(int i = 0; i < number.length(); i++) {
    				if(Character.isLetter(number.charAt(i))) {
    					++counter;
    				}
    			}

    			// check if length of Uppercase letter exceeds 9.
    			if(counter <= 9) {
    				return true;
    			}
    			return false;
    		}
    	} 
    	// checks if mobile numbers contain text.
    	else if(cat == Category.MOBILE) {
    		number = number.substring(3);
    		if(number.matches(alphabetRegx)) {
    			return false;
    		}
    	} 
    	// could also check if landline number has text.
    	else if(cat == Category.LANDLINE) {
    		number = number.substring(2);
    		if(number.length() != 7) {
    			return false;
    		}
    	}

    	// determines if numbers follow length rules.
		switch(id) {

			// Freephone
			case F0508:

				if(number.length() == 6) {
					return true;
				}
				break;

			case F0800:

				if(number.length() == 6 || number.length() == 7) {
					return true;
				}
				break;

			case F0900:

				if(number.length() == 5) {
					return true;
				}
				break;

			// Mobile
			case M021:

				if(6 <= number.length() && number.length() <= 8) {
					return true;
				}
				break;
			
			case M022:
			
				if(number.length() == 7) {
					return true;
				}
				break;
			
			case M027:
				
				if(number.length() == 7) {
					return true;
				}
				break;
			
			case M025:
				if(number.length() == 6) {
					return true;
				}
				break;

			case L02:
				number = number.replaceAll(" ","");
    			number = number.replaceAll("-","");

				if(number.substring(0,3).equals("409")) {
					return true;
				}
				break;

			// default to landline. landline codes similar rules.
			default:

				// check the exceptions for next three digits.
				String nextFirstDigit  = number.substring(0,1);
				String nextThreeDigits = number.substring(0,3);

				if(nextThreeDigits.equals("900") || 
				   nextThreeDigits.equals("911") || 
				   nextThreeDigits.equals("999")) {

					return true;
				} 

				// if it doesnt follow any of the exception test landline rule.
				if(nextFirstDigit.matches(landlineNumRuleRegx)) {
					return true;
				} else {
					return false;
				}
		}

		return false;
    }

    /*
    *	Method formats the number by adding spaces, decrypting text, and substituting 
    *	special case mobile numbers.
    */
    private String buildFormat(String prefix, String number, Identity id) {

    	StringBuilder sb;

    	number = truncate(number, id);

    	// if id is special mobile case.
    	if(id == Identity.M025) {
			sb = new StringBuilder("027");
    	} else {
    		sb = new StringBuilder(prefix);
    	}

    	int numberSize = number.length();

    	// initial space after code.
    	sb.append(" ");

    	// determine if number consists of text, and decrypt.
    	if(number.matches(uppercaseRegx)) {
    		number = decryptTextKeypad(number, numberSize);
    	}

    	// no spaces required
    	if(numberSize == 5) {
    		sb.append(number);
    	} 
    	// special case for mobile numbers with 025.
    	else if(numberSize == 6 && id == Identity.M025) {

    		sb.append("4");
    		for(int i = 0; i < numberSize; i++) {

	    		if(i == 2) {
	    			sb.append(" ");
	    		}

	    		sb.append(number.charAt(i));
    		}
    	}
    	// space between third and fourth digit.
    	else if(numberSize == 6 || numberSize == 7) {
    		for(int i = 0; i < numberSize; i++) {

	    		if(i == 3) {
	    			sb.append(" ");
	    		}
	    		sb.append(number.charAt(i));
    		}
    	}
    	// space after fourth digit.
    	else if(numberSize == 8) {
    		for(int i = 0; i < numberSize; i++) {

	    		if(i == 4) {
	    			sb.append(" ");
	    		}
	    		sb.append(number.charAt(i));
    		}
    	}
    	// no space requirement for numbers size 9. 
    	else {
    		sb.append(number);
    	}
    	return sb.toString();
    }

    /*
    *	Method that decrypts uppercase letters to numbers.
    */
    private String decryptTextKeypad(String number, int length) {

    	StringBuilder sb = new StringBuilder();
    	for(int i = 0; i < length; i++) {

    		char currChar = number.charAt(i);

    		if(Character.isDigit(currChar)) {
    			sb.append(currChar);
    		} else {
    			sb.append(telephoneKeypadLookup.get(currChar));
    		}
    	}
    	return sb.toString();
    }
}
