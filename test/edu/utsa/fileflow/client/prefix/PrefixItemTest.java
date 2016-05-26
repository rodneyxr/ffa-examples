package edu.utsa.fileflow.client.prefix;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PrefixItemTest {

	@Test
	public void testLongestCommonPrefix() throws Exception {
		// x = 'abc'
		final String x = "abc";

		// y = 'ac'
		final String y = "ac";

		// prefix = 'a'
		final String prefix = "a";

		// check answer
		assertEquals(PrefixItem.longestCommonPrefix(x, y), prefix);
	}

	@Test
	public void testLongestCommonPrefixWithNoCommonPrefix() throws Exception {
		// x = 'cat'
		final String x = "cat";

		// y = 'dog'
		final String y = "dog";

		// prefix = ''
		final String prefix = "";
		System.out.println(PrefixItem.longestCommonPrefix(x, y));

		// check answer
		assertEquals(PrefixItem.longestCommonPrefix(x, y), prefix);
	}

}
