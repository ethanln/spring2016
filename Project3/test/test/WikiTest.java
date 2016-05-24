package test;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import exception.TokenizerException;
import util.Tokenizer;

public class WikiTest {

	@Test
	public void test() throws TokenizerException {
		File fileOne = new File("wiki/To_be_posted/Doc (200).txt");
		File fileTwo = new File("wiki/To_be_posted/Doc (201).txt");
		File fileThree = new File("wiki/To_be_posted/Doc (202).txt");
		Tokenizer tokenizer = new Tokenizer();
		
		tokenizer.parse(fileOne);
		tokenizer.parse(fileTwo);
		tokenizer.parse(fileThree);
		
		assertEquals("200", tokenizer.getDocWithMostFrequency("dancer"));
		assertEquals(0, tokenizer.getNumberOfKeyWordsInDocument("200", "two"));
		assertEquals(2, tokenizer.getNumberOfKeyWordsInDocument("200", "charact"));
		
		assertEquals(0, tokenizer.getHighestFrequency("their"));
		assertEquals(3, tokenizer.getHighestFrequency("year"));
		assertEquals(8, tokenizer.getHighestFrequency("patsi"));
		assertEquals(2, tokenizer.getNumberOfDocumentsWithWord("patsi"));
		
		assertEquals("202", tokenizer.getDocWithMostFrequency("patsi"));
		//assertEquals(2, tokenizer.frequencyOfWordInDocument("202", "jame"));
		
		assertEquals(3, tokenizer.getNumberOfDocuments());
	}

}
