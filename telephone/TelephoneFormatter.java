
public class TelephoneFormatter {

    // ENUM TYPES.
    
    private enum Prefixes {
	FREEPHONE, MOBILE, UNUSED_MOBILE, LANDLINE, INVALID
    }
    

    // DATA FIELDS.
    
    private String freephoneRegx    = "(0508|0800|09000)";
    private String mobileRegx       = "(021|022|027)";
    private String unusedMobileRegx = "(025)";
    private String landlineRegx     = "(02|03|04|05|06|07|09)";
    
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

	if(prefix.matches(freephoneRegx)) {
	    return Prefixes.FREEPHONE;
	}

	prefix = prefix.substring(0,3);
	if(prefix.matches(mobileRegx)) {
	    return Prefixes.MOBILE;
	} else if(prefix.matches(unusedMobileRegx)) {
	    return Prefixes.UNUSED_MOBILE;
	}

	prefix = prefix.substring(0,2);
	if(prefix.matches(landlineRegx)) {
	    return Prefixes.LANDLINE;
	}
	
	return Prefixes.INVALID;
    }
}
