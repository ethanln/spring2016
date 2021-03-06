package webqs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import util.PorterStemmer;
import util.Tokenizer;
import containers.TrieSQ;
import model.SuggestedQuery;
import model.Term;

public class WebQS {
	private TrieSQ trie;
	private Map<String, Integer> modFreq;
	
	private Set<SuggestedQuery> topSuggestedQueries;
	
	private PorterStemmer stemmer;
	
	private static final String uri = "logs";
	
	public WebQS(){
		this.trie = new TrieSQ();
		this.modFreq = new TreeMap<String, Integer>();
		
		this.topSuggestedQueries = new TreeSet<SuggestedQuery>();
		
		this.stemmer = new PorterStemmer();
	}
	
	public void init(){
		System.out.print("Loading Query Logs... \n");
		try{
			File[] files = new File(uri).listFiles();
			for(File file : files){
				this.parseFile(file);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.print("Error Occurred \n");
		}
		finally{
			System.out.print("Finished \n");
		}
	}
	
	private void parseFile(File logFile){

		try {
			BufferedReader wikiReader = new BufferedReader(new FileReader(logFile));
			
			// get rid of the first line
			wikiReader.readLine();
			
			String line1 = wikiReader.readLine();
			String line2;
			
			if(line1 == null){
				wikiReader.close();
				return;
			}
			
			// insert line1 into trie
			String[] line1Tokens = line1.split("\t");
			trie.insert((line1Tokens[1]), false);
			
			while ((line2 = wikiReader.readLine()) != null){
				boolean isMod = false;
				
				// test to see if the string has been modified
				isMod = this.mod(line1, line2);
				String[] line2Tokens = line2.split("\t");
				
				// insert line 2 into the trie
				trie.insert((line2Tokens[1]), isMod);
				
				if(isMod){
					// update modification frequency in the map
					if(!this.modFreq.containsKey(line2Tokens[1])){
						this.modFreq.put(line2Tokens[1], 0);
					}
					
					// increment modification 
					int modCount = this.modFreq.get(line2Tokens[1]);
					modCount++;
					this.modFreq.put(line2Tokens[1], modCount);
				}
								
				line1 = line2;
			}
			
			wikiReader.close();
		} 
		catch (FileNotFoundException e) {
			System.out.print("File not found \n");
		}
		catch (IOException e) {
			System.out.print("IO exception thrown \n");
		}
	}
	
	private boolean mod(String Q, String QS){
		String[] QTokens = Q.split("\t");
		String[] QSTokens = QS.split("\t");
		
		String sessionIdQ = QTokens[0];
		String sessionIdQS = QSTokens[0];
		
		String QDate = QTokens[2];
		String QSDate = QSTokens[2];
		
		ArrayList<String> QLineTokens = (ArrayList<String>)Tokenizer.parseQuery(QTokens[1]);
		ArrayList<String> QSLineTokens = (ArrayList<String>)Tokenizer.parseQuery(QSTokens[1]);
		
		// set up time formatter
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
		
		// get time difference between logs
		DateTime dateQ = formatter.parseDateTime(QDate);
		DateTime dateQS = formatter.parseDateTime(QSDate);
		Minutes minutes = Minutes.minutesBetween(dateQ, dateQS);
		
		/*
		 * query sessions must be the same
		 * queries must be 10 minutes within each other
		 * both queries must at least have one word
		 * first word of both queries must be equal
		 * and both queries must not be equal over all
		 */
		if(sessionIdQ.equals(sessionIdQS)
				&& minutes.getMinutes() <= 10
				&& QLineTokens.size() > 0 
				&& QSLineTokens.size() > 0
				&& QLineTokens.get(0).equals(QSLineTokens.get(0))
				&& !QTokens[1].equals(QSTokens[1])){
			return true;
		}
		return false;
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
				if((query.toCharArray()[query.length() - 1] != '\t') && (query.toCharArray()[query.length() - 1] != ' ')){
					System.out.print("\n Not a complete word, type another query: ");
					continue;
				}
				
				Scanner reader = new Scanner(query);
				StringBuilder queryFormatted = new StringBuilder("");
				while(reader.hasNext()){
					String token = reader.next();
					queryFormatted.append(token);
					if(reader.hasNext()){
						queryFormatted.append(" ");
					}
				}
				
				reader.close();
				
				this.getSQ(queryFormatted.toString());
				this.printResult();
				System.out.print("\nType another query: ");
			}
			catch(Exception e){
				e.printStackTrace();
				System.out.print("\n something screwed up \n");
			}
		}
		
		inputListener.close();
	}
	
	private void getSQ(String query){
		ArrayList<Term> suggestedQueries = (ArrayList<Term>)this.trie.getSuggestedQueries(query);
		
		if(suggestedQueries.size() > 0){
			String[] query1Tokenized = query.split(" ");
			
			// for each suggested query
			for(int i = 0; i < suggestedQueries.size(); i++){
				
				// tokenize the suggested query
				String query2 = suggestedQueries.get(i).toString();
				String[] query2Tokenized = query2.split(" ");
				
				if(query2Tokenized.length > 0){
					double wcf;
					String word1Token = query1Tokenized[query1Tokenized.length - 1];
					String word2Token = query2Tokenized[0];
					
					// get the wcf score
					try{
						// stem both words
						String word1TokenStemmed = this.stemmer.stem(word1Token);
						String word2TokenStemmed = this.stemmer.stem(word2Token);
						
						// if word1 does not stem properly
						if(word1TokenStemmed.equals("Invalid term") 
								|| word1TokenStemmed.equals("No term entered")){
							word1TokenStemmed = word1Token;
						}
						
						// if word2 does not stem properly
						if(word2TokenStemmed.equals("Invalid term") 
								|| word2TokenStemmed.equals("No term entered")){
							word2TokenStemmed = word2Token;
						}
						
						// get wcf score
						wcf = this.WCF(word1TokenStemmed, word2TokenStemmed);	
					}
					catch(Exception e){
						wcf = this.WCF(word1Token, word2Token);
					}
					
					// normalize wcf
					if(wcf > 0){
						wcf = wcf/0.001;
					}
					
					// get freq score with normalization
					double freq = (double)suggestedQueries.get(i).getFrequency() / (double)this.trie.getHighestFrequency();
					// get modfreq score with normalization
					double mod = (double)suggestedQueries.get(i).getModFreq() / (double)this.trie.getHighestModFrequency();
								
					// calculate score.
					double min = Math.min(freq, mod);
					min = Math.min(min, wcf);
					
					double score = (wcf + freq + mod) / (1 - min);
					this.topSuggestedQueries.add(new SuggestedQuery(query + " " + query2, score));
				}
			}
		}
	}
	
	private double WCF(String w1, String w2){
		try{

			String myURL = "http://peacock.cs.byu.edu/CS453Proj2/?word1="+w1+"&word2="+w2;

			Document pageDoc = (Document) Jsoup.connect(myURL).get();
			String htmlContent = pageDoc.html();		
			Document contentDoc = Jsoup.parse(htmlContent);
			String contentVal = contentDoc.body().text();

			System.out.print(myURL);
			Double val= Double.parseDouble(contentVal);

			if(val == -1.0){
				return 0.0;
			}
			return val;

		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return 0.0;
	}
	
	private void printResult(){
		int count = 1;
		for(SuggestedQuery sq : this.topSuggestedQueries){
			System.out.print(count + ". " + sq.SQ + " =======> " + sq.score + "\n");
			if(count == 8){
				break;
			}
			count++;
		}
		this.topSuggestedQueries = new TreeSet<SuggestedQuery>();
	}
}
