package edu.utsa.fileflow.client.dummy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import edu.utsa.fileflow.analysis.AnalysisException;
import edu.utsa.fileflow.analysis.Analyzer;
import edu.utsa.fileflow.cfg.FlowPoint;
import edu.utsa.fileflow.utilities.FileFlowHelper;

public class DummyMain {

	private static final String TEST_SCRIPT = "scripts/test.ffa";

	public static void main(String[] args) throws FileNotFoundException, IOException {
		FlowPoint cfg = FileFlowHelper.generateControlFlowGraphFromFile(new File(TEST_SCRIPT));
		DummyAnalysisDomain domain = new DummyAnalysisDomain();
		DummyAnalysis analysis = new DummyAnalysis();
		Analyzer<DummyAnalysisDomain, DummyAnalysis> analyzer = new Analyzer<>(domain, analysis);
		try {
			analyzer.analyze(cfg);
		} catch (AnalysisException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
