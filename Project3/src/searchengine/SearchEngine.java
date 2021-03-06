package searchengine;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

import exception.TokenizerException;
import models.Document;
import models.Result;
import noiseychannel.NoiseyChannel;
import soundex.Soundex;
import util.SnippetGenerator;
import util.Tokenizer;

public class SearchEngine {
		
	private static final String uri = "wiki" + File.separator + "To_be_posted";
	
	private Soundex soundex;
	private Tokenizer tokenizer;
	private NoiseyChannel noiseyChannelModel;
	
	private Result result;
	
	public SearchEngine(){
		this.soundex = new Soundex();
		try {
			this.tokenizer = new Tokenizer();
		} 
		catch (TokenizerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void init(){
		System.out.print("Perparing soundex... \n");
		this.soundex.buildDictionary();
		
		System.out.print("Perparing wiki... \n");
		
		// recursively load all file documents into the index
		try{
			File[] files = new File(uri).listFiles();
			for(File file : files){
				tokenizer.parse(file);
			}
		}
		catch(TokenizerException e){
			e.printStackTrace();
		}
		
		System.out.print("Perparing noisey channel model... \n");
		this.noiseyChannelModel = new NoiseyChannel(tokenizer, soundex);
		this.noiseyChannelModel.setup();
		
		System.out.print("finished! \n");
	}
	
	public void run(){
		Scanner inputListener = new Scanner(System.in);
		System.out.print("Type in a query: ");
		while(true){
			this.result = new Result();
			try{
				String query = inputListener.nextLine();
				
				System.out.print("Query: " + query + "\n");
				
				if(query.equals("q")){
					break;
				}
				
				// get corrected query
				String correctedQuery = this.spellCorrectQuery(query);
				System.out.print("Corrected Query: " + correctedQuery + "\n");
				
				// parse query
				ArrayList<String> tokens = this.tokenizer.parseQuery(correctedQuery);
				ArrayList<String> fileNames = this.rankDocuments(tokens);
				
				// get snippets
				Map<String, String> snippets = SnippetGenerator.generateSnippet(fileNames, correctedQuery);
				
				//update result
				this.result.originalQuery = query;
				this.result.correctedQuery = correctedQuery;
				this.result.snippets = snippets;
				
				System.out.print(this.result.toString());
				System.out.print("\n ");
				System.out.print("\n ");
				System.out.print("\nType another query: ");
			}
			catch(Exception e){
				e.printStackTrace();
				System.out.print("something screwed up");
			}
		}
		
		inputListener.close();
	}
	
	private String spellCorrectQuery(String query){
		
		ArrayList<String> tokens = new ArrayList<String>();
		Scanner reader = new Scanner(query);
		
		// extract all tokens from query
		String token;
		while(reader.hasNext()){
			token = reader.next();
			token = token.toLowerCase();
			token = token.replaceAll("[^A-Za-z0-9]", "");
			
			tokens.add(token);
		}
		// end token extraction
		reader.close();
		
		// start building the corrected query
		StringBuilder correctedQuery = new StringBuilder("");
		
		for(int i = 0; i < tokens.size(); i++){
			String correctedWord = tokens.get(i);
			
			correctedWord = this.spellCorrectWord(correctedWord);
			
			if(i == tokens.size() - 1){
				correctedQuery.append(correctedWord);
			}
			else{
				correctedQuery.append(correctedWord + " ");
			}
		}
		
		return correctedQuery.toString();
	}
	
	private String spellCorrectWord(String e){
		
		if(this.soundex.isCorrect(e)){
			return e;
		}
		
		// initialize set of all suggested corrections S
		ArrayList<String> S = new ArrayList<String>();
		
		// encode the error word e
		String code = this.soundex.encodeString(e);
		
		// for each word found with the matching soundex code of e, do edit distance
		for(String suggestedCorrection : this.soundex.getWords(code)){
			// for any score less than or equal to 2, add it to the suggested corrections
			if(Soundex.LevenShteinDistance(e, suggestedCorrection) <= 2){
				S.add(suggestedCorrection);
			}
		}
		
		// do noisey channel model function right here where 
		String correctedWord = this.noiseyChannelModel.getSuggestedCorrection(e, S);
		
		//update result
		this.result.soundexCodes.add(code);
		this.result.suggestedCorrections.addAll(S);
		
		return correctedWord;
	}
	
	/**
	 * rank all documents that contain either of the tokens from the query
	 * @param tokens
	 */
	private ArrayList<String> rankDocuments(ArrayList<String> tokens){
		TreeMap<String, Double> rankedDocuments = new TreeMap<String, Double>();
		TreeSet<Document> documents = new TreeSet<Document>();
		
		// rank all documents for each query term it contains
		for(String token : tokens){
			Iterator<String> docs = this.tokenizer.getDocIds(token);
			if(docs == null){
				continue;
			}
			while(docs.hasNext()){
				String doc = docs.next();

				double TF = this.TF(token, doc);
				double IDF = this.IDF(token);
				double score = TF * IDF;
				
				if(!rankedDocuments.containsKey(doc)){
					rankedDocuments.put(doc, 0.0);
				}
				double currentScore = rankedDocuments.get(doc);
				currentScore += score;
				rankedDocuments.put(doc, currentScore);
		
			}
		}
		
		// put all ranked documents in ordered set
		for(String doc : rankedDocuments.keySet()){
			double count = rankedDocuments.get(doc);
			documents.add(new Document(doc, count));
		}
		
		// display the results
		StringBuilder result = new StringBuilder("");
		ArrayList<String> docNames = new ArrayList<String>();
		int count = 0;
		for(Document doc : documents){
			if(count == 5){
				break;
			}
			result.append(count + 1 + ". id: " + doc.getDocName() + " ==============> score: " + doc.getScore() + "\n");
			count++;
			docNames.add("Doc (" + doc.getDocName() + ").txt");
		}
		System.out.print(result.toString());
		System.out.print("SIZE: " + rankedDocuments.size() + "\n");
		
		return docNames;
	}

	/**
	 * calculate term frequency
	 * @param word
	 * @param document
	 * @return
	 */
	private double TF(String word, String document){
		double frequency = (double)this.tokenizer.getNumberOfKeyWordsInDocument(document, word);
		return frequency / (double)this.tokenizer.getHighestFrequencyInDoc(document);
	}
	
	/**
	 * calculate the IDF 
	 * @param word
	 * @return
	 */
	private double IDF(String word){
		double totalDocuments = (double)this.tokenizer.getNumberOfDocuments();
		double totalDocumentsForWord = (double)this.tokenizer.getNumberOfDocumentsWithWord(word);
		double amount = (double)Math.log(totalDocuments / totalDocumentsForWord) / Math.log(2.0);
		return amount;
	}
}
