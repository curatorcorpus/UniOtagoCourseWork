import java.util.Scanner;
import java.util.PriorityQueue;
import java.util.LinkedList;
import java.util.HashSet;

public class JoinUp {
	
	private static String firstWord;
	private static String secondWord;
	
	private static HashSet<String> visited = new HashSet<String>();
	
	static int minLengthSJ(String w1, String w2) { 
		return (1+Math.min(w1.length(), w2.length()))/2;
	}
	static int minLengthDJ(String w1, String w2) { 
		return (1+Math.max(w1.length(), w2.length()))/2;
	}
	
	public static void main(String[] args) {
		JoinUpDict dict = new JoinUpDict();
		Scanner sc = new Scanner(System.in);
		
		firstWord = args[0];
		secondWord = args[1];
		
		while (sc.hasNext())
			dict.addWord(sc.next());
			
		findWordChain(dict, true);
		visited.clear();
 		findWordChain(dict, false);
		
	}
	
	private static void findWordChain(JoinUpDict dict, boolean sj) {
	
		PriorityQueue<LinkedList<String>> q = new PriorityQueue<LinkedList<String>>(1000, new WordChainComparer());
		LinkedList<String> firstWordChain = new LinkedList<String>();
		boolean foundChain = false;
		
		firstWordChain.add(firstWord);
		q.add(firstWordChain);
		while (q.size() != 0) {
			LinkedList<String> wordChain = q.poll();
			String wordChainsLastWord = wordChain.peekLast();
			int len = wordChainsLastWord.length();
			
			if (match(wordChainsLastWord, secondWord, sj)) {
				wordChain.add(secondWord);
				printWordChain(wordChain);
				foundChain = true;
				break;
			}
			for (int i = len - 1; i >= 0; i--) {
				String suffix = wordChainsLastWord.substring(i, len);
				String[] words = dict.getWords(suffix);
				if (words != null) {
					for (String word : words) {	
						if (match(wordChainsLastWord, word, sj) && !visited.contains(word)) {
							LinkedList<String> newWordChain = new LinkedList<String>(wordChain);
							newWordChain.add(word);
							q.add(newWordChain);
							visited.add(word);
						}
					}
				}
			}
		}
		if (!foundChain) System.out.println("0");
	}
	
	private static void printWordChain(LinkedList<String> chain) {
		System.out.print(chain.size() + " ");
		for (Object word : chain.toArray())
			System.out.print(word + " ");
		System.out.println();
	}
	
	private static boolean match(String w1, String w2, boolean sj) {
		int l1 = w1.length();
		int l2 = w2.length();
		int reqLen;
		
		if (sj) reqLen = minLengthSJ(w1, w2);
		else reqLen = minLengthDJ(w1, w2);
		
		if (l1 < reqLen || l2 < reqLen) return false;
		
		while (reqLen <= l1 && reqLen <= l2) {
			if (w1.substring(l1-reqLen, l1).equals(w2.substring(0, reqLen)))
				return true;
			reqLen++;
		}
		return false;
	}
}