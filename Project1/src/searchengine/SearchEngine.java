package searchengine;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeSet;

import model.Document;
import exception.*;
import util.Tokenizer;

public class SearchEngine {
	private Tokenizer tokenizer;
	private static final String uri = "wiki" + File.separator + "To_be_posted";
	
	public SearchEngine() throws SearchEngineException{
		try{
			this.tokenizer = new Tokenizer();
		}
		catch(TokenizerException e){
			throw new SearchEngineException();
		}
		this.setupIndexing();
	}
	
	private void setupIndexing() throws SearchEngineException{
		try{
			File[] files = new File(uri).listFiles();
			for(File file : files){
				tokenizer.parse(file);
			}
		}
		catch(TokenizerException e){
			throw new SearchEngineException();
		}
	}
	
	public void run(){		
		Scanner inputListener = new Scanner(System.in);
		System.out.print("Type in a query: ");
		while(true){
			String query = inputListener.nextLine();
			
			System.out.print(query + "\n");
			
			if(query.equals("q")){
				break;
			}
			
			ArrayList<String> tokens = this.tokenizer.parseQuery(query);
			this.rankDocuments(tokens);
			System.out.print("\nType another query: ");
		}
		
		inputListener.close();
	}
	
	private void rankDocuments(ArrayList<String> tokens){
		ArrayList<String> docs = this.tokenizer.getDocIds();
		TreeSet<Document> rankedDocuments = new TreeSet<Document>();
		for(String doc : docs){
			rankedDocuments.add(scoreDocument(tokens, doc));
		}
		
		for(Document doc : rankedDocuments){
			System.out.print("id: " + doc.getDocName() + " \t score: " + doc.getScore() + "\n");
		}
		System.out.print("SIZE: " + rankedDocuments.size() + "\n");
	}
	
	private Document scoreDocument(ArrayList<String> tokens, String doc){
		
		double rank = 0.0;
		for(String token : tokens){
			rank += (TF(token, doc) * IDF(token));
		}
		
		return new Document(doc, rank);
	}
	
	public double TF(String word, String document){

		if(document.equals("118")){
			double me = 0.0;
		}
		double frequency = (double)this.tokenizer.getNumberOfKeyWordsInDocument(document, word);
		// just need to implement this
		return frequency / (double)this.tokenizer.getWordCountByDocId(document);
	}
	
	public double IDF(String word){
		double totalDocuments = this.tokenizer.getNumberOfDocuments();
		double totalDocumentsForWord = this.tokenizer.getNumberOfDocumentsWithWord(word);
		double amount = Math.log(totalDocuments / totalDocumentsForWord) / Math.log(2);
		return amount;
	}
}
