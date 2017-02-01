package edu.utsa.fileflow.client.prefix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import edu.utsa.fileflow.analysis.AnalysisException;
import edu.utsa.fileflow.analysis.Analyzer;
import edu.utsa.fileflow.cfg.FlowPoint;
import edu.utsa.fileflow.testutils.GraphvizGenerator;
import edu.utsa.fileflow.utilities.FileFlowHelper;

public class PrefixMain {

	public static boolean DEBUG = false;

	private static final String TEST_SCRIPT = "scripts/test.ffa";

	public static void main(String[] args) throws FileNotFoundException, IOException {
		FlowPoint cfg = FileFlowHelper.generateControlFlowGraphFromFile(new File(TEST_SCRIPT));
		writeDOT(cfg);

		// perform prefix analysis
		PrefixAnalysisDomain domain = new PrefixAnalysisDomain();
		PrefixAnalysis analysis = new PrefixAnalysis();
		Analyzer<PrefixAnalysisDomain, PrefixAnalysis> analyzer = new Analyzer<>(domain, analysis);
		try {
			analyzer.analyze(cfg);
		} catch (AnalysisException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Generate DOT file before analysis
	 * 
	 * @param cfg
	 *            FlowPoint to represent entry point of the CFG
	 */
	private static void writeDOT(FlowPoint cfg) {
		String dot = GraphvizGenerator.generateDOT(cfg);
		GraphvizGenerator.saveDOTToFile(dot, TEST_SCRIPT + ".dot");
		System.out.println("DOT file written to: '" + TEST_SCRIPT + ".dot'");
		System.out.println();
	}

}
