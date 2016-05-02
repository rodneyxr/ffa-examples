package edu.utsa.fileflow.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import edu.utsa.fileflow.analysis.Analyzer;
import edu.utsa.fileflow.cfg.FlowPoint;
import edu.utsa.fileflow.client.prefix.PrefixAnalysis;
import edu.utsa.fileflow.client.prefix.PrefixAnalysisDomain;
import edu.utsa.fileflow.utilities.FileFlowHelper;

public class Main {

	private static final String TEST_SCRIPT = "scripts/test.ffa";

	public static void main(String[] args) throws FileNotFoundException, IOException {
		FlowPoint cfg = FileFlowHelper.generateControlFlowGraphFromScript(new File(TEST_SCRIPT));

		// perform prefix analysis
		Analyzer<PrefixAnalysisDomain, PrefixAnalysis> analyzer = new Analyzer<>(PrefixAnalysisDomain.class,
				PrefixAnalysis.class);
		analyzer.analyze(cfg);
	}

}
