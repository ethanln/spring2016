package containers;

import java.util.ArrayList;
import java.util.List;

import model.Term;
import util.Tokenizer;

public class TrieSQ {
	
	private Node root;
	private long highestFrequency;
	private long highestModFrequency;
	
	public TrieSQ(){
		this.root = new Node();
		this.highestFrequency = 0;
		this.highestModFrequency = 0;
	}
	
	/**
	 * insert a suggested query
	 * @param query
	 * @return
	 */
	public boolean insert(String query, boolean hasBeenModified){
		if(query.equals("bible verses") || query.equals("bible verse")){
			//System.out.print(query);
		}
		
		ArrayList<String> suggestedQueryTerms = (ArrayList<String>)Tokenizer.parseQuery(query);
		Node node = root;
		for(int i = 0; i < suggestedQueryTerms.size(); i++){
			
			String term = suggestedQueryTerms.get(i);
			char[] termCharacters = term.toCharArray();
			
			for(int j = 0; j < termCharacters.length; j++){
				int index = (termCharacters[j]) - 97;

				try{
					if(node.nodes[index] == null){
						node.nodes[index] = new Node();
						node.nodes[index].value = termCharacters[j];
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
				node = node.nodes[index];
			}
			
			if(node.nodes[26] == null && i < suggestedQueryTerms.size() - 1){
				node.nodes[26] = new Node();
				node.nodes[26].value = 32;
			}
			
			if(i < suggestedQueryTerms.size() - 1){
				node = node.nodes[26];
			}
			
		}
		
		node.isComplete = true;
		node.frequency++;
		if(node.frequency > this.highestFrequency){
			this.highestFrequency = node.frequency;
		}
		
		if(hasBeenModified){
			node.modFrequency++;
			if(node.modFrequency > this.highestModFrequency){
				this.highestModFrequency = node.modFrequency;
			}
		}
		
		return true;
	}
	
	/**
	 * get suggested queries
	 * QUESTIONS: Do we need to include spaces
	 * How do we query this.
	 * @param query
	 * @return
	 */
	public List<Term> getSuggestedQueries(String query){
		ArrayList<String> queryTerms = (ArrayList<String>)Tokenizer.parseQuery(query);
		Node node = root;

		for(int i = 0; i < queryTerms.size(); i++){
			
			String term = queryTerms.get(i);
			char[] termCharacters = term.toCharArray();
			
			for(int j = 0; j < termCharacters.length; j++){
				int index = (termCharacters[j]) - 97;

				if(node.nodes[index] == null){
					return new ArrayList<Term>();
				}
				node = node.nodes[index];
			}
			// Do I want to include a space at the end of a query before looking for suggestions???
			//if(i < queryTerms.size() - 1){
			//	node = node.nodes[26];
			//}
			if(node.nodes[26] == null){
				return new ArrayList<Term>();
			}
			node = node.nodes[26];
		}		

		return (ArrayList<Term>)_getSuggestedQueries(node);
	}
	
	private ArrayList<Term> _getSuggestedQueries(Node node){
		ArrayList<Term> queries = new ArrayList<Term>();
		for(int i = 0; i < 27; i++){
			if(node.nodes[i] != null){
				queries.addAll(getSuggestedQueries(node.nodes[i]));
			}
		}

		return queries;
	}
	
	private ArrayList<Term> getSuggestedQueries(Node node){
		ArrayList<Term> queries = new ArrayList<Term>();
		for(int i = 0; i < 27; i++){
			if(node.nodes[i] != null){
				queries.addAll(getSuggestedQueries(node.nodes[i]));
			}
		}
		
		if(node.isComplete){
			queries.add(new Term(new StringBuilder(""), node.modFrequency, node.frequency));
		}
		
		// concatenate characters
		for(int i = 0; i < queries.size(); i++){
			queries.get(i).insert(0, String.valueOf((char)(node.value)));
		}

		return queries;
	}

	public long getHighestFrequency(){
		return this.highestFrequency;
	}
	
	public long getHighestModFrequency(){
		return this.highestModFrequency;
	}
	
	private class Node{
		public boolean isComplete;
		public Node[] nodes;
		
		public int value;
		
		public int frequency;
		public int modFrequency;
		
		public Node(){
			this.isComplete = false;
			
			this.nodes = new Node[27];
			this.value = -1;
			
			this.frequency = 0;
			this.modFrequency = 0;
		}
	}
}
