package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeSet;

import container.DocumentTermIndex;
import container.TermDocumentIndex;
import exception.TokenizerException;

public class Tokenizer {
	
	private static final String dictionaryUri = "dictionary.txt";
	
	private TreeSet<String> dictionary;
	
	private DocumentTermIndex documentTerm;
	private TermDocumentIndex termDocument;
	
	private PorterStemmer stemmer;
	private StopWords stopWords;
	
	public Tokenizer() throws TokenizerException{
		// instantiate dictionary
		this.dictionary = new TreeSet<String>();
		
		// instantiate wiki indexing data structure
		this.termDocument = new TermDocumentIndex();
		this.documentTerm = new DocumentTermIndex();
		
		// instantiate utilities
		this.stemmer = new PorterStemmer();
		this.stopWords = new StopWords();
		
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

			
			if(this.stopWords.contains(token)){
				continue;
			}
			
			token = token.replaceAll("[^\\P{P}-]+","");
			token = token.replaceAll("[^-A-Za-z0-9]", "");
			
			if(token.contains("-")){
				String strippedHyphenStr = token.replaceAll("-", "");
				if(this.isInDictionary(strippedHyphenStr)){
					this.termDocument.addKeyTerm(strippedHyphenStr, id);
					this.documentTerm.addKeyTerm(strippedHyphenStr, id);
				}
				else{
					this.parseLine(new Scanner(token.replaceAll("-", " ")), id);
				}
			}
			else{
				if(!this.stopWords.contains(token)){
					String stemmedToken = this.stemmer.stem(token);
					if(!stemmedToken.equals("Invalid term") && !stemmedToken.equals("No term entered")){
						this.termDocument.addKeyTerm(stemmedToken, id);
						this.documentTerm.addKeyTerm(stemmedToken, id);
					}
				}
			}
		}
		reader.close();
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
			
			
			if(this.stopWords.contains(token)){
				continue;
			}
			
			token = token.replaceAll("[^\\P{P}-]+","");
			token = token.replaceAll("[^-A-Za-z0-9]", "");
			
			if(token.contains("-")){
				String strippedHyphenStr = token.replaceAll("-", "");
				if(this.isInDictionary(strippedHyphenStr)){
					tokens.add(strippedHyphenStr);
				}
				else{
					this.parseQuery(new Scanner(token.replaceAll("-", " ")), tokens);
				}
			}
			else{
				if(!this.stopWords.contains(token)){
					String stemmedToken = this.stemmer.stem(token);
					if(!stemmedToken.equals("Invalid term") && !stemmedToken.equals("No term entered")){
						tokens.add(stemmedToken);
					}
				}
			}
		}
		queryReader.close();
	}
	
}
