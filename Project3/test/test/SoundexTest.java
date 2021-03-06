package test;

import static org.junit.Assert.*;

import org.junit.Test;

import soundex.Soundex;

public class SoundexTest {

	@Test
	public void testSoundex() {
		Soundex soundex = new Soundex();
		assertEquals("S655", soundex.encodeString("screening"));
		assertEquals("M100", soundex.encodeString("movie"));
		
		assertEquals("E235", soundex.encodeString("extenssions"));
		assertEquals("E235", soundex.encodeString("extensions"));
		
		assertEquals("M625", soundex.encodeString("marshmellow"));
		assertEquals("M625", soundex.encodeString("marshmallow"));
		
		assertEquals("B655", soundex.encodeString("brimingham"));
		assertEquals("B655", soundex.encodeString("birmingham"));
		
		assertEquals("P560", soundex.encodeString("poiner"));
		assertEquals("P536", soundex.encodeString("pointer"));
		
		assertEquals("M100", soundex.encodeString("moff"));
		
		
	}

	@Test
	public void testEditDistance() {
		assertEquals(3, Soundex.LevenShteinDistance("sitting", "kitten"));
		assertEquals(3, Soundex.LevenShteinDistance("sunday", "saturday"));

	}
}
