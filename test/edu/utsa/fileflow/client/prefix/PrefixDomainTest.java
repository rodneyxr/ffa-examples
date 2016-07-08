package edu.utsa.fileflow.client.prefix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PrefixDomainTest {

	final PrefixAnalysisDomain FACTORY = new PrefixAnalysisDomain();

	@Test
	public void testPrefixDomainClone() throws Exception {
		PrefixAnalysisDomain d1 = FACTORY.bottom();
		d1.table.put("$x0", new PrefixItem("a"));

		PrefixAnalysisDomain d2 = d1.clone();

		// check answer
		assertEquals(d1.table, d2.table);
	}

	@Test
	public void testPrefixDomainMerge() throws Exception {
		PrefixAnalysisDomain source = FACTORY.bottom();
		PrefixAnalysisDomain target = FACTORY.bottom();

		// $x0 = 'abc'
		source.table.put("$x0", new PrefixItem("abc"));
		// $x0 = 'ac'
		target.table.put("$x0", new PrefixItem("ac"));

		// merge and store result
		PrefixAnalysisDomain result = source.merge(target);
		// cut to common prefix
		// prefix = 'a'
		// anything after prefix is unknown

		// System.out.println(result.table.get("$x0"));

		// check answer
		PrefixItem expected = new PrefixItem("a");
		expected.setPrefix("a"); // this sets unknown to true; (a*)

		assertTrue(result.table.containsValue(expected));
	}
}
