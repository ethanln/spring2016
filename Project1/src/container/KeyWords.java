package container;

import java.util.Map;
import java.util.TreeMap;

public class KeyWords{

	private Map<String, Integer> keywords;
	private String mostFrequentWord;
	private int highestFrequency;
	
	public KeyWords(){
		this.keywords = new TreeMap<String, Integer>();
		this.mostFrequentWord = "";
		this.highestFrequency = 0;
	}
	
	public void incrementFrequency(String keyword){
		if(!this.keywords.containsKey(keyword)){
			this.keywords.put(keyword, 0);
		}
		
		int count = this.keywords.get(keyword);
		count++;
		
		if(count > this.highestFrequency){
			this.highestFrequency = count;
			this.mostFrequentWord = keyword;
		}
		this.keywords.put(keyword, count);
	}
	
	public String getMostFrequentWord(){
		return this.mostFrequentWord;
	}
	
	public int getHighestFrequency(){
		return this.highestFrequency;
	}
}
