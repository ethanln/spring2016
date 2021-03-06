package models;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Result {
	public String originalQuery;
	public String correctedQuery;
	public ArrayList<String> soundexCodes;
	public ArrayList<String> suggestedCorrections;
	public Map<String, String> snippets;
	
	public Result(){
		this.soundexCodes = new ArrayList<String>();
		this.suggestedCorrections = new ArrayList<String>();
		this.snippets = new TreeMap<String, String>();
	}
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder("");
		
		builder.append("Original Query: " + this.originalQuery + "\n");
		builder.append("Corrected Query: " + this.correctedQuery + "\n");
		
		builder.append("Soundex Code: ");
		
		for(String soundexCode : this.soundexCodes){
			builder.append(soundexCode + ", ");
		}
		builder.append("\n");
		
		builder.append("Suggested Corrections: ");
		
		for(String suggestedCorrection : this.suggestedCorrections){
			builder.append(suggestedCorrection + ", ");
		}
		builder.append("\n");
		
		for(String docId : this.snippets.keySet()){
			builder.append(docId);
			builder.append("\n");
			builder.append("\n");
			builder.append(this.snippets.get(docId));
			builder.append("\n");
			builder.append("\n");
		}
		return builder.toString();	
	}
}
