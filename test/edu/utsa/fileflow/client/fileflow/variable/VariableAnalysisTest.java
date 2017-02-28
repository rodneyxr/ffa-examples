package edu.utsa.fileflow.client.fileflow.variable;

import dk.brics.automaton.Automaton;
import edu.utsa.fileflow.analysis.Analyzer;
import edu.utsa.fileflow.cfg.FlowPoint;
import edu.utsa.fileflow.utilities.FileFlowHelper;
import edu.utsa.fileflow.utilities.GraphvizGenerator;
import org.junit.Test;

/**
 * This class tests functionality of the variable analysis.
 * <p>
 * Created by Rodney on 2/11/2017.
 */
public class VariableAnalysisTest {

	@Test
	public void testAnalysis() throws Exception {
		VariableAnalysisDomain domain = new VariableAnalysisDomain();
		VariableAnalysis analysis = new VariableAnalysis();
		Analyzer<VariableAnalysisDomain, VariableAnalysis> analyzer = new Analyzer<>(domain, analysis);
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
		VariableAnalysisDomain result = analyzer.analyze(cfg);
//		Automaton a = result.grammar.getVariable(new Variable("$x4", 8));
//		GraphvizGenerator.saveDOTToFile(a.toDot(), "tmp/var_analysis.dot");
	}

}
