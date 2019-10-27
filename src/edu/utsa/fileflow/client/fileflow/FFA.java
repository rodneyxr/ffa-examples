package edu.utsa.fileflow.client.fileflow;

import edu.utsa.fileflow.analysis.AnalysisException;
import edu.utsa.fileflow.analysis.Analyzer;
import edu.utsa.fileflow.cfg.FlowPoint;
import edu.utsa.fileflow.client.fileflow.grammar.GrammarAnalysis;
import edu.utsa.fileflow.client.fileflow.grammar.GrammarAnalysisDomain;
import edu.utsa.fileflow.client.fileflow.variable.VariableAnalysis;
import edu.utsa.fileflow.client.fileflow.variable.VariableAnalysisDomain;

public class FFA {
    FlowPoint cfg;

    /* Variable Analyzer */
    VariableAnalysisDomain variableAnalysisDomain;
    VariableAnalysis variableAnalysis;
    Analyzer<VariableAnalysisDomain, VariableAnalysis> variableAnalyzer;

    /* Grammar Analysis */
    GrammarAnalysisDomain grammarDomain;
    GrammarAnalysis grammarAnalysis;
    Analyzer<GrammarAnalysisDomain, GrammarAnalysis> grammarAnalyzer;

    /* File Flow Analysis */
    FileFlowAnalysisDomain ffaDomain;
    FileFlowAnalysis ffaAnalysis;
    Analyzer<FileFlowAnalysisDomain, FileFlowAnalysis> ffaAnalyzer;

    FFA(FlowPoint cfg) {
//        FlowPoint cfg = FileFlowHelper.generateControlFlowGraphFromFile(new File(filepath));
//        writeDOT(cfg);
        this.cfg = cfg;
    }

    void run() {
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

        try {
            variableAnalyzer.analyze(cfg);
            grammarAnalyzer.analyze(cfg);
            ffaAnalyzer.analyze(cfg);

            // Run again
            ffaAnalyzer.analyze(cfg);
        } catch (AnalysisException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
