package mnb;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import util.MathExt;

public class MNBClassification{

	private MDR training_set;
	private MDR test_set;

	private String[] M = {"all", "24800", "18600", "12400", "6200"};
	
	private DC documentCollection;


	public MNBClassification(int upperBoundTraining, int upperBoundTest){
		this.training_set = new MDR();
		this.test_set = new MDR();
		this.documentCollection = new DC(upperBoundTraining, upperBoundTest);
	}

	public void init(String collectionDir){
		try {
			this.documentCollection.init(collectionDir);
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void doClassification(){
		for(String m : M){
			if(!m.equals("all")){
				ArrayList<Feature> features = this.featureSelect(Integer.parseInt(m));
				this.setUpTrainingSet(features);
				this.setUpTestSet(features);
			}
			else{
				this.setUpTrainingSet();
				this.setUpTestSet();
			}
		}
	}

	/**
	 * feature selection for the first mth terms in the DC_training found in the DC instance
	 * @param m
	 * @return
	 */
	public ArrayList<Feature> featureSelect(int m){
		TreeSet<Feature> featureSet = new TreeSet<Feature>();
		
		// get list of all vocabulary from corpus
		Set<String> vocab = this.documentCollection.getDC_TrainingVocab();
		
		// get list of all classes in corpus
		Set<String> classNames = this.documentCollection.getClasses();
		
		// for each word w in the vocab
		for(String w : vocab){
			double score = 0.0;
			double summation1 = 0.0;
			double summation2 = 0.0;
			double summation3 = 0.0;
			
			// get all summations
			for(String c : classNames){
				summation1 += this.PCollection(c) * MathExt.log2(this.PCollection(c));
				summation2 += this.PCollectionWithWord(c, w) * MathExt.log2(this.PCollectionWithWord(c, w));
				summation3 += this.PCollectionWithoutWord(c, w) * MathExt.log2(this.PCollectionWithoutWord(c, w));
			}
			
			// get official score
			score = (-1.0 * summation1) + (this.PWord(w) * summation2) + (this.PNotWord(w) * summation3);
			
			// add features to structure
			featureSet.add(new Feature(w, score));
		}	
		
		// sub set the list of features till the mth position
		ArrayList<Feature> features = new ArrayList<Feature>();
		int count = 0;
		for(Feature feature : featureSet){
			if(count == m){
				break;
			}
			
			features.add(feature);
			count++;
		}
		// return features
		return features;
	}

	public void setUpTrainingSet(){
		
	}
	
	public void setUpTestSet(){
		
	}
	
	public void setUpTrainingSet(ArrayList<Feature> features){
		
	}

	public void setUpTestSet(ArrayList<Feature> features){
		
	}
	
	// P(c)
	private double PCollection(String c){
		return (double)this.documentCollection.getNumberOfDocumentsInDC_TrainingLabeled_C(c) / (double)this.documentCollection.getTotalNumberOfDocumentsInDC_Training();
	}
	
	// p(w)
	private double PWord(String w){
		return (double)this.documentCollection.getNumberOfDocumentsInDC_TrainingWith_W(w) / (double)this.documentCollection.getTotalNumberOfDocumentsInDC_Training();
	}
	
	// p(!w)
	private double PNotWord(String w){
		return (double)this.documentCollection.getNumberOfDocumentsInDC_TrainingWithout_W(w) / (double)this.documentCollection.getTotalNumberOfDocumentsInDC_Training();
	}
	
	// p(c|w)
	private double PCollectionWithWord(String c, String w){
		return (double)this.documentCollection.getNumberOfDocsInDC_TrainingWith_W_Labeled_C(c, w) / (double)this.documentCollection.getNumberOfDocumentsInDC_TrainingWith_W(w);
	}
	
	// p(c|!w)
	private double PCollectionWithoutWord(String c, String w){
		return (double)this.documentCollection.getNumberOfDocsInDC_TrainingWithout_W_Labeled_C(c, w) / (double)this.documentCollection.getNumberOfDocumentsInDC_TrainingWithout_W(w);
	}

	@SuppressWarnings("rawtypes")
	private class Feature implements Comparable{
		private double score;
		private String term;
		
		public Feature(String _term, double _score)
		{
			this.score = _score;
			this.term = _term;
		}

		public double getScore() {
			return score;
		}

		public String getTerm() {
			return term;
		}

		@Override
		public int compareTo(Object obj) {
			Feature doc = (Feature)obj;
			if(this.score == doc.getScore()){
				return 1;
			}
			else if(this.score > doc.getScore()){
				return -1;
			}
			return 1;
		}
	}
}
