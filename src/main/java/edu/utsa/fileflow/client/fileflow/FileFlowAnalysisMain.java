package edu.utsa.fileflow.client.fileflow;

import edu.utsa.fileflow.analysis.Analyzer;
import edu.utsa.fileflow.cfg.FlowPoint;
import edu.utsa.fileflow.utilities.FileFlowHelper;
import edu.utsa.fileflow.utilities.GraphvizGenerator;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class FileFlowAnalysisMain {
    private static boolean DEBUG = true;

    /**
     * Parses the command line arguments.
     *
     * @param args The command line arguments
     * @return The arguments as a HashMap
     */
    private static HashMap<String, String> parseArguments(String[] args) {
        HashMap<String, String> map = new HashMap<>();

        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                String key = args[i];
                try {
                    String value = args[i + 1];
                    map.put(key, value);
                } catch (IndexOutOfBoundsException e) {
                    System.err.printf("Flag has no value: %s", key);
                    System.exit(1);
                }
                i++; // Skip the next value
            }
        }
        return map;
    }

    public static void main(String[] args) {
        Analyzer.CONTINUE_ON_ERROR = true;
        Analyzer.VERBOSE = false;

        // Parse the arguments
        HashMap<String, String> map = parseArguments(args);

        // Get the directory or file to analyze
        String scanPath = map.get("--files");
        if (scanPath == null) {
            System.err.println("Missing required flag: --files <file/directory>");
            System.exit(1);
        }

        // Get the precondition
        String preconditionDir = map.get("--precondition");

        File file = new File(scanPath);
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
                try {
                    FlowPoint cfg = FileFlowHelper.generateControlFlowGraphFromFile(f);
                    writeDOT(cfg, saveDir);
                    FFA ffa = new FFA(cfg);
                    GraphvizGenerator.PATH_PREFIX = saveDir;
                    if (preconditionDir != null) {
                        ffa.runUsingSystemPath(preconditionDir);
                    } else {
                        ffa.run();
                    }
                    GraphvizGenerator.PATH_PREFIX = "";

                    String timeResults = String.format("Variable analysis elapsed time: %dms\n", ffa.variableElapsedTime) +
                            String.format("Grammar analysis elapsed time: %dms\n", ffa.grammarElapsedTime) +
                            String.format("FFA first run elapsed time: %dms\n", ffa.ffaElapsedTime1) +
                            String.format("FFA second run elapsed time: %dms\n", ffa.ffaElapsedTime2);
                    Files.write(Paths.get("dot", saveDir, "time.txt"), timeResults.getBytes());
                    if (FileFlowAnalysisMain.DEBUG)
                        System.out.println(timeResults);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("error: failed to analyze " + f);
                }
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
