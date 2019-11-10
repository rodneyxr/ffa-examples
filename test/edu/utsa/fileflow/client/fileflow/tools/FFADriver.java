package edu.utsa.fileflow.client.fileflow.tools;

import edu.utsa.fileflow.analysis.AnalysisException;
import edu.utsa.fileflow.analysis.Analyzer;
import edu.utsa.fileflow.cfg.FlowPoint;
import edu.utsa.fileflow.client.fileflow.FileFlowAnalysis;
import edu.utsa.fileflow.client.fileflow.FileFlowAnalysisDomain;
import edu.utsa.fileflow.client.fileflow.grammar.GrammarAnalysis;
import edu.utsa.fileflow.client.fileflow.grammar.GrammarAnalysisDomain;
import edu.utsa.fileflow.client.fileflow.variable.LiveVariableMap;
import edu.utsa.fileflow.client.fileflow.variable.VariableAnalysis;
import edu.utsa.fileflow.client.fileflow.variable.VariableAnalysisDomain;
import edu.utsa.fileflow.utilities.FileFlowHelper;

import java.io.IOException;

/**
 * This is a helper class to make it simple to run a script and store the results
 * during testing.
 * <p>
 * Created by Rodney on 4/26/2017.
 */
public class FFADriver {

	/* Variable Analysis */
	public VariableAnalysisDomain variableAnalysisDomain;
	public VariableAnalysis variableAnalysis;
	public Analyzer<VariableAnalysisDomain, VariableAnalysis> variableAnalyzer;

	/* Grammar Analysis */
	public GrammarAnalysisDomain grammarDomain;
	public GrammarAnalysis grammarAnalysis;
	public Analyzer<GrammarAnalysisDomain, GrammarAnalysis> grammarAnalyzer;

	/* File Flow Analysis */
	public FileFlowAnalysisDomain ffaDomain;
	public FileFlowAnalysis ffaAnalysis;
	public Analyzer<FileFlowAnalysisDomain, FileFlowAnalysis> ffaAnalyzer;

	/* Results */
	public VariableAnalysisDomain variableResult;
	public GrammarAnalysisDomain grammarResult;
	public FileFlowAnalysisDomain ffaResult;
	public LiveVariableMap liveVariables;

	private FlowPoint cfg;

	private FFADriver(String script) throws Exception {
		cfg = FileFlowHelper.generateControlFlowGraphFromScript(script);
	}

	public static FFADriver run(String script) throws Exception {
		FFADriver ffaDriver = new FFADriver(script);
		ffaDriver.setUp();
		ffaDriver.createAnalyzers();
		return ffaDriver;
	}

	private void setUp() {
		/* Variable Analysis */
		variableAnalysisDomain = new VariableAnalysisDomain();
		variableAnalysis = new VariableAnalysis();
		variableAnalyzer = new Analyzer<>(variableAnalysisDomain, variableAnalysis);

		/* Grammar Analysis */
		grammarDomain = new GrammarAnalysisDomain();
		grammarAnalysis = new GrammarAnalysis();
		grammarAnalyzer = new Analyzer<>(grammarDomain, grammarAnalysis);

		/* File Flow Analysis */
		ffaDomain = new FileFlowAnalysisDomain();
		ffaAnalysis = new FileFlowAnalysis();
		ffaAnalyzer = new Analyzer<>(ffaDomain, ffaAnalysis);
	}

	private void createAnalyzers() throws AnalysisException {
		variableResult = variableAnalyzer.analyze(cfg);
		grammarResult = grammarAnalyzer.analyze(cfg);
		ffaResult = ffaAnalyzer.analyze(cfg);
		liveVariables = variableResult.getLiveVariables();
	}

}
