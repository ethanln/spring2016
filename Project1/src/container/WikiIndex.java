package container;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class WikiIndex implements Iterable<String>{

	private Map<String, Index> wiki;
	
	public WikiIndex(){
		wiki = new TreeMap<String, Index>();
	}
	
	public void addKeyTerm(String keyWord, String documentId){
		if(!this.wiki.containsKey(keyWord)){
			this.wiki.put(keyWord, new Index());
		}
		
		Index index = this.wiki.get(keyWord);
		index.incrementFrequency(documentId);
		this.wiki.put(keyWord, index);
	}
	
	public String getDocWithMostFrequency(String word){
		if(this.wiki.containsKey(word)){
			return this.wiki.get(word).getDocWithMostFrequency();
		}
		return "none";
	}
	
	public int getHighestFrequency(String word){
		if(this.wiki.containsKey(word)){
			return this.wiki.get(word).getHighestFrequency();
		}
		return 0;
	}
	
	public int getNumberOfDocumentsWithWord(String word){
		if(this.wiki.containsKey(word)){
			return this.wiki.get(word).getNumberOfDocumentsWithWord();
		}
		return 0;
	}
	
	public int getNumberOfKeyWordsInDocument(String word, String docId){
		if(this.wiki.containsKey(word)){
			return this.wiki.get(word).getNumberOfKeyWordsInDocument(docId);
		}
		return 0;
	}
	
	
	@Override
    public Iterator<String> iterator() {
        return wiki.keySet().iterator();
    }
}
