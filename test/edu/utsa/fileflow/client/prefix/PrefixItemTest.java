package edu.utsa.fileflow.client.prefix;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PrefixItemTest {

	@Test
	public void testLongestCommonPrefix() throws Exception {
		// x = 'abc'
		// y = 'ac'
		// prefix = 'a'
		final String x = "abc";
		final String y = "ac";
		final String expectedPrefix = "a";
		assertEquals(expectedPrefix, PrefixItem.longestCommonPrefix(x, y));
	}

	@Test
	public void testLongestCommonPrefixWithNoCommonPrefix() throws Exception {
		// x = 'cat'
		// y = 'dog'
		// prefix = ''
		final String x = "cat";
		final String y = "dog";
		final String expectedPrefix = "";
		assertEquals(expectedPrefix, PrefixItem.longestCommonPrefix(x, y));
	}

}
