
public class TelephoneFormatter {

    // ENUM TYPES.
    
    private enum Prefixes {
	FREEPHONE, MOBILE, LANDLINE, INVALID
    }
    

    // DATA FIELDS.
    
    private String freephonePrefixRegx = "(0508|0800|09000)";
    
    // CONSTRUCTORS.
    
    public TelephoneFormatter() {}

    // GETTERS SETTERS.

    

    // PUBLIC METHODS.

    public String format(String teleNumber) {

	System.out.println(determinePrefix(teleNumber) + " origin: " + teleNumber);
	
	return "";
    }
    

    /// PRIVATE METHODS.

    private Prefixes determinePrefix(String number) {
	
	// check parentheses, if there are parenthesis, we can assume the telephone
	// number code will be between the parenthesis.
	if(number.contains("(") || number.contains(")")) {

	    // extract code within parenthesis.
	    String prefix = number.replaceAll(".*\\(|\\).*", "");
	    
	    if(prefix.length() == 4) {
		return Prefixes.FREEPHONE;
	    } else if(prefix.length() == 3) {
		return Prefixes.MOBILE;
	    } else {
		return Prefixes.LANDLINE;
	    }
	}

	// determine if phone number is a FREEPHONE.
	String prefix = number.substring(0,4);
	
	return Prefixes.INVALID;
    }
}
