package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import models.Sentence;

public class SnippetGenerator {
	
	private static SnippetGenerator inst;
	private final String uri = "wiki" + File.separator + "To_be_posted" + File.separator;
	
	private Set<String> queryTermsDictionary;
	
	private SnippetGenerator(){
	}
	
	private static SnippetGenerator instance(){
		if(inst == null){
			inst = new SnippetGenerator();
		}
		return inst;
	}
	
	public static Map<String, String> generateSnippet(ArrayList<String> fileNames, String query){
		return instance()._generateSnippet(fileNames, query);
	}
	
	private TreeMap<String, String> _generateSnippet(ArrayList<String> fileNames, String query){
		// parse query
		ArrayList<String> queryTerms = ParserService.parseQuery(query);
		
		// set up query term dictionary
		this.queryTermsDictionary = new TreeSet<String>();
		for(String queryTerm : queryTerms){
			queryTermsDictionary.add(queryTerm);
		}

		TreeMap<String, String> snippets = new TreeMap<String, String>();
		
		// parse through each file
		for(String fileName : fileNames){
			File file = new File(this.uri + fileName);
			ArrayList<Sentence> sentences = parseSentences(file);
			
			// get sentence scores
			densityMeasure(sentences);
			longestContiguousRun(sentences);
			numberOfUniqueQueryTerms(sentences);
			totalNumberOfQueryTerms(sentences);
			
			// evaluate sentence scores
			String snippet = evaluateSentenceScores(sentences);
			snippets.put(fileName, snippet);
		}
		
		this.queryTermsDictionary = new TreeSet<String>();
		return snippets;
	}
	
	/**
	 * get all sentences out of file document
	 * @param file
	 * @return
	 */
	private ArrayList<Sentence> parseSentences(File file){
		BufferedReader reader = null;
		StringBuilder builder = new StringBuilder("");
		try {
			reader = new BufferedReader(new FileReader(file));
			String lineToken;
			while ((lineToken = reader.readLine()) != null){
				builder.append(lineToken + " ");
			}
			reader.close();
		} 
		catch (FileNotFoundException e) {

		}
		catch (IOException e) {

		}
		
		ArrayList<Sentence> parsedSentences = new ArrayList<Sentence>();
		String[] sentences = builder.toString().split("(?<=[a-z])\\.\\s+");
		
		int count = 1;
		for(String sentence : sentences){
			Sentence parsedSentence = new Sentence(sentence);
			
			if(count == 1){
				parsedSentence.isFirst = true;
			}
			else if(count == 2){
				parsedSentence.isSecond = true;
			}
			
			parsedSentences.add(parsedSentence);
			count++;
		}
		
		return parsedSentences;
	}
	
	/**
	 *  i) measure density
	 * @param sentences
	 */
	private void densityMeasure(ArrayList<Sentence> sentences){
		for(int i = 0; i < sentences.size(); i++){	
			ArrayList<String> words = ParserService.parseQuery(sentences.get(i).sentence);
			double significantFactor = this.getSignificanceFactor(words);
			sentences.get(i).densityMeasure = significantFactor;
		}
	}
	
	private double getSignificanceFactor(ArrayList<String> words){
		double highestSignificantFactor = 0.0;
		
		for(int i = 0; i < words.size(); i++){
			if(!this.queryTermsDictionary.contains(words.get(i))){
				continue;
			}
			
			int significantCount = 0;
			int insignificantCount = 0;
			for(int j = i; j < words.size(); j++){
				if(this.queryTermsDictionary.contains(words.get(j))){
					significantCount++;
				}
				else{
					insignificantCount++;
				}
				
				if(insignificantCount >= 4){
					break;
				}
			}
			
			double significantFactor = Math.pow((double)significantCount, 2.0) / ((double)significantCount + (double)insignificantCount);
			if(significantCount > highestSignificantFactor){
				highestSignificantFactor = significantFactor;
			}
		}
		
		return highestSignificantFactor;
	}
	
	/**
	 * ii) longest contiguous run of query terms in sentence
	 * @param sentences
	 */
	private void longestContiguousRun(ArrayList<Sentence> sentences){
		for(int i = 0; i < sentences.size(); i++){	
			ArrayList<String> words = ParserService.parseQuery(sentences.get(i).sentence);
			double longestContiguousValue = this.getLongestContiguousValue(words);
			sentences.get(i).longestContiguousRunOfQueryTerms = longestContiguousValue;
		}
	}
	
	private double getLongestContiguousValue(ArrayList<String> words){
		int highestCount = 0;
		int count = 0;

		for(int i = 0; i < words.size(); i++){
			if(this.queryTermsDictionary.contains(words.get(i))){
				count++;
			}
			else{
				if(count > highestCount){
					highestCount = count;
				}
				count = 0;
			}
		}
		return (double)highestCount / (double)words.size();
	}
	
	/**
	 * iii)
	 * @param sentences
	 */
	private void numberOfUniqueQueryTerms(ArrayList<Sentence> sentences){
		for(int i = 0; i < sentences.size(); i++){	
			ArrayList<String> words = ParserService.parseQuery(sentences.get(i).sentence);
			int numberOfUniqueWords = this.getNumberOfUniqueWords(words);
			sentences.get(i).numberOfUniqueQueryTerms = numberOfUniqueWords;
		}
	}
	
	private int getNumberOfUniqueWords(ArrayList<String> words){
		TreeSet<String> alreadyDiscoveredWords = new TreeSet<String>();
		int count = 0;
		
		for(String word : words){
			if(this.queryTermsDictionary.contains(word)
					&& !alreadyDiscoveredWords.contains(word)){
				alreadyDiscoveredWords.add(word);
				count++;
			}
		}
		return count;
	}
	
	/**
	 * iv)
	 * @param sentences
	 */
	private void totalNumberOfQueryTerms(ArrayList<Sentence> sentences){
		for(int i = 0; i < sentences.size(); i++){	
			ArrayList<String> words = ParserService.parseQuery(sentences.get(i).sentence);
			int totalNumberOfQueryTerms = this.getTotalNumberOfQueryTerms(words);
			sentences.get(i).totalNumberOfQueryTerms = totalNumberOfQueryTerms;
		}
	}
	
	private int getTotalNumberOfQueryTerms(ArrayList<String> words){
		int count = 0;
		for(String word : words){
			if(this.queryTermsDictionary.contains(word)){
				count++;
			}
		}
		return count;
	}
	
	private String evaluateSentenceScores(ArrayList<Sentence> sentences){
		TreeSet<Sentence> orderedSentences = new TreeSet<Sentence>();
		for(Sentence sentence : sentences){
			orderedSentences.add(sentence);
		}
		
		StringBuilder builder = new StringBuilder();
		int count = 0; 
		for(Sentence sentence : orderedSentences){
			if(sentence.sentence.charAt(sentence.sentence.length() - 1) != '.'){
				sentence.sentence += ".";
			}
			if(count == 1){
				builder.append(sentence.sentence);
			}
			else{
				builder.append(sentence.sentence + "\n");
			}
			
			count++;
			if(count == 2){
				break;
			}
		}
		
		return builder.toString();
	}
}
