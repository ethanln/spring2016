package util;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class SnippetGenerator {
	
	private static SnippetGenerator inst;
	
	private SnippetGenerator(){}
	
	private static SnippetGenerator instance(){
		if(inst == null){
			inst = new SnippetGenerator();
		}
		return inst;
	}
	
	public static Map<String, String> generateSnippet(ArrayList<String> fileNames){
		return instance()._generateSnippet(fileNames);
	}
	
	private Map<String, String> _generateSnippet(ArrayList<String> fileNames){
		for(String fileName : fileNames){
			File file = new File(fileName);
			//TODO: implement right here
		}
	
		return new TreeMap<String, String>();
	}
	
	private void densityMeasure(File file){
		
	}
	
	private void longestContiguousRun(File file){
		
	}
	
	private void numberOfUniqueQueryTerms(File file){
		
	}
	
	private void totalNumberOfQueryTerms(File file){
		
	}
	
	private void firstOrSecondLine(File file){
		
	}
	
	private void heading(File file){
		
	}
}
