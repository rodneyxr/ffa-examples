package edu.utsa.fileflow.client.fileflow.grammar;

import dk.brics.automaton.Automaton;
import edu.utsa.fileflow.analysis.Analyzer;
import edu.utsa.fileflow.cfg.FlowPoint;
import edu.utsa.fileflow.client.fileflow.variable.LiveVariableMap;
import edu.utsa.fileflow.client.fileflow.variable.VariableAnalysis;
import edu.utsa.fileflow.client.fileflow.variable.VariableAnalysisDomain;
import edu.utsa.fileflow.utilities.FileFlowHelper;
import edu.utsa.fileflow.utilities.GraphvizGenerator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class tests functionality of the variable analysis.
 * <p>
 * Created by Rodney on 2/11/2017.
 */
public class GrammarAnalysisTest {

	/* Variable Analysis */
	VariableAnalysisDomain variableAnalysisDomain;
	VariableAnalysis variableAnalysis;
	Analyzer<VariableAnalysisDomain, VariableAnalysis> variableAnalyzer;
	VariableAnalysisDomain variableAnalysisResult;

	/* Grammar Analysis */
	GrammarAnalysisDomain grammarDomain;
	GrammarAnalysis grammarAnalysis;
	Analyzer<GrammarAnalysisDomain, GrammarAnalysis> grammarAnalyzer;

	/* Results */
	GrammarAnalysisDomain grammarAnalysisResult;
	LiveVariableMap liveVariables;

	@Before
	public void setUp() throws Exception {
		/* Variable Analysis */
		variableAnalysisDomain = new VariableAnalysisDomain();
		variableAnalysis = new VariableAnalysis();
		variableAnalyzer = new Analyzer<>(variableAnalysisDomain, variableAnalysis);

		/* Grammar Analysis */
		GrammarAnalysisDomain grammarDomain = new GrammarAnalysisDomain();
		GrammarAnalysis grammarAnalysis = new GrammarAnalysis();
		grammarAnalyzer = new Analyzer<>(grammarDomain, grammarAnalysis);
	}

	@Test
	public void testAnalysis() throws Exception {
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
		createAnalyzers(cfg);
		Automaton a;

		a = grammarAnalysisResult.getVariable("$x0", liveVariables);
		assertTrue(a.run("a"));

		a = grammarAnalysisResult.getVariable("$x1", liveVariables);
		assertTrue(a.run("b"));

		a = grammarAnalysisResult.getVariable("$x2", liveVariables);
		assertTrue(a.run("c"));

		a = grammarAnalysisResult.getVariable("$x3", liveVariables);
		assertTrue(a.run("b"));
		assertTrue(a.run("c"));

		a = grammarAnalysisResult.getVariable("$x4", liveVariables);
		assertTrue(a.run("b"));
		assertTrue(a.run("c"));
	}

	@Test
	public void testAnalysisSimple() throws Exception {
		FlowPoint cfg = FileFlowHelper.generateControlFlowGraphFromScript(
				"" +
						"$x0 = 'a';" +
						"$x1 = 'b';" +
						"if (other) {" +
						"    $x0 = $x1;" +
						"}" +
						"$x1 = $x0;"
		);
		createAnalyzers(cfg);
		Automaton a;

		a = grammarAnalysisResult.getVariable("$x0", liveVariables);
		assertTrue(a.run("a"));
		assertTrue(a.run("b"));

		a = grammarAnalysisResult.getVariable("$x1", liveVariables);
		assertTrue(a.run("a"));
		assertTrue(a.run("b"));
	}

	@Test
	public void testAnalysisWithSimpleLoop() throws Exception {
		FlowPoint cfg = FileFlowHelper.generateControlFlowGraphFromScript(
				"" +
						"$x0 = 'a';" +
						"while (other) {" +
						"    $x0 = 'b';" +
						"}"
		);
		createAnalyzers(cfg);
		Automaton a;

		GrammarAnalysisDomain result = grammarAnalyzer.analyze(cfg);
		a = result.getVariable("$x0", liveVariables);
		assertTrue(a.run("a"));
		assertTrue(a.run("b"));
	}

	@Test
	public void testAnalysisWithConcatLoop1Var() throws Exception {
		FlowPoint cfg = FileFlowHelper.generateControlFlowGraphFromScript(
				"" +
						"$x0 = 'a';" +
						"while (other) {" +
						"    $x0 = $x0.$x0;" +
						"}"
		);
		createAnalyzers(cfg);
		Automaton a;

		a = grammarAnalysisResult.getVariable("$x0", liveVariables);
		assertTrue(a.run("a"));
		assertTrue(a.run("aa"));
	}

	@Test
	public void testAnalysisWithConcatLoop2Vars() throws Exception {
		FlowPoint cfg = FileFlowHelper.generateControlFlowGraphFromScript(
				"" +
						"$x0 = 'a';" +
						"$x1 = 'b';" +
						"while (other) {" +
						"    $x0 = $x0.$x1;" +
						"}"
		);
		createAnalyzers(cfg);
		Automaton a;

		a = grammarAnalysisResult.getVariable("$x0", liveVariables);
		assertTrue(a.run("a"));
		assertTrue(a.run("ab"));

		a = grammarAnalysisResult.getVariable("$x1", liveVariables);
		assertTrue(a.run("b"));
		assertFalse(a.run("a"));
	}

	private void createAnalyzers(FlowPoint cfg) throws Exception {
		variableAnalysisResult = variableAnalyzer.analyze(cfg);
		grammarAnalysisResult = grammarAnalyzer.analyze(cfg);
		liveVariables = variableAnalysisResult.getLiveVariables();
	}

}
