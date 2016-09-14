package edu.utsa.fileflow.client.postfix;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PostfixItemTest {

	@Test
	public void testLongestCommonPostfix() throws Exception {
		// x = 'abcd'
		final String x = "abcd";

		// y = 'acd'
		final String y = "acd";

		// postfix = 'cd'
		final String expectedPostfix = "cd";

		// check answer
		assertEquals(expectedPostfix, PostfixItem.longestCommonPostfix(x, y));
	}

	@Test
	public void testLongestCommonPostfixWithNoCommonPostfix() throws Exception {
		// x = 'cat'
		final String x = "cat";

		// y = 'dog'
		final String y = "dog";

		// postfix = ''
		final String expectedPostfix = "";

		// check answer
		assertEquals(expectedPostfix, PostfixItem.longestCommonPostfix(x, y));
	}

}
