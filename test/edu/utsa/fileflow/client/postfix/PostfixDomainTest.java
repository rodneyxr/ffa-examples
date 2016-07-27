package edu.utsa.fileflow.client.postfix;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PostfixDomainTest {

	final PostfixAnalysisDomain FACTORY = new PostfixAnalysisDomain();

	@Test
	public void testPostfixDomainMerge() throws Exception {
		PostfixAnalysisDomain source = FACTORY.bottom();
		PostfixAnalysisDomain target = FACTORY.bottom();

		// $x0 = 'abc'
		source.table.put("$x0", new PostfixItem("abc"));
		// $x0 = 'ac'
		target.table.put("$x0", new PostfixItem("ac"));

		// merge and store result
		PostfixAnalysisDomain result = source.merge(target);
		// cut to common postfix
		// postfix = 'c'
		// anything before postfix is unknown

		// System.out.println(result.table.get("$x0"));

		// check answer
		PostfixItem expected = new PostfixItem("c");
		expected.setPostfix("c"); // this sets unknown to true; (*c)

		assertTrue(result.table.containsValue(expected));
	}
}
