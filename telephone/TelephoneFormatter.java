
import java.util.Map;
import java.util.HashMap;

public class TelephoneFormatter {

    // ENUM TYPES.
    
    private enum Category { FREEPHONE, MOBILE, UNUSED_MOBILE, LANDLINE, INVALID }
    private enum Identity { F0508, F0800, F0900, L02, L03, L04, L06, L07,
    						L09, M021, M022, M027, M025, UNKNOWN }

    
    // STATIC FIELDS.

    private static Map<String, String> telephoneKeypadLookup;
    static {
	
		telephoneKeypadLookup = new HashMap<String, String>();

		telephoneKeypadLookup.put("2", "ABC");
    	telephoneKeypadLookup.put("3", "DEF");
    	telephoneKeypadLookup.put("4", "GHI");
    	telephoneKeypadLookup.put("5", "JKL");
    	telephoneKeypadLookup.put("6", "MNO");
    	telephoneKeypadLookup.put("7", "PQRS");
    	telephoneKeypadLookup.put("8", "TUV");
    	telephoneKeypadLookup.put("9", "WXYZ");

    }

	// DATA FIELDS.

    private String freephoneRegx    = "(0508|0800|09000)";
    private String mobileRegx       = "(021|022|027)";
    private String unusedMobileRegx = "(025)";
    private String landlineRegx     = "(02|03|04|05|06|07|09)";
    
    // CONSTRUCTORS.
    
    public TelephoneFormatter() {

    }

    // GETTERS SETTERS.



    // PUBLIC METHODS.

    public String format(String teleNumber) {

    	Category category = determineCategory(teleNumber);

    	//TODO: determine invalids + add tests for invalid numbers.

		System.out.println(category + " origin: " + teleNumber);
	
		// remove parenthesis if it exists.
		if(teleNumber.contains("(") || teleNumber.contains(")")) {

		    // extract prefix code if within parenthesis.
		    teleNumber = teleNumber.replaceAll(".*\\(|\\).*", "");
		}

		String prefix = "";

		switch(category) {
			case FREEPHONE:
				prefix = teleNumber.substring(0,4);
				break;
			case MOBILE:
				prefix = teleNumber.substring(0,3);
				break;
			case LANDLINE:
				prefix = teleNumber.substring(0,2);
				break;
			case INVALID:

				break;
		}

		Identity id = determineIdentity(prefix);

		boolean isValid = determineValidity(teleNumber);

		if(isValid) {

		} else {
			teleNumber += " INV";
		}

		System.out.println("Identity " + id);
		return teleNumber;
    }
    

    /// PRIVATE METHODS.

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

		// determine number type if there are no parenthesis.

		// is it a freephone number?
		String prefix = number.substring(0,4);
		if(prefix.matches(freephoneRegx)) {
		    return Category.FREEPHONE;
		}

		// is it a mobile number?
		prefix = prefix.substring(0,3);
		if(prefix.matches(mobileRegx)) {
		    return Category.MOBILE;
		} else if(prefix.matches(unusedMobileRegx)) {
		    return Category.UNUSED_MOBILE;
		}

		// is it a landline number?
		prefix = prefix.substring(0,2);
		if(prefix.matches(landlineRegx)) {
		    return Category.LANDLINE;
		}

		return Category.INVALID;
	}

	private Identity determineIdentity(String prefix) {

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

    private boolean determineValidity(String number) {

    	return false;
    }
}
