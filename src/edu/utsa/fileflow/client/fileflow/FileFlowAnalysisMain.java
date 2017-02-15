package edu.utsa.fileflow.client.fileflow;

import edu.utsa.fileflow.analysis.AnalysisException;
import edu.utsa.fileflow.analysis.Analyzer;
import edu.utsa.fileflow.cfg.FlowPoint;
import edu.utsa.fileflow.utilities.FileFlowHelper;
import edu.utsa.fileflow.utilities.GraphvizGenerator;

import java.io.File;
import java.io.IOException;

public class FileFlowAnalysisMain {

    public static boolean DEBUG = true;
    private static final String TEST_SCRIPT = "scripts/test.ffa";

    public static void main(String[] args) throws IOException {
        FlowPoint cfg = FileFlowHelper.generateControlFlowGraphFromFile(new File(TEST_SCRIPT));
        writeDOT(cfg);

        // perform prefix analysis
        FileFlowAnalysisDomain domain = new FileFlowAnalysisDomain();
        FileFlowAnalysis analysis = new FileFlowAnalysis();
        Analyzer<FileFlowAnalysisDomain, FileFlowAnalysis> analyzer = new Analyzer<>(domain, analysis);
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
     * @param cfg FlowPoint to represent entry point of the CFG
     */
    private static void writeDOT(FlowPoint cfg) {
        String dot = GraphvizGenerator.generateDOT(cfg);
        GraphvizGenerator.saveDOTToFile(dot, TEST_SCRIPT + ".cfg.dot");
        if (FileFlowAnalysisMain.DEBUG) {
            System.out.println("DOT file written to: 'dot/" + TEST_SCRIPT + ".dot'");
            System.out.println();
        }
    }

}
