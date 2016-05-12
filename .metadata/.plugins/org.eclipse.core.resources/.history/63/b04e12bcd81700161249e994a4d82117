package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import containers.TrieSQ;

public class TrieTest {

	@Test
	public void test() {
		TrieSQ trie = new TrieSQ();
		trie.insert("This is a test for the Enlighted ones!!! Strosserfelm@#$", true);
		trie.insert("This is a test for the Enlighted ones!!! Strosserfelm Heimer", true);
		trie.insert("This is a test for the Enlighted ones!!! Strosserfelm Steimer", true);
		trie.insert("This is a test HELLO TO YOU!!!", true);
		ArrayList<StringBuilder> builder = (ArrayList<StringBuilder>) trie.getSuggestedQueries("This is a");
		
		for(int i = 0; i < builder.size(); i++){
			System.out.print(builder.get(i).toString() + "\n");
		}
	}

}
