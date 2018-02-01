import java.util.LinkedList;
import java.util.Comparator;

public class WordChainComparer implements Comparator<LinkedList<String>> {
	
	public int compare(LinkedList<String> o1, LinkedList<String> o2) {
		if (o1.size() > o2.size()) {
			return 1;
		} else if (o1.size() < o2.size()) {
			return -1;
		} 
		return 0;
	}
}