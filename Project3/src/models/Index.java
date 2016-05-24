package models;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Index {
	private Map<String, Integer> index;
	private String docWithMostFrequency;
	private int highestFrequency;
	
	public Index(){
		this.index = new TreeMap<String, Integer>();
		this.docWithMostFrequency = "";
		this.highestFrequency = 0;
	}
	
	public void incrementFrequency(String documentId){
		if(!this.index.containsKey(documentId)){
			this.index.put(documentId, 0);
		}
		
		int count = this.index.get(documentId);
		count++;
		
		if(count > this.highestFrequency){
			this.highestFrequency = count;
			this.docWithMostFrequency = documentId;
		}
		this.index.put(documentId, count);
	}
	
	public String getDocWithMostFrequency(){
		return this.docWithMostFrequency;
	}
	
	public int getHighestFrequency(){
		return this.highestFrequency;
	}
	
	public int getNumberOfDocumentsWithWord(){
		return this.index.size();
	}
	
	public Set<String> getAllCorrelatedDocuments(){
		return this.index.keySet();
	}
	
	public int getNumberOfKeyWordsInDocument(String docId){
		if(this.index.containsKey(docId)){
			return this.index.get(docId);
		}
		return 0;
	}
}
