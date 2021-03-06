package model;

public class Term {
	private StringBuilder term;
	private long modFreq;
	private long frequency;
	
	public Term(StringBuilder _term, long _modFreq, long _frequency){
		this.term = _term;
		this.modFreq = _modFreq;
		this.frequency = _frequency;
	}
	
	public void append(String str){
		this.term.append(str);
	}
	
	public void insert(int position, String str){
		this.term.insert(position, str);
	}
	
	@Override
	public String toString(){
		return this.term.toString();
	}

	public long getModFreq() {
		return modFreq;
	}

	public long getFrequency() {
		return frequency;
	}
}
