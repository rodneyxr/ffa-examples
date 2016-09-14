package edu.utsa.fileflow.client.postfix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import edu.utsa.fileflow.analysis.AnalysisException;
import edu.utsa.fileflow.analysis.Analyzer;
import edu.utsa.fileflow.cfg.FlowPoint;
import edu.utsa.fileflow.utilities.FileFlowHelper;

public class PostfixMain {

	private static final String TEST_SCRIPT = "scripts/test.ffa";

	public static void main(String[] args) throws FileNotFoundException, IOException {
		FlowPoint cfg = FileFlowHelper.generateControlFlowGraphFromFile(new File(TEST_SCRIPT));
		Analyzer<PostfixAnalysisDomain, PostfixAnalysis> analyzer = new Analyzer<>(PostfixAnalysisDomain.class,
				PostfixAnalysis.class);
		try {
			analyzer.analyze(cfg);
		} catch (AnalysisException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
