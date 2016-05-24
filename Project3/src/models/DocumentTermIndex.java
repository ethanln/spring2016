package models;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class DocumentTermIndex implements Iterable<String>{

	private Map<String, KeyWords> documentTerm;
	
	public DocumentTermIndex(){
		documentTerm = new TreeMap<String, KeyWords>();
	}
	
	public void addKeyTerm(String keyWord, String documentId){
		if(!this.documentTerm.containsKey(documentId)){
			this.documentTerm.put(documentId, new KeyWords());
		}
		
		KeyWords keywords = this.documentTerm.get(documentId);
		keywords.incrementFrequency(keyWord);
		this.documentTerm.put(documentId, keywords);
	}
	
	public String getMostFrequentWord(String docId){
		if(this.documentTerm.containsKey(docId)){
			return this.documentTerm.get(docId).getMostFrequentWord();
		}
		return "none";
	}
	
	public int getHighestFrequency(String docId){
		if(this.documentTerm.containsKey(docId)){
			return this.documentTerm.get(docId).getHighestFrequency();
		}
		return 0;
	}
	
	public int size(){
		return this.documentTerm.size();
	}
	
	@Override
    public Iterator<String> iterator() {
        return documentTerm.keySet().iterator();
    }
}
