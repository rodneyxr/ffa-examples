package edu.utsa.fileflow.client.fileflow;

import edu.utsa.fileflow.analysis.AnalysisException;
import edu.utsa.fileflow.analysis.Analyzer;
import edu.utsa.fileflow.cfg.FlowPoint;
import edu.utsa.fileflow.client.fileflow.grammar.GrammarAnalysis;
import edu.utsa.fileflow.client.fileflow.grammar.GrammarAnalysisDomain;
import edu.utsa.fileflow.client.fileflow.variable.VariableAnalysis;
import edu.utsa.fileflow.client.fileflow.variable.VariableAnalysisDomain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    void runUsingSystemPath(String filepath) throws IOException {
        FileStructure init = new FileStructure();
        String prefix = "/home/user/";
        Path systemPath = Paths.get(filepath);
        String rootPath = systemPath.toString().replace(systemPath.getParent().toString(), prefix) + "/";

        // Iterate through each file under the provided system path
        Files.walk(systemPath).forEach(x -> {
            String newpath = x.toString().replace(systemPath.getParent().toString(), prefix);
            String[] levels = newpath.replace(prefix, "").split("[/\\\\]");
            if (levels.length > 3) {
                if (levels[2].equals(".git")) {
                    return;
                }
            }

            if (Files.isDirectory(x))
                newpath += "/";
            VariableAutomaton va = new VariableAutomaton(newpath);
            init.forceCreate(va);
        });

        // Set the root path for the ffa script
        init.changeWorkingDirectory(new VariableAutomaton(rootPath));
        runWithPrecondition(init);
    }

    void run() {
        runWithPrecondition(null);
    }

    void runWithPrecondition(FileStructure precondition) {
        /* Variable Analysis */
        variableAnalysisDomain = new VariableAnalysisDomain();
        variableAnalysis = new VariableAnalysis();
        variableAnalyzer = new Analyzer<>(variableAnalysisDomain, variableAnalysis);

        /* Grammar Analysis */
        grammarDomain = new GrammarAnalysisDomain();
        grammarAnalysis = new GrammarAnalysis();
        grammarAnalyzer = new Analyzer<>(grammarDomain, grammarAnalysis);

        /* File Flow Analysis */
        if (precondition == null) {
            ffaDomain = new FileFlowAnalysisDomain();
            ffaAnalysis = new FileFlowAnalysis();
        } else {
            ffaDomain = new FileFlowAnalysisDomain(precondition);
            ffaAnalysis = new FileFlowAnalysis(precondition);
        }
        ffaAnalyzer = new Analyzer<>(ffaDomain, ffaAnalysis);

        try {
            long start = System.currentTimeMillis();
            variableAnalyzer.analyze(cfg);
            variableElapsedTime = System.currentTimeMillis() - start;

            start = System.currentTimeMillis();
            grammarAnalyzer.analyze(cfg);
            grammarElapsedTime = System.currentTimeMillis() - start;

            if (precondition == null) {
                // The post-condition of the first run will be used as the pre-condition of the second run
                start = System.currentTimeMillis();
                ffaAnalyzer.analyze(cfg);
                ffaElapsedTime1 = System.currentTimeMillis() - start;
            }

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
