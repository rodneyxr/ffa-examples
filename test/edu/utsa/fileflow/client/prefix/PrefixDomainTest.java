package edu.utsa.fileflow.client.prefix;

import static org.junit.Assert.assertEquals;
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

	StringBuilder script;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		script = new StringBuilder();
	}

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

		// check answer
		PrefixItem expected = new PrefixItem("a", true);

		assertTrue(result.table.containsValue(expected));
	}

	@Test
	public void testAssignment() throws Exception {
		s("$x0 = 'a';");
		PrefixAnalysisDomain result = getResult(script.toString());
		assertEquals(result.table.get("$x0"), new PrefixItem("a"));
	}

	private PrefixAnalysisDomain getResult(String script) throws FileNotFoundException, IOException {
		FlowPoint cfg = FileFlowHelper.generateControlFlowGraphFromScript(script);
		Analyzer<PrefixAnalysisDomain, PrefixAnalysis> analyzer = new Analyzer<>(PrefixAnalysisDomain.class,
				PrefixAnalysis.class);
		return analyzer.analyze(cfg);
	}

	private void s(String line) {
		script.append(line);
		script.append("\n");
	}
}
