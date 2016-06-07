package mnb;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import util.MathExt;

public class MNBClassification{

	//private MDR training_set;
	//private MDR test_set;

	private String[] M = {"all", "24800", "18600", "12400", "6200"};
	
	private DC documentCollection;
	
	private MNBProbability probability;


	public MNBClassification(int upperBoundTraining, int upperBoundTest){
		this.documentCollection = new DC(upperBoundTraining, upperBoundTest);
		this.probability = new MNBProbability();
	}

	public void init(String collectionDir){
		try {
			this.documentCollection.init(collectionDir);
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * begin classification
	 */
	public void doClassification(){
		for(String m : M){
			Set<String> features;
			if(!m.equals("all")){
				features = this.featureSelect(Integer.parseInt(m));	
			}
			else{
				features = this.documentCollection.getDC_TrainingVocab();
			}
			
			Map<String, Map<String, Double>> WordProbabilities = this.probability.computeWordProbability(this.documentCollection, features);
			Map<String, Double> ClassProbabilities = this.probability.computeClassProbability(this.documentCollection);
			
			// do labeling for each class test set
			for(String className : this.documentCollection.getClasses()){
				
				// Map<DocId, resultClass> - this contains all documents of a test set of a particular 
				// class and their class results from the label method
				Map<String, String> classResults = new TreeMap<String, String>();
				
				// Map<DocId, Map<term, count>> - fetch all DC_test documents of a particular class
				Map<String, Map<String, Integer>> test_set = this.documentCollection.getDC_Test_Set(className);
				
				// for each document in the test set of the class
				for(String docId : test_set.keySet()){
					// get class label of a document
					String classResult = this.label(WordProbabilities, ClassProbabilities, test_set.get(docId), docId);
					classResults.put(docId, classResult);
					
					System.out.print("Document: " + docId + "\n");
					System.out.print("Original Class: " + className + "\n");
					System.out.print("Output Class: " + classResult + "\n\n");
				}
				
				// measure accuracy of class TODO: HERE
			}
		}
	}

	/**
	 * *************************************************************************************
	 * 									LABEL
	 * *************************************************************************************
	 * Assigns the most probable class for a particular document in test_set.
	 * In performing the classification task, you are required to use the getWordProbability and getClassProbability methods
	 * @param m
	 * @return
	 */
	public String label(Map<String, Map<String, Double>> WordProbabilities, 
						Map<String, Double> ClassProbabilities, 
						Map<String, Integer> wordSetInDoc, 
						String docId){
		// Set<class : score>
		Set<ClassScore> scores = new TreeSet<ClassScore>();
		
		// iterate through class name for the document
		for(String className : this.documentCollection.getClasses()){
			
			// initial score
			double score = 1.0;
			
			// for each word in the document
			for(String word : wordSetInDoc.keySet()){
				
				// check if word in the probability set of the specified class
				if(WordProbabilities.get(className).containsKey(word)){
					// P(w|c)^(term frequency in document)
					score *= Math.pow(WordProbabilities.get(className).get(word), wordSetInDoc.get(word));
				}
				else{
					// P(w|c)^(term frequency in document)  -  In this case it is not seen word, because it can't be found in 
					// the word probabilities for the particular class
					score *= Math.pow(WordProbabilities.get(className).get(MNBProbability.NOT_SEEN), wordSetInDoc.get(word));
				}
			}
			scores.add(new ClassScore(className, score /* ClassProbabilities.get(className)*/));
		}
		return scores.iterator().next().className;
	}
	
	/**
	 * *************************************************************************************
	 * 								FEATURE SELECT
	 * *************************************************************************************
	 * feature selection for the first mth terms in the DC_training found in the DC instance
	 * @param m
	 * @return
	 */
	public Set<String> featureSelect(int m){
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
		TreeSet<String> features = new TreeSet<String>();
		
		// variable to keep track of position of term
		int count = 0;
		for(Feature feature : featureSet){
			if(count >= m){
				break;
			}
			
			features.add(feature.getTerm());
			count++;
		}
		
		// return features
		return features;
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

	/**
	 * WRAPPED CLASS FEATURE
	 */
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
	
	@SuppressWarnings("rawtypes")
	private class ClassScore implements Comparable{
		
		public ClassScore(String _className, double _score){
			this.className = _className;
			this.score = _score;
		}
		public String className;
		public double score;

		@Override
		public int compareTo(Object obj) {
			ClassScore doc = (ClassScore)obj;
			if(this.score == doc.score){
				return 1;
			}
			else if(this.score > doc.score){
				return -1;
			}
			return 1;
		}
	}
}
