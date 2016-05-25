package searchengine;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import exception.TokenizerException;
import noiseychannel.NoiseyChannel;
import soundex.Soundex;
import util.Tokenizer;

public class SearchEngine {
		
	private static final String uri = "wiki" + File.separator + "To_be_posted";
	
	private Soundex soundex;
	private Tokenizer tokenizer;
	private NoiseyChannel noiseyChannelModel;
	
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
		
		System.out.print("Perparing noisey channel model... \n");
		this.noiseyChannelModel = new NoiseyChannel(tokenizer, soundex);
		this.noiseyChannelModel.setup();
		
		System.out.print("finished! \n");
	}
	
	public void run(){
		Scanner inputListener = new Scanner(System.in);
		System.out.print("Type in a query: ");
		while(true){
			try{
				String query = inputListener.nextLine();
				
				System.out.print(query + "\n");
				
				if(query.equals("q")){
					break;
				}
				
				String correctedQuery = this.spellCorrectQuery(query);
				// WORK HERE TODO:
				System.out.print("\nType another query: ");
			}
			catch(Exception e){
				e.printStackTrace();
				System.out.print("something screwed up");
			}
		}
		
		inputListener.close();
	}
	
	private String spellCorrectQuery(String query){
		
		ArrayList<String> tokens = new ArrayList<String>();
		Scanner reader = new Scanner(query);
		
		// extract all tokens from query
		String token;
		while(reader.hasNext()){
			token = reader.next();
			token = token.toLowerCase();
			token = token.replaceAll("[^A-Za-z0-9]", "");
			
			tokens.add(token);
		}
		// end token extraction
		reader.close();
		
		// start building the corrected query
		StringBuilder correctedQuery = new StringBuilder("");
		
		for(int i = 0; i < tokens.size(); i++){
			String correctedWord = tokens.get(i);
			
			correctedWord = this.spellCorrectWord(correctedWord);
			
			if(i == tokens.size() - 1){
				correctedQuery.append(correctedWord);
			}
			else{
				correctedQuery.append(correctedWord + " ");
			}
		}
		
		// CHECK AND SEE WHY movi gives different suggestions
		return correctedQuery.toString();
	}
	
	private String spellCorrectWord(String e){
		
		if(this.soundex.isCorrect(e)){
			return e;
		}
		
		// initialize set of all suggested corrections S
		ArrayList<String> S = new ArrayList<String>();
		
		// encode the error word e
		String code = this.soundex.encodeString(e);
		
		// for each word found with the matching soundex code of e, do edit distance
		for(String suggestedCorrection : this.soundex.getWords(code)){
			// for any score less than or equal to 2, add it to the suggested corrections
			if(Soundex.LevenShteinDistance(e, suggestedCorrection) <= 2){
				S.add(suggestedCorrection);
			}
		}
		
		// do noisey channel model function right here where 
		String correctedWord = this.noiseyChannelModel.getSuggestedCorrection(e, S);
		
		return correctedWord;
	}
}
