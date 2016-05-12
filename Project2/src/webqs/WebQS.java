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
			
			//String query1 = query;
			String[] query1Tokenized = query.split(" ");
			
			for(int i = 0; i < suggestedQueries.size(); i++){
				//System.out.print(term.toString() + " " + term.getModFreq() + " " + term.getFrequency() + "\n");
				//String query2 = query + suggestedQueries.get(i).toString();
				String query2 = suggestedQueries.get(i).toString();
				String[] query2Tokenized = query2.split(" ");
				
				if(query1Tokenized.length < query2Tokenized.length){
					//////
					String query1TokenStemmed;
					String query2TokenStemmed;
					
					double wcf;
					
					try{
						query1TokenStemmed = this.stemmer.stem(query1Tokenized[query1Tokenized.length - 1]);
						query2TokenStemmed = this.stemmer.stem(query2Tokenized[0]);
						
						if(query1TokenStemmed.equals("Invalid term") 
								|| query1TokenStemmed.equals("No term entered")
								|| query2TokenStemmed.equals("Invalid term") 
								|| query2TokenStemmed.equals("No term entered")){
							wcf = 0.0;
						}
						else{
							wcf = this.WCF(query1TokenStemmed, query2TokenStemmed);
						}
					}
					catch(Exception e){
						wcf = 0.0;
					}
					///////
					//double wcf = this.WCF(query1TokenStemmed, query2TokenStemmed);
					double freq = (double)suggestedQueries.get(i).getFrequency() / (double)this.trie.getHighestFrequency();
					double mod = (double)suggestedQueries.get(i).getModFreq() / (double)this.trie.getHighestModFrequency();
					
					
					if(freq == 0){
						System.out.println("what the???");
					}
					
					// calculate score.
					double min = Math.min(freq, mod);
					min = Math.min(min, wcf);
					
					double score = (wcf + freq + mod) / (1 - min);
					this.topSuggestedQueries.add(new SuggestedQuery(query + " " + query2, score));
				}
				
				//query1 = query2;
				//query1Tokenized = query2Tokenized;
			}
		}
	}
	
	private double WCF(String w1, String w2){
		try{

			String myURL = "http://peacock.cs.byu.edu/CS453Proj2/?word1="+w1+"&word2="+w2;

			System.out.println("Fetching content: "+myURL);

			Document pageDoc = (Document) Jsoup.connect(myURL).get();
			String htmlContent = pageDoc.html();		
			Document contentDoc = Jsoup.parse(htmlContent);
			String contentVal = contentDoc.body().text();
			
			//System.out.println(contentVal);

			Double val= Double.parseDouble(contentVal);

			//System.out.println(val);
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
			if(count == 10){
				break;
			}
			count++;
			
		}
		this.topSuggestedQueries = new TreeSet<SuggestedQuery>();
	}
}