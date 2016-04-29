package searchengine;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeMap;
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
			try{
				String query = inputListener.nextLine();
				
				System.out.print(query + "\n");
				
				if(query.equals("q")){
					break;
				}
				
				ArrayList<String> tokens = this.tokenizer.parseQuery(query);
				this.rankDocuments(tokens);
				System.out.print("\nType another query: ");
			}
			catch(Exception e){
				System.out.print("something screwed up");
			}
		}
		
		inputListener.close();
	}
	
	private void rankDocuments(ArrayList<String> tokens){
		//TreeSet<Document> rankedDocuments = new TreeSet<Document>();
		TreeMap<String, Double> rankedDocuments = new TreeMap<String, Double>();
		TreeSet<Document> documents = new TreeSet<Document>();
		
		for(String token : tokens){
			Iterator<String> docs = this.tokenizer.getDocIds(token);
			if(docs == null){
				continue;
			}
			while(docs.hasNext()){
				String doc = docs.next();
				
				if(doc.equals("114")){
					// Do we need to implement some incidence term-document to term-term algorithm?
					System.out.println("Stuff");
				}
				double TF = this.TF(token, doc);
				double IDF = this.IDF(token);
				double score = TF * IDF;
				
				if(!rankedDocuments.containsKey(doc)){
					rankedDocuments.put(doc, 0.0);
				}
				double currentScore = rankedDocuments.get(doc);
				currentScore += score;
				rankedDocuments.put(doc, currentScore);
				
				//docs.remove();
			}
		}
		
		for(String doc : rankedDocuments.keySet()){
			double count = rankedDocuments.get(doc);
			documents.add(new Document(doc, count));
		}
		
		for(Document doc : documents){
			System.out.print("id: " + doc.getDocName() + " \t score: " + doc.getScore() + "\n");
		}
		System.out.print("SIZE: " + rankedDocuments.size() + "\n");
	}

	
	private double TF(String word, String document){
		double frequency = (double)this.tokenizer.getNumberOfKeyWordsInDocument(document, word);
		// just need to implement this
		return frequency / (double)this.tokenizer.getHighestFrequencyInDoc(document);
	}
	
	private double IDF(String word){
		double totalDocuments = (double)this.tokenizer.getNumberOfDocuments();
		double totalDocumentsForWord = (double)this.tokenizer.getNumberOfDocumentsWithWord(word);
		double amount = (double)Math.log(totalDocuments / totalDocumentsForWord) / Math.log(2.0);
		return amount;
	}
}
