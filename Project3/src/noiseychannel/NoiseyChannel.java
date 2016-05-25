package noiseychannel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import noiseychannel.IncorrectQueryLogWords.IncorrectQueryLogWord;
import noiseychannel.QueryLogWords.QueryLogWord;
import soundex.Soundex;
import util.Tokenizer;

public class NoiseyChannel {
	
	private final String url = "query_logs.txt";
	
	private Tokenizer tokenizer;
	
	private Map<String, QueryLogWords> queryLogWords;
	private Map<String, IncorrectQueryLogWords> incorrectQueryLogWords;
	
	private Soundex soundex;
	
	public NoiseyChannel(Tokenizer _tokenizer, Soundex _soundex){
		this.tokenizer = _tokenizer;
		this.queryLogWords = new TreeMap<String, QueryLogWords>();
		this.soundex = _soundex;
		this.incorrectQueryLogWords = new TreeMap<String, IncorrectQueryLogWords>();
	}
	
	public void setup(){
		File file = new File(url);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null){
				String[] queryLogSession = line.split("\t");
				String sessionId = queryLogSession[0];
				String queryLog = queryLogSession[1];
				
				this.processQueryLog(sessionId, queryLog);
			}
			reader.close();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * set up the noisey channel model the query logs
	 * @param sessionId
	 * @param queryLog
	 */
	private void processQueryLog(String sessionId, String queryLog){
		
		Scanner reader = new Scanner(queryLog);
		
		int pos = 1;
		while(reader.hasNext()){
			String word = reader.next();
			word = word.toLowerCase();
			word = word.replaceAll("[^A-Za-z0-9]", "");
			
			// add in incorrect log words in sessionId based key data structure.
			if(!this.soundex.isCorrect(word)){
				if(!this.incorrectQueryLogWords.containsKey(sessionId)){
					this.incorrectQueryLogWords.put(sessionId, new IncorrectQueryLogWords());
				}
				
				IncorrectQueryLogWords incorrectWords = this.incorrectQueryLogWords.get(sessionId);
				incorrectWords.addIncorrectQueryLogWord(word, pos);
				this.incorrectQueryLogWords.put(sessionId, incorrectWords);
			}
			
			// add any querylog word into the word based key data structure.
			if(!this.queryLogWords.containsKey(word)){
				this.queryLogWords.put(word, new QueryLogWords());
			}
			
			QueryLogWords queryLogs = this.queryLogWords.get(word);
			queryLogs.addQueryLogWord(sessionId, pos);
			pos++;
		}
		reader.close();
	}
	
	public String getSuggestedCorrection(String e, ArrayList<String> S){
		// W*
		String suggestedCorrection = "";
		
		// score
		double score = 0.0;
		
		for(String W : S){
			double newScore = this.getPofEinW(e, W) * this.getPofW(W);
			if(newScore > score){
				score = newScore;
				suggestedCorrection = W;
			}
		}
		
		return suggestedCorrection;
	}
	
	private double getPofEinW(String e, String W){
		// check if e and W are in the query logs, or if e has every been corrected to W
		if(!this.queryLogWords.containsKey(e) ||
				!this.queryLogWords.containsKey(W)){
			return 0.0;
		}
		ArrayList<QueryLogWord> eWords = this.queryLogWords.get(e).getQueryLogWords();
		ArrayList<QueryLogWord> wWords = this.queryLogWords.get(W).getQueryLogWords();
		int countNumerator = 0;
		
		for(QueryLogWord eWord : eWords){
			for(QueryLogWord wWord : wWords){
				if(eWord.sessionId.equals(wWord.sessionId)
						&& eWord.pos == wWord.pos){
					countNumerator++;
				}
			}
		}
		
		int countDenominator = 0;
		for(QueryLogWord wWord : wWords){
			// check to see if the session id of W exists in the incorrect query log words
			if(!this.incorrectQueryLogWords.containsKey(wWord.sessionId)){
				continue;
			}
			ArrayList<IncorrectQueryLogWord> incorrectWords = this.incorrectQueryLogWords.get(wWord.sessionId).getIncorrectQueryLogWords();
			for(IncorrectQueryLogWord incorrectWord : incorrectWords){
				if(wWord.pos == incorrectWord.pos){
					countDenominator++;
				}
			}
		}
		return (double)countNumerator / (double)countDenominator;
	}
	
	private double getPofW(String W){
		return (double)tokenizer.getWordCollectionFrequency(W) / (double)tokenizer.getCollectionFrequency();
	}
}
