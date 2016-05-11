package webqs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import util.Tokenizer;
import containers.TrieSQ;

public class WebQS {
	private TrieSQ trie;
	private Map<String, Integer> modFreq;
	
	private static final String uri = "logs";
	
	public WebQS(){
		this.trie = new TrieSQ();
		this.modFreq = new TreeMap<String, Integer>();
	}
	
	public void init(){
		System.out.print("Loading Query Logs...");
		try{
			File[] files = new File(uri).listFiles();
			for(File file : files){
				this.parseFile(file);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.print("Error Occurred");
		}
		finally{
			System.out.print("Finished");
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
			System.out.print("File not found");
		}
		catch (IOException e) {
			System.out.print("IO exception thrown");
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
		
	}
}
