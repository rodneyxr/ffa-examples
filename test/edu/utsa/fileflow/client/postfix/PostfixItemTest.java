package edu.utsa.fileflow.client.postfix;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PostfixItemTest {

	@Test
	public void testLongestCommonPostfix() throws Exception {
		// x = 'abc'
		final String x = "abc";

		// y = 'ac'
		final String y = "ac";

		// postfix = 'c'
		final String expectedPostfix = "c";
		System.out.println(PostfixItem.longestCommonPostfix(x, y));

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
//		System.out.println(PostfixItem.longestCommonPostfix(x, y));

		// check answer
		assertEquals(expectedPostfix, PostfixItem.longestCommonPostfix(x, y));
	}

}
