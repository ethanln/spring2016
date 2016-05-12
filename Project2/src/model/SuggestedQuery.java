package model;

@SuppressWarnings("rawtypes")
public class SuggestedQuery implements Comparable{
	public String SQ;
	public double score;
	
	public SuggestedQuery(String _SQ, double _score){
		this.SQ = _SQ;
		this.score = _score;
	}

	@Override
	public int compareTo(Object obj) {
		SuggestedQuery sq = (SuggestedQuery)obj;
		if(this.score == sq.score){
			return 1;
		}
		else if(this.score > sq.score){
			return -1;
		}
		return 1;
	}
}
