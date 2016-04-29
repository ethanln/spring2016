package solution;

import exception.SearchEngineException;
import searchengine.SearchEngine;

public class SearchEngineRunner {
	public static void main(String[] args){
		try{
			SearchEngine engine = new SearchEngine();
			engine.run();
		}
		catch(SearchEngineException e){
			System.out.print("search engine failed");
		}
		
	}
}
