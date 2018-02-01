import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class JoinUpDict {

	private HashMap<String,HashSet<String>> dict = new HashMap<String,HashSet<String>>();
	
	
	public void addWord(String word) {
		for (int i = 0; i <= word.length(); i++) {
			if (dict.containsKey(word.substring(0, i))) {
				HashSet<String> set = dict.get(word.substring(0, i));
				set.add(word);
			} else {
				HashSet<String> set = new HashSet<String>();
				set.add(word);
				dict.put(word.substring(0, i), set);
			}
		}
	}
	
	private boolean prefixExists(String prefix) {
		return dict.containsKey(prefix);
	}
	
	public String[] getWords(String prefix) {
		if (prefixExists(prefix)) {
			int count = 0;
			String[] words = new String[dict.get(prefix).size()];
			Iterator wordSet = dict.get(prefix).iterator();
			while (wordSet.hasNext())
				words[count++] = wordSet.next().toString();	
				
			return words;
		} 
		return null;
	}
	
	public void printDict() {
		for (String prefix: dict.keySet()){
            String key = prefix;
            HashSet<String> value = dict.get(prefix);
            Iterator words = value.iterator();
            
            System.out.println("Key: " + key);
            System.out.print("Words: ");  
            while (words.hasNext())
            	System.out.print(words.next() + " ");
            	
            System.out.println();
		}
	}
	
	
}