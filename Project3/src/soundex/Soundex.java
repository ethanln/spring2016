package soundex;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Soundex {
	private final String url = "dictionary.txt";
	
	private Map<String, TreeSet<String>> soundex;
	private Set<String> dictionary;
	
	public Soundex(){
		this.soundex = new TreeMap<String, TreeSet<String>>();
		this.dictionary = new TreeSet<String>();
	}
	
	/**
	 * initialize dictionary
	 */
	public void buildDictionary(){
		File dictionaryFile = new File(url);
		
		try {
			// read dictionary.txt file
			BufferedReader wikiReader = new BufferedReader(new FileReader(dictionaryFile));
			
			String word;
			
			// parse all words in dictionary
			while ((word = wikiReader.readLine()) != null){
				
				String code = this.encodeString(word);
				
				// check if code already exists as key
				if(!this.soundex.containsKey(code)){
					this.soundex.put(code, new TreeSet<String>());
				}
				
				// lowercase all letters
				word = word.toLowerCase();
				
				// rid of all punctuation
				word = word.replaceAll("[^A-Za-z0-9]", "");
				
				// insert word by code key
				TreeSet<String> temp = this.soundex.get(code);
				temp.add(word);
				this.soundex.put(code, temp);
				
				// add word to dictionary
				this.dictionary.add(word);
				
			}
			wikiReader.close();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * find soundex code for a string
	 * @param word
	 * @return
	 */
	public String encodeString(String word){
		if(word == null || word.length() < 1){
			return word;
		}

		// lowercase all letters
		word = word.toLowerCase();
		
		// rid of all punctuation
		word = word.replaceAll("[^A-Za-z0-9]", "");
		
		// 1) Keep the first letter (in upper case)
		StringBuilder code = new StringBuilder();
		code.append(word.toUpperCase().charAt(0));
		
		// 2) Replace these letters with hyphens
		word = this.hyphenReplace(word);
		
		// 3) Replace the other letters by numbers.
		word = this.numberReplace(word);
		
		// 4) remove adjacent repeats of a numbers
		word = this.deleteAdjacentNumbers(word);
		
		// substring the first letter to parse the rest
		word = word.substring(1, word.length());
		
		// 5) delete hyphens
		word = word.replace("-", "");
		
		// 6) Keep the first three numbers or pad out with zeros
		if(word.length() < 3){
			while(word.length() < 3){
				word += "0";
			}
		}
		else{
			word = word.substring(0, 3);
		}
		
		code.append(word);
		
		return code.toString();
	}
	
	/**
	 * 2) Replace these letters with hyphens
	 * @param word
	 * @return
	 */
	private String hyphenReplace(String word){
		word = word.replace('a', '-');
		word = word.replace('e', '-');
		word = word.replace('i', '-');
		word = word.replace('o', '-');
		word = word.replace('u', '-');
		word = word.replace('y', '-');
		word = word.replace('h', '-');
		word = word.replace('w', '-');
		
		return word;
	}
	
	/**
	 * 3) Replace the other letters by numbers.
	 * @param word
	 * @return
	 */
	private String numberReplace(String word){
		/* for numbers of 1 */
		word = word.replace('b', '1');
		word = word.replace('f', '1');
		word = word.replace('p', '1');
		word = word.replace('v', '1');
		
		/* for numbers of 2 */
		word = word.replace('c', '2');
		word = word.replace('g', '2');
		word = word.replace('j', '2');
		word = word.replace('k', '2');
		word = word.replace('q', '2');
		word = word.replace('s', '2');
		word = word.replace('x', '2');
		word = word.replace('z', '2');
		
		/* for numbers of 3 */
		word = word.replace('d', '3');
		word = word.replace('t', '3');
		
		/* for numbers of 4 */
		word = word.replace('l', '4');
		
		/* for numbers of 5 */
		word = word.replace('m', '5');
		word = word.replace('n', '5');
		
		/* for numbers of 6 */
		word = word.replace('r', '6');

		return word;
	}
	
	/**
	 * 4) remove adjacent repeats of a numbers
	 * @param word
	 * @return
	 */
	private String deleteAdjacentNumbers(String word){
		char[] wordArray = word.toCharArray();
		
		// instantiate word for format
		StringBuilder formattedWord = new StringBuilder("");
		
		char prev = ' ';
		
		// if word count is empty, return the word
		if(wordArray.length < 1){
			return word;
		}
		
		// initialize the previous character with the first character
		prev = wordArray[0];
		formattedWord.append(prev);
		
		for(int i = 1; i < word.length(); i++){
			if(prev == wordArray[i] 
					&& Character.isDigit(prev) 
					&& Character.isDigit(wordArray[i])){
				
				continue;
			}
			prev = wordArray[i];
			formattedWord.append(wordArray[i]);
		}
		
		return formattedWord.toString();
	}
	
	/**
	 * get word with specified code string
	 * @param code
	 * @return
	 */
	public TreeSet<String> getWords(String code){
		return this.soundex.get(code);
	}
	
	
	public boolean isCorrect(String word){
		return this.dictionary.contains(word);
	}
	/**
	 * edit distance algorithm
	 * @param word1
	 * @param word2
	 * @return
	 */
	public static int LevenShteinDistance(String word1, String word2){
		int[][] table = new int[word1.length() + 1][word2.length() + 1];
		int cost; 
		
		for(int i = 0; i < word1.length() + 1; i++){
			table[i][0] = i;
		}
		
		for(int j = 0; j < word2.length() + 1; j++){
			table[0][j] = j;
		}
		
		for(int i = 1; i < word1.length() + 1; i++){
			for(int j = 1; j < word2.length() + 1; j++){
				cost = word1.charAt(i - 1) == word2.charAt(j - 1) ? 0 : 1;
				
				table[i][j] = Math.min(table[i - 1][j] + 1, table[i][j - 1] + 1);
				table[i][j] = Math.min(table[i][j], table[i - 1][j - 1] + cost);
			}
		}
		
		return table[word1.length()][word2.length()];
	}
}
