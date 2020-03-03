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
    long variableElapsedTime = 0L;

    /* Grammar Analysis */
    GrammarAnalysisDomain grammarDomain;
    GrammarAnalysis grammarAnalysis;
    Analyzer<GrammarAnalysisDomain, GrammarAnalysis> grammarAnalyzer;
    long grammarElapsedTime = 0L;

    /* File Flow Analysis */
    FileFlowAnalysisDomain ffaDomain;
    FileFlowAnalysis ffaAnalysis;
    Analyzer<FileFlowAnalysisDomain, FileFlowAnalysis> ffaAnalyzer;
    long ffaElapsedTime1 = 0L;
    long ffaElapsedTime2 = 0L;

    FFA(FlowPoint cfg) {
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
            long start = System.currentTimeMillis();
            variableAnalyzer.analyze(cfg);
            variableElapsedTime = System.currentTimeMillis() - start;

            start = System.currentTimeMillis();
            grammarAnalyzer.analyze(cfg);
            grammarElapsedTime = System.currentTimeMillis() - start;

            start = System.currentTimeMillis();
            ffaAnalyzer.analyze(cfg);
            ffaElapsedTime1 = System.currentTimeMillis() - start;

            // Run again
            start = System.currentTimeMillis();
            ffaAnalyzer.analyze(cfg);
            ffaElapsedTime2 = System.currentTimeMillis() - start;
        } catch (AnalysisException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
