package edu.utsa.fileflow.client.fileflow.variable;

import dk.brics.automaton.Automaton;
import edu.utsa.fileflow.analysis.Analyzer;
import edu.utsa.fileflow.cfg.FlowPoint;
import edu.utsa.fileflow.utilities.FileFlowHelper;
import edu.utsa.fileflow.utilities.GraphvizGenerator;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class tests functionality of the variable analysis.
 * <p>
 * Created by Rodney on 2/11/2017.
 */
public class VariableAnalysisTest {

	@Test
	public void testAnalysis() throws Exception {
		VariableAnalysisDomain domain = new VariableAnalysisDomain();
		VariableAnalysis analysis = new VariableAnalysis();
		Analyzer<VariableAnalysisDomain, VariableAnalysis> analyzer = new Analyzer<>(domain, analysis);
		FlowPoint cfg = FileFlowHelper.generateControlFlowGraphFromScript(
				"" +
						"$x0 = 'a';" +
						"$x1 = 'b';" +
						"$x2 = 'c';" +
						"$x3 = $x0;" +
						"$x3 = $x1;" +
						"if (other) {" +
						"   $x3 = $x2;" +
						"}" +
						"$x4 = $x3;"
		);
		VariableAnalysisDomain result = analyzer.analyze(cfg);
		Automaton a;

		a = result.getVariable("$x0");
		assertTrue(a.run("a"));

		a = result.getVariable("$x1");
		assertTrue(a.run("b"));

		a = result.getVariable("$x2");
		assertTrue(a.run("c"));

		a = result.getVariable("$x3");
		assertTrue(a.run("b"));
		assertTrue(a.run("c"));

		a = result.getVariable("$x4");
		assertTrue(a.run("b"));
		assertTrue(a.run("c"));
	}

	@Test
	public void testAnalysisSimple() throws Exception {
		Analyzer<VariableAnalysisDomain, VariableAnalysis> analyzer = createAnalyzer();
		FlowPoint cfg = FileFlowHelper.generateControlFlowGraphFromScript(
				"" +
						"$x0 = 'a';" +
						"$x1 = 'b';" +
						"if (other) {" +
						"    $x0 = $x1;" +
						"}" +
						"$x1 = $x0;"
		);
		VariableAnalysisDomain result = analyzer.analyze(cfg);
		Automaton a = result.getVariable("$x0");
		assertTrue(a.run("a"));
		assertTrue(a.run("b"));

		a = result.getVariable("$x1");
		assertTrue(a.run("a"));
		assertTrue(a.run("b"));
	}

	@Test
	public void testAnalysisWithSimpleLoop() throws Exception {
		Analyzer<VariableAnalysisDomain, VariableAnalysis> analyzer = createAnalyzer();
		FlowPoint cfg = FileFlowHelper.generateControlFlowGraphFromScript(
				"" +
						"$x0 = 'a';" +
						"while (other) {" +
						"    $x0 = 'b';" +
						"}"
		);
		VariableAnalysisDomain result = analyzer.analyze(cfg);
		Automaton a = result.getVariable("$x0");
		assertTrue(a.run("a"));
		assertTrue(a.run("b"));
	}

	@Test
	public void testAnalysisWithConcatLoop1Var() throws Exception {
		Analyzer<VariableAnalysisDomain, VariableAnalysis> analyzer = createAnalyzer();
		FlowPoint cfg = FileFlowHelper.generateControlFlowGraphFromScript(
				"" +
						"$x0 = 'a';" +
						"while (other) {" +
						"    $x0 = $x0.$x0;" +
						"}"
		);
		VariableAnalysisDomain result = analyzer.analyze(cfg);
		// FIXME: java.lang.RuntimeException: grammar is not strongly regular
		Automaton a = result.getVariable("$x0");
		GraphvizGenerator.saveDOTToFile(a.toDot(), "tmp/analysis_x0.dot");
		assertTrue(a.run("a"));
		assertTrue(a.run("aa"));
	}

	@Test
	public void testAnalysisWithConcatLoop2Vars() throws Exception {
		Analyzer<VariableAnalysisDomain, VariableAnalysis> analyzer = createAnalyzer();
		FlowPoint cfg = FileFlowHelper.generateControlFlowGraphFromScript(
				"" +
						"$x0 = 'a';" +
						"$x1 = 'b';" +
						"while (other) {" +
						"    $x0 = $x0.$x1;" +
						"}"
		);
		VariableAnalysisDomain result = analyzer.analyze(cfg);
		Automaton a = result.getVariable("$x0");
		GraphvizGenerator.saveDOTToFile(a.toDot(), "tmp/analysis_x0.dot");
		assertTrue(a.run("a"));
		assertTrue(a.run("ab"));

		a = result.getVariable("$x1");
		assertTrue(a.run("b"));
		assertFalse(a.run("a"));
	}

	private Analyzer<VariableAnalysisDomain, VariableAnalysis> createAnalyzer() {
		VariableAnalysisDomain domain = new VariableAnalysisDomain();
		VariableAnalysis analysis = new VariableAnalysis();
		return new Analyzer<>(domain, analysis);
	}

}
