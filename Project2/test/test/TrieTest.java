package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import containers.TrieSQ;

public class TrieTest {

	@Test
	public void test() {
		TrieSQ trie = new TrieSQ();
		trie.insert("This is a test for the Enlighted ones!!! Strosserfelm@#$");
		trie.insert("This is a test for the Enlighted ones!!! Strosserfelm Heimer");
		trie.insert("This is a test for the Enlighted ones!!! Strosserfelm Steimer");
		ArrayList<StringBuilder> builder = (ArrayList<StringBuilder>) trie.getSuggestedQueries("T");
		
		for(int i = 0; i < builder.size(); i++){
			System.out.print(builder.get(i).toString());
		}
	}

}
