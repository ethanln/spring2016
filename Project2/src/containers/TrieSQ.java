package containers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import util.Tokenizer;

public class TrieSQ {
	
	private Node root;
	
	public TrieSQ(){
		this.root = new Node();
	}
	
	/**
	 * insert a suggested query
	 * @param query
	 * @return
	 */
	public boolean insert(String query){
		ArrayList<String> suggestedQueryTerms = (ArrayList)Tokenizer.parseQuery(query);
		Node node = root;
		for(int i = 0; i < suggestedQueryTerms.size(); i++){
			
			String term = suggestedQueryTerms.get(i);
			char[] termCharacters = term.toCharArray();
			
			for(int j = 0; j < termCharacters.length; j++){
				int index = (termCharacters[j]) - 97;
				if(node.nodes[index] == null){
					node.nodes[index] = new Node();
				}
				node = node.nodes[index];
				node.value = index;
			}
		}
		
		node.isComplete = true;
		
		return true;
	}
	
	/**
	 * get suggested queries
	 * QUESTIONS: Do we need to include spaces
	 * How do we query this.
	 * @param query
	 * @return
	 */
	public List<StringBuilder> getSuggestedQueries(String query){
		ArrayList<String> queryTerms = (ArrayList)Tokenizer.parseQuery(query);
		Node node = root;
		
		ArrayList<String> suggestedQueries = new ArrayList<String>();

		for(int i = 0; i < queryTerms.size(); i++){
			
			String term = queryTerms.get(i);
			char[] termCharacters = term.toCharArray();
			
			for(int j = 0; j < termCharacters.length; j++){
				int index = (termCharacters[j]) - 97;
				if(node.nodes[index] == null){
					return new ArrayList<StringBuilder>();
				}
				node = node.nodes[index];
			}
		}		

		return (ArrayList<StringBuilder>)_getSuggestedQueries(node);
		
		//return suggestedQueries;
	}
	
	private ArrayList<StringBuilder> _getSuggestedQueries(Node node){
		ArrayList<StringBuilder> queries = new ArrayList<StringBuilder>();
		for(int i = 0; i < 26; i++){
			if(node.nodes[i] != null){
				queries.addAll(getSuggestedQueries(node.nodes[i]));
			}
		}
		return queries;
	}
	
	private ArrayList<StringBuilder> getSuggestedQueries(Node node){
		ArrayList<StringBuilder> queries = new ArrayList<StringBuilder>();
		for(int i = 0; i < 26; i++){
			if(node.nodes[i] != null){
				queries.addAll(getSuggestedQueries(node.nodes[i]));
			}
		}
		
		if(node.isComplete){
			queries.add(new StringBuilder("" + String.valueOf((char)(node.value + 97))));
		}
		else{
			for(int i = 0; i < queries.size(); i++){
				queries.get(i).insert(0, String.valueOf((char)(node.value + 97)));
			}
		}
		return queries;
	}

	
	private class Node{
		public boolean isComplete;
		public Node[] nodes;
		
		public int value;
		
		public Node(){
			this.isComplete = false;
			this.nodes = new Node[26];
			this.value = 0;
		}
	}
}
