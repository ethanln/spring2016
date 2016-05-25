package noiseychannel;

import java.util.ArrayList;

public class IncorrectQueryLogWords {
	private ArrayList<IncorrectQueryLogWord> incorrectQueryLogWords;
	
	public IncorrectQueryLogWords(){
		this.incorrectQueryLogWords = new ArrayList<IncorrectQueryLogWord>();
	}
	
	public ArrayList<IncorrectQueryLogWord> getIncorrectQueryLogWords(){
		return this.incorrectQueryLogWords;
	}
	
	public void addIncorrectQueryLogWord(String word, int pos){
		this.incorrectQueryLogWords.add(new IncorrectQueryLogWord(word, pos));
	}
	
	
	public class IncorrectQueryLogWord{
		
		public String word;
		public int pos;
		
		public IncorrectQueryLogWord(String _word, int _pos){
			this.word = _word;
			this.pos = _pos;
		}
	}
}
