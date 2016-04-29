package model;

@SuppressWarnings("rawtypes")
public class Document implements Comparable{
	private String docName;
	private double score;
	
	public Document(){
		this.docName = "";
		this.score = 0.0;
	}
	
	public Document(String docName, double score){
		this.docName = docName;
		this.score = score;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}
	
	@Override
	public int compareTo(Object obj){
		Document doc = (Document)obj;
		if(this.score == doc.score){
			return 1;
		}
		else if(this.score > doc.score){
			return -1;
		}
		return 1;
	}
	
	@Override
	public boolean equals(Object obj){
		Document doc = (Document)obj;
		return this.docName.equals(doc.getDocName());
	}
}
