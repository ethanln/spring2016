package models;

@SuppressWarnings("rawtypes")
public class Sentence implements Comparable{
	
	// raw sentence
	public String sentence;
	
	// v) is first sentence
	public boolean isFirst;
	
	// v) is second sentence
	public boolean isSecond;
	
	// i) density measure of query words (significance factor
	public double densityMeasure;
	
	// ii) the longest contiguous run of query words in the sentence
	public double longestContiguousRunOfQueryTerms;
	
	// iii) the number of unique query terms in sentence
	public int numberOfUniqueQueryTerms;
	
	// iv) total number of query terms occurring in the sentence
	public int totalNumberOfQueryTerms;
	
	public Sentence(String _sentence){
		this.sentence = _sentence;
		
		this.isFirst = false;
		this.isSecond = false;
		
		this.densityMeasure = 0.0;
		this.longestContiguousRunOfQueryTerms = 0.0;
		this.numberOfUniqueQueryTerms = 0;
		this.totalNumberOfQueryTerms = 0;
	}	
	
	public double calculateScore(){
		double score = 0.0;
		
		score += this.isFirst ? 2.0 : 0.0;
		score += this.isSecond ? 1.0 : 0.0;
		
		score += this.densityMeasure;
		score += this.longestContiguousRunOfQueryTerms;
		score += (double)this.numberOfUniqueQueryTerms;
		score += (double)this.totalNumberOfQueryTerms;
		
		return score;
	}

	@Override
	public int compareTo(Object arg0) {
		Sentence obj = (Sentence)arg0;
		
		if(this.calculateScore() == obj.calculateScore()){
			return 1;
		}
		else if(this.calculateScore() > obj.calculateScore()){
			return -1;
		}
		return 1;
	}
}
