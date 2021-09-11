package edu.utsa.fileflow.client.fileflow.variable;

import edu.utsa.fileflow.analysis.Analyzer;
import edu.utsa.fileflow.cfg.FlowPoint;
import edu.utsa.fileflow.utilities.FileFlowHelper;
import org.junit.jupiter.api.Test;

/**
 * This class tests the functionality of the {@link VariableAnalysis} class.
 * <p>
 * Created by Rodney on 3/27/2017.
 */
public class VariableAnalysisTest {

	@Test
	public void testSimpleScript() throws Exception {
		FlowPoint cfg = FileFlowHelper.generateControlFlowGraphFromScript(
				"" +
						"$x0 = 'a';" +
						"$x1 = 'b';" +
						"$x2 = 'c';" +
						"$x3 = $x0;" +
						"$x3 = $x1;" +
						"if (other) {" +
						"   $x3 = $x2;" +
						"}" +
						"$x4 = $x3;"
		);
		VariableAnalysisDomain variableAnalysisDomain = new VariableAnalysisDomain();
		VariableAnalysis variableAnalysis = new VariableAnalysis();
		Analyzer<VariableAnalysisDomain, VariableAnalysis> variableAnalyzer = new Analyzer<>(variableAnalysisDomain, variableAnalysis);
		VariableAnalysisDomain variableResult = variableAnalyzer.analyze(cfg);
		LiveVariableMap liveVariables = variableResult.getLiveVariables();
	}

}
