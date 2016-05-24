package searchengine;

import java.io.File;
import exception.TokenizerException;
import soundex.Soundex;
import util.Tokenizer;

public class SearchEngine {
		
	private static final String uri = "wiki" + File.separator + "To_be_posted";
	
	private Soundex soundex;
	private Tokenizer tokenizer;
	
	public SearchEngine(){
		this.soundex = new Soundex();
		try {
			this.tokenizer = new Tokenizer();
		} 
		catch (TokenizerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void init(){
		System.out.print("Perparing soundex... \n");
		this.soundex.buildDictionary();
		
		System.out.print("Perparing wiki... \n");
		
		// recursively load all file documents into the index
		try{
			File[] files = new File(uri).listFiles();
			for(File file : files){
				tokenizer.parse(file);
			}
		}
		catch(TokenizerException e){
			e.printStackTrace();
		}
		
		System.out.print("finished! \n");
	}
	
	public void run(){
		
	}
}