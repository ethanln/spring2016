package util;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

import exception.TokenizerException;
import models.*;

public class Tokenizer {
	
	private static final String dictionaryUri = "dictionary.txt";
	
	private TreeSet<String> dictionary;
	
	private DocumentTermIndex documentTerm;
	private TermDocumentIndex termDocument;
	
	private PorterStemmer stemmer;
	private StopWords stopWords;
	
	private Map<String, Integer> wordFrequencies;
	private long totalWords;
	
	public Tokenizer() throws TokenizerException{
		// instantiate dictionary
		this.dictionary = new TreeSet<String>();
		
		// instantiate wiki indexing data structure
		this.termDocument = new TermDocumentIndex();
		this.documentTerm = new DocumentTermIndex();
		
		// instantiate utilities
		this.stemmer = new PorterStemmer();
		this.stopWords = new StopWords();
		
		this.wordFrequencies = new TreeMap<String, Integer>();
		this.totalWords = 0;
		
		// set up dictionary
		this.setupDictionary();
	}
	
	/**
	 * setup the dictionary
	 * @throws TokenizerException
	 */
	private void setupDictionary() throws TokenizerException{
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
			throw new TokenizerException();
		}
		finally{
			dictionaryReader.close();
		}
	}
	
	/**
	 * parse file
	 * @param file
	 * @throws TokenizerException
	 */
	public void parse(File file) throws TokenizerException{
		String fileName = file.getName();
		String id = fileName.replaceAll("[^0-9]", "");
		
		try {
			BufferedReader wikiReader = new BufferedReader(new FileReader(file));
			this.parseWiki(wikiReader, id);
		} 
		catch (FileNotFoundException e) {
			throw new TokenizerException();
		}
		catch (IOException e) {
			throw new TokenizerException();
		}
	}
	
	/**
	 * parse the wiki document
	 * @param reader
	 * @param id
	 * @throws IOException
	 */
	private void parseWiki(BufferedReader reader, String id) throws IOException{
		
		String lineToken;
		while ((lineToken = reader.readLine()) != null){
			parseLine(new Scanner(lineToken), id);
		}
	    reader.close();
	}
	
	/**
	 * parse line of the wiki
	 * @param reader
	 * @param id
	 */
	private void parseLine(Scanner reader, String id){
		while(reader.hasNext()){
			String token = reader.next();
			token = token.toLowerCase();

			//token = token.replaceAll("[^\\P{P}-]+","");
			token = token.replaceAll("[^-A-Za-z0-9':;]", "");
			
			if(this.stopWords.contains(token)){
				continue;
			}
			
			if(this.stripDocumentToken("-", token, id) 
					|| this.stripDocumentToken("'", token, id)
					|| this.stripDocumentToken(";", token, id)
					|| this.stripDocumentToken(":", token, id)){
				continue;
			}
			else{
				// add word frequency
				this.addWordFrequency(token);
				
				String stemmedToken = this.stemmer.stem(token);
				if(!stemmedToken.equals("Invalid term") && !stemmedToken.equals("No term entered")){
					this.termDocument.addKeyTerm(stemmedToken, id);
					this.documentTerm.addKeyTerm(stemmedToken, id);
				}
				
			}
		}
		reader.close();
	}
	
	private boolean stripDocumentToken(String character, String token, String id){
		if(token.contains(character)){
			String strippedHyphenStr = token.replaceAll(character, "");
			if(this.isInDictionary(strippedHyphenStr)){
				
				// add word frequency
				this.addWordFrequency(strippedHyphenStr);
				
				String stemmedToken = this.stemmer.stem(strippedHyphenStr);
				if(!stemmedToken.equals("Invalid term") && !stemmedToken.equals("No term entered")){
					this.termDocument.addKeyTerm(stemmedToken, id);
					this.documentTerm.addKeyTerm(stemmedToken, id);
					return true;
				}
			}
			else{
				this.parseLine(new Scanner(token.replaceAll(character, " ")), id);
				return true;
			}
		}
		return false;
	}
	
	private void addWordFrequency(String word){
		// update frequencies
		if(!this.wordFrequencies.containsKey(word)){
			this.wordFrequencies.put(word, 0);
		}
		int count = this.wordFrequencies.get(word);
		count++;
		this.wordFrequencies.put(word, count);
		
		// update total words count;
		this.totalWords++;
	}
	
	/**
	 * check if word is in the dictionary
	 * @param word
	 * @return
	 */
	private boolean isInDictionary(String word){
		return this.dictionary.contains(word);
	}

	
	public String getDocWithMostFrequency(String word){
		return this.termDocument.getDocWithMostFrequency(word);
	}
	
	public int getHighestFrequency(String word){
		return this.termDocument.getHighestFrequency(word);
	}
	
	public int getNumberOfDocumentsWithWord(String word){
		return this.termDocument.getNumberOfDocumentsWithWord(word);
	}
	
	public int getNumberOfKeyWordsInDocument(String docId, String word){
		return this.termDocument.getNumberOfKeyWordsInDocument(word, docId);
	}
	
	public int getWordCollectionFrequency(String word){
		if(this.wordFrequencies.containsKey(word)){
			return this.wordFrequencies.get(word);
		}
		return 0;
	}
	
	public long getCollectionFrequency(){
		return this.totalWords;
	}
	
	/**
	 * get the number of documents total
	 * @return
	 */
	public int getNumberOfDocuments(){
		return this.documentTerm.size();
	}
	
	public Iterator<String> getDocIds(String keyWord){
		return this.termDocument.getAllCorrelatedDocuments(keyWord);
	}
	
	public int getHighestFrequencyInDoc(String docId){
		return this.documentTerm.getHighestFrequency(docId);
	}
	
	public ArrayList<String> parseQuery(String input){
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
