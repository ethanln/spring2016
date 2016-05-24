package models;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class TermDocumentIndex implements Iterable<String>{

	private Map<String, Index> termDocument;
	
	public TermDocumentIndex(){
		termDocument = new TreeMap<String, Index>();
	}
	
	public void addKeyTerm(String keyWord, String documentId){
		if(!this.termDocument.containsKey(keyWord)){
			this.termDocument.put(keyWord, new Index());
		}
		
		Index index = this.termDocument.get(keyWord);
		index.incrementFrequency(documentId);
		this.termDocument.put(keyWord, index);
	}
	
	public String getDocWithMostFrequency(String keyWord){
		if(this.termDocument.containsKey(keyWord)){
			return this.termDocument.get(keyWord).getDocWithMostFrequency();
		}
		return "none";
	}
	
	public int getHighestFrequency(String keyWord){
		if(this.termDocument.containsKey(keyWord)){
			return this.termDocument.get(keyWord).getHighestFrequency();
		}
		return 0;
	}
	
	public int getNumberOfDocumentsWithWord(String keyWord){
		if(this.termDocument.containsKey(keyWord)){
			return this.termDocument.get(keyWord).getNumberOfDocumentsWithWord();
		}
		return 0;
	}
	
	public int getNumberOfKeyWordsInDocument(String keyWord, String docId){
		if(this.termDocument.containsKey(keyWord)){
			return this.termDocument.get(keyWord).getNumberOfKeyWordsInDocument(docId);
		}
		return 0;
	}
	
	public Iterator<String> getAllCorrelatedDocuments(String keyWord){
		if(this.termDocument.containsKey(keyWord)){
			Set<String> documents = this.termDocument.get(keyWord).getAllCorrelatedDocuments();
			return documents.iterator();
		}
		return null;	
	}
	
	@Override
    public Iterator<String> iterator() {
        return termDocument.keySet().iterator();
    }
}
