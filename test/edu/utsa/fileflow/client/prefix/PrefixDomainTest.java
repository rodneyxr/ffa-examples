package edu.utsa.fileflow.client.prefix;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import edu.utsa.fileflow.analysis.Analyzer;
import edu.utsa.fileflow.cfg.FlowPoint;
import edu.utsa.fileflow.utilities.FileFlowHelper;

public class PrefixDomainTest {

	final PrefixAnalysisDomain FACTORY = new PrefixAnalysisDomain();

	// holds the text of a script to be analyzed
	StringBuilder script;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// reset the script for before each test case
		script = new StringBuilder();
	}

	@Test
	public void testPrefixDomainClone() throws Exception {
		PrefixAnalysisDomain d1 = FACTORY.bottom();

		// create a reference of $x0 and store it in domain d1
		d1.table.put("$x0", new PrefixItem("a"));

		// create a clone of d1, called d2, that should contain different
		// references of each prefix item
		PrefixAnalysisDomain d2 = d1.clone();

		// assert table contents are the same
		assertEquals(d1.table, d2.table);

		// assert prefix items are different objects in memory
		assertTrue(d1.table.get("$x0") != d2.table.get("$x0"));

		// change d1 and make sure that it does not affect d2's values
		d1.table.get("$x0").prefix = "modified";
		assertThat(d1.table.get("$x0"), not(d2.table.get("$x0")));
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

		// check answer
		PrefixItem expected = new PrefixItem("a", true);

		assertTrue(result.table.containsValue(expected));
	}

	@Test
	public void testScriptAssignment() throws Exception {
		s("$x0 = 'a';");
		PrefixAnalysisDomain result = getResult();
		assertEquals(new PrefixItem("a"), result.table.get("$x0"));
	}

	@Test
	public void testScriptBasicIfStat() throws Exception {
		s("$x0 = 'abc';");
		s("if (other) {");
		s("    $x0 = 'ac';");
		s("}");
		PrefixAnalysisDomain result = getResult();
		assertEquals(new PrefixItem("a", true), result.table.get("$x0"));
	}

	@Test
	public void testScriptMerge() throws Exception {
		s("$x0 = 'a';");
		s("$x1 = 'ac';");
		s("if (other) {");
		s("    $x1 = $x0.$x1;");
		s("}");
		PrefixAnalysisDomain result = getResult();
		assertEquals(new PrefixItem("a", false), result.table.get("$x0"));
		assertEquals(new PrefixItem("a", true), result.table.get("$x1"));
	}

	@Test
	public void testScriptLoop() throws Exception {
		s("$x0 = 'ab';");
		s("$x1 = 't';");
		s("while (other) {");
		s("    $x0 = $x0.$x1;");
		s("}");
		PrefixAnalysisDomain result = getResult();
		// FIXME: ON_EXIT hook might be broken
		assertEquals(new PrefixItem("ab", true), result.table.get("$x0"));
		assertEquals(new PrefixItem("t", false), result.table.get("$x1"));
	}
	
	@Test
	public void testScriptArray() throws Exception {
		// TODO: Add support for this in grammar
		s("$x0 = INPUT;");
		s("while (other) {");
		s("    $x1[?] = 'header-'.$x0");
		s("    $x1[?] = $x1[?].'.txt;'");
		s("}");
		s("$y = '/home/'.$x1[?];");
		PrefixAnalysisDomain result = getResult();
		assertEquals(new PrefixItem("ab", true), result.table.get("$x0"));
		assertEquals(new PrefixItem("t", false), result.table.get("$x1"));
	}

	/**
	 * Analyzes the script and returns the resulting domain.
	 * 
	 * @return the result of the analysis on the script after exit.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private PrefixAnalysisDomain getResult() throws IOException {
		FlowPoint cfg = FileFlowHelper.generateControlFlowGraphFromScript(script.toString());
		Analyzer<PrefixAnalysisDomain, PrefixAnalysis> analyzer = new Analyzer<>(PrefixAnalysisDomain.class,
				PrefixAnalysis.class);
		return analyzer.analyze(cfg);
	}

	/**
	 * Adds a new line to the script.
	 * 
	 * @param line
	 *            The line to be added to the script.
	 */
	private void s(String line) {
		script.append(line);
		script.append("\n");
	}
}
