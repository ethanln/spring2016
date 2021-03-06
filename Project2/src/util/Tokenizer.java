package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Tokenizer {
	
	//private static final String dictionaryUri = "dictionary.txt";
	
	private static Tokenizer instance;
	private StopWords stopWords;
	private PorterStemmer stemmer;
	
	//private TreeSet<String> dictionary;
	
	private Tokenizer(){
		this.stopWords = new StopWords();
		this.stemmer = new PorterStemmer();
		
		//this.dictionary = new TreeSet<String>();
		//this.setupDictionary();
	}
	
	/**
	 * setup the dictionary
	 * @throws TokenizerException
	 */
	/*private void setupDictionary(){
		File dictionaryFile = new File(dictionaryUri);
		Scanner dictionaryReader = null;
		try {
			dictionaryReader = new Scanner(dictionaryFile);
			while(dictionaryReader.hasNext()){
				String token = dictionaryReader.next();
				this.dictionary.add(token);
			}
		} 
		catch (FileNotFoundException e) {
			System.out.println("dictionary screwed up");
		}
		finally{
			dictionaryReader.close();
		}
	}*/
	
	/**
	 * check if word is in the dictionary
	 * @param word
	 * @return
	 */
	/*private boolean isInDictionary(String word){
		return this.dictionary.contains(word);
	}*/
	
	private static Tokenizer inst(){
		if(instance == null){
			instance = new Tokenizer();
		}
		return instance;
	}
	
	public static List<String> parseQuery(String query){
		return inst()._parseQuery(query);
	}
	
	private List<String> _parseQuery(String query){
		ArrayList<String> keyTerms = new ArrayList<String>();
		this.parseQuery(new Scanner(query), keyTerms, true);
		return keyTerms;
	}
	
	private void parseQuery(Scanner queryReader, ArrayList<String> tokens, boolean _isFirstToken){
		boolean isFirstToken = _isFirstToken;
		
		while(queryReader.hasNext()){
			String token = queryReader.next();
			
			// lower case token
			token = token.toLowerCase();
			
			// eliminate punctuation
			token = token.replaceAll("[^A-Za-z0-9]", "");
			
			// examine leading stop words
			if(isFirstToken){
				isFirstToken = false;
				// eliminate stop words
				if(this.stopWords.contains(token)){
					continue;
				}
			}
			
			// should I stem the token????
			//try{
				// stem token
				//String stemmedToken = this.stemmer.stem(token);
				//if(stemmedToken.equals("Invalid term") || stemmedToken.equals("No term entered")){
				//	continue;
				//}
				
				//tokens.add(stemmedToken);
			//}
			//catch(Exception e){
				tokens.add(token);
				//continue;
			//}
			
			// strip tokens: hyphens, colons, semi-colons, and apostrophes
			//if(this.stripQueryToken("-", tokens, token) 
			//		|| this.stripQueryToken("'", tokens, token)
			//		|| this.stripQueryToken(";", tokens, token)
			//		|| this.stripQueryToken(":", tokens, token)){
			//	continue;
			//}
			//else{
					//String stemmedToken = this.stemmer.stem(token);
					//if(!stemmedToken.equals("Invalid term") && !stemmedToken.equals("No term entered")){
						//tokens.add(stemmedToken);
				
					//}				
			//}
		}
		queryReader.close();
	}
	
	/*private boolean stripQueryToken(String character, ArrayList<String> tokens, String token){
		if(token.contains(character)){
			String strippedHyphenStr = token.replaceAll(character, "");
			if(this.isInDictionary(strippedHyphenStr)){
				String stemmedToken = this.stemmer.stem(strippedHyphenStr);
				if(!stemmedToken.equals("Invalid term") && !stemmedToken.equals("No term entered")){
					tokens.add(stemmedToken);
					return true;
				}
			}
			else{
				this.parseQuery(new Scanner(token.replaceAll(character, " ")), tokens, false);
				return true;
			}
		}
		return false;
	}*/
}
