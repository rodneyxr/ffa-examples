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
import java.util.List;

public class FileFlowAnalysisMain {

    public static boolean DEBUG = true;

    public static void main(String[] args) throws IOException {
        Analyzer.CONTINUE_ON_ERROR = true;
        Analyzer.VERBOSE = false;

        File dir = new File("C:\\Users\\Rodney\\Desktop\\dockerfiles");
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.toPath().toString().endsWith(".ffa")) {
                System.out.println(file);
                String saveDir = file.toPath().getFileName().toString().replaceAll("\\.ffa$", "");
                FlowPoint cfg = FileFlowHelper.generateControlFlowGraphFromFile(file);
                writeDOT(cfg, saveDir);
                System.out.println(saveDir);
                FFA ffa = new FFA(cfg);
                GraphvizGenerator.PATH_PREFIX = saveDir;
                ffa.run();
                GraphvizGenerator.PATH_PREFIX = "";
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
