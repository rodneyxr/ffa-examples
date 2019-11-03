package edu.utsa.fileflow.client.fileflow;

import edu.utsa.fileflow.analysis.Analyzer;
import edu.utsa.fileflow.cfg.FlowPoint;
import edu.utsa.fileflow.utilities.FileFlowHelper;
import edu.utsa.fileflow.utilities.GraphvizGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileFlowAnalysisMain {
    private static boolean DEBUG = true;

    public static void main(String[] args) throws IOException {
        Analyzer.CONTINUE_ON_ERROR = true;
        Analyzer.VERBOSE = false;

        // Check arguments
        if (args.length != 1) {
            System.err.println("usage: prog <file/directory>");
            System.exit(1);
        }
        File file = new File(args[0]);
        File[] files;
        if (file.isDirectory()) {
            files = file.listFiles();
        } else {
            files = new File[]{file};
        }

        // Make sure files were found
        if (files == null) {
            System.err.println("no '.ffa' files were found");
            System.exit(1);
        }
        for (File f : files) {
            if (f.toPath().toString().endsWith(".ffa")) {
                System.out.println(f);
                String saveDir = f.toPath().getFileName().toString().replaceAll("\\.ffa$", "");
                FlowPoint cfg = FileFlowHelper.generateControlFlowGraphFromFile(f);
                writeDOT(cfg, saveDir);
                System.out.println(saveDir);
                FFA ffa = new FFA(cfg);
                GraphvizGenerator.PATH_PREFIX = saveDir;
                ffa.run();
                GraphvizGenerator.PATH_PREFIX = "";

                String timeResults = String.format("Variable analysis elapsed time: %dms\n", ffa.variableElapsedTime) +
                        String.format("Grammar analysis elapsed time: %dms\n", ffa.grammarElapsedTime) +
                        String.format("FFA first run elapsed time: %dms\n", ffa.ffaElapsedTime1) +
                        String.format("FFA second run elapsed time: %dms\n", ffa.ffaElapsedTime2);
                Files.write(Paths.get("dot", saveDir, "time.txt"), timeResults.getBytes());
                if (FileFlowAnalysisMain.DEBUG)
                    System.out.println(timeResults);
            }
        }
    }

    /**
     * Generate DOT file before analysis
     *
     * @param cfg FlowPoint to represent entry point of the CFG
     */
    private static void writeDOT(FlowPoint cfg, String filepath) {
        String dot = GraphvizGenerator.generateDOT(cfg);
        Path path = Paths.get("dot", filepath);
        path.toFile().mkdirs();
        GraphvizGenerator.saveDOTToFile(dot, Paths.get(filepath, "cfg.dot").toString());
        if (FileFlowAnalysisMain.DEBUG) {
            System.out.println("DOT file written to: " + Paths.get(path.toString(), "cfg.dot"));
            System.out.println();
        }
    }

}
