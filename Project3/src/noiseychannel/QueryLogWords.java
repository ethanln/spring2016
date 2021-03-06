package noiseychannel;

import java.util.ArrayList;

public class QueryLogWords {
	private ArrayList<QueryLogWord> queryLogWords;
	
	public QueryLogWords(){
		this.queryLogWords = new ArrayList<QueryLogWord>();
	}
	
	public ArrayList<QueryLogWord> getQueryLogWords(){
		return this.queryLogWords;
	}
	
	public void addQueryLogWord(String sessionId, int pos){
		this.queryLogWords.add(new QueryLogWord(sessionId, pos));
	}
	
	public class QueryLogWord{
		
		public String sessionId;
		public int pos;
		
		public QueryLogWord(String _sessionId, int _pos){
			this.sessionId = _sessionId;
			this.pos = _pos;
		}
	}
}
