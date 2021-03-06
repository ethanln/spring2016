package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import exception.TokenizerException;

public class ParserService {
	
	private static ParserService inst;
	
	private StopWords stopWords;
	private PorterStemmer stemmer;
	
	private Set<String> dictionary;
	
	private static final String dictionaryUri = "dictionary.txt";
	
	private ParserService(){
		this.stopWords = new StopWords();
		this.stemmer = new PorterStemmer();
		this.dictionary = new TreeSet<String>();
		
		this.setupDictionary();
	}
	
	private static ParserService instance(){
		if(inst == null){
			inst = new ParserService();
		}
		return inst;
	}
	
	/**
	 * setup the dictionary
	 * @throws TokenizerException
	 */
	private void setupDictionary(){
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
			e.printStackTrace();
		}
		finally{
			dictionaryReader.close();
		}
	}
	
	/**
	 * check if word is in the dictionary
	 * @param word
	 * @return
	 */
	private boolean isInDictionary(String word){
		return this.dictionary.contains(word);
	}
	
	public static ArrayList<String> parseQuery(String input){
		return instance()._parseQuery(input);
	}
	private ArrayList<String> _parseQuery(String input){
		ArrayList<String> tokens = new ArrayList<String>();
		this.parseQuery(new Scanner(input), tokens);
		return tokens;
	}
	
	private void parseQuery(Scanner queryReader, ArrayList<String> tokens){
		
		while(queryReader.hasNext()){
			String token = queryReader.next();
			token = token.toLowerCase();
							
			//token = token.replaceAll("[^\\P{P}-]+","");
			token = token.replaceAll("[^-A-Za-z0-9':;]", "");
			
			if(this.stopWords.contains(token)){
				continue;
			}

			if(this.stripQueryToken("-", tokens, token) 
					|| this.stripQueryToken("'", tokens, token)
					|| this.stripQueryToken(";", tokens, token)
					|| this.stripQueryToken(":", tokens, token)){
				continue;
			}
			else{
					String stemmedToken = this.stemmer.stem(token);
					if(!stemmedToken.equals("Invalid term") && !stemmedToken.equals("No term entered")){
						tokens.add(stemmedToken);
					}				
			}
		}
		queryReader.close();
	}
	
	private boolean stripQueryToken(String character, ArrayList<String> tokens, String token){
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
				this.parseQuery(new Scanner(token.replaceAll(character, " ")), tokens);
				return true;
			}
		}
		return false;
	}
}
