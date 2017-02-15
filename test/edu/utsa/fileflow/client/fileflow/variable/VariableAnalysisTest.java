package edu.utsa.fileflow.client.fileflow.variable;

import edu.utsa.fileflow.analysis.Analyzer;
import edu.utsa.fileflow.cfg.FlowPoint;
import edu.utsa.fileflow.utilities.FileFlowHelper;
import org.junit.Test;

/**
 * Created by Rodney on 2/11/2017.
 * <p>
 * This class tests functionality of the variable analysis.
 */
public class VariableAnalysisTest {

    @Test
    public void testAnalysis() throws Exception {
        VariableAnalysisDomain domain = new VariableAnalysisDomain();
        VariableAnalysis analysis = new VariableAnalysis();
        Analyzer<VariableAnalysisDomain, VariableAnalysis> analyzer = new Analyzer<>(domain, analysis);
        FlowPoint cfg = FileFlowHelper.generateControlFlowGraphFromScript("$x0='a';");
        VariableAnalysisDomain result = analyzer.analyze(cfg);
        System.out.println(result.liveVariables);
    }

}
