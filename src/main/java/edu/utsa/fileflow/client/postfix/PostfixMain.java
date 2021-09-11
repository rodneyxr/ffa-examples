package edu.utsa.fileflow.client.postfix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import edu.utsa.fileflow.analysis.AnalysisException;
import edu.utsa.fileflow.analysis.Analyzer;
import edu.utsa.fileflow.cfg.FlowPoint;
import edu.utsa.fileflow.utilities.FileFlowHelper;

public class PostfixMain {

	public static boolean DEBUG = false;

	private static final String TEST_SCRIPT = "scripts/test.ffa";

	public static void main(String[] args) throws IOException, AnalysisException {
		FlowPoint cfg = FileFlowHelper.generateControlFlowGraphFromFile(new File(TEST_SCRIPT));
		PostfixAnalysisDomain domain = new PostfixAnalysisDomain();
		PostfixAnalysis analysis = new PostfixAnalysis();
		Analyzer<PostfixAnalysisDomain, PostfixAnalysis> analyzer = new Analyzer<>(domain, analysis);
		try {
			analyzer.analyze(cfg);
		} catch (AnalysisException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
