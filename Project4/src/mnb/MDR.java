package mnb;

public class MDR{
	// Map<DocId, MRDRow>
	/*private Map<String, MDRRow> matrix;

	public MDR(){
		this.matrix = new TreeMap<String, MDRRow>();
	}

	public addDoc(String docId, String class){
		this.matrix.put(docId, new MDRRow(class));
	}

	public void addTerm(String docId, String term){
		for(String key : this.matrix.getKeySet()){
			MDRRow row = this.matrix.get(key);
			row.addTerm(term);

			this.matrix.put(key, row);
		}

		MDRRow row = this.matrix.get(docId);
		row.incrementTermFrequency(term);
		this.matrix.put(docId, row);
	}

	public MDRRow getRow(String docId){
		return this.matrix.get(docId);
	}

	private class MDRRow{
		// Map<term, frequency>
		private Map<String, Integer> termFrequency;
		private String class;

		public MDRRow(String _class){
			this.class = _class;
			this.termFrequency = new TreeMap<String, Integer>();
		}

		public void addTerm(String term){
		 	if(!this.termFrequency.containsKey(term)){
		 		this.termFrequency.put(term, 0);
		 	}
		}

		public void incrementTermFrequency(String term){
		 	int count = this.termFrequency.get(term);
		 	count++;
		 	this.termFrequency.put(term, count);
		}

		public Map<String, Integer> getRow(){
			return this.termFrequency;
		}
	}*/
}