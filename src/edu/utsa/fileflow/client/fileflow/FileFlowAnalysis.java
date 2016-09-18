package edu.utsa.fileflow.client.fileflow;

import dk.brics.automaton.Automaton;
import edu.utsa.fileflow.analysis.AnalysisException;
import edu.utsa.fileflow.analysis.BaseAnalysis;
import edu.utsa.fileflow.antlr.FileFlowParser.ExpressionContext;
import edu.utsa.fileflow.antlr.FileFlowParser.FunctionCallContext;
import edu.utsa.fileflow.antlr.FileFlowParser.ValueContext;
import edu.utsa.fileflow.cfg.FlowPointContext;
import edu.utsa.fileflow.testutils.GraphvizGenerator;

//domain.files = Automaton.makeString("root");
//domain.files = domain.files.concatenate(Automaton.makeString("file1"));
//domain.files = domain.files.union(Automaton.makeString("rootfile1"));
//GraphvizGenerator.saveDOTToFile(domain.files.toDot(), "automaton.dot");

public class FileFlowAnalysis extends BaseAnalysis<FileFlowAnalysisDomain> {

	@Override
	public FileFlowAnalysisDomain onFinish(FileFlowAnalysisDomain domain) throws AnalysisException {
		// write automaton to DOT file
		GraphvizGenerator.saveDOTToFile(domain.post.toDot(), "automaton.dot");
		return domain;
	}

	@Override
	public FileFlowAnalysisDomain touch(FileFlowAnalysisDomain domain, FlowPointContext context)
			throws AnalysisException {
		ValueContext v1, v2 = null;
		String s1 = null, s2 = "";
		{
			// touch only accepts one expression
			FunctionCallContext ctx = (FunctionCallContext) context.getContext();
			ExpressionContext expr = ctx.expression(0);
			v1 = expr.value(0);
			if (expr.value().size() == 2)
				v2 = expr.value(1);
		}

		// set s1
		if (v1.Variable() == null) { // if v1 is a string
			s1 = v1.String().getText();
		} else { // if v1 is a variable

		}

		// set s2 if not null
		if (v2 != null) {
			if (v2.Variable() == null) { // if v2 is a string
				s2 = v2.String().getText();
			} else { // if v2 is a variable

			}
		}
		
		String value = "/" + s1 + s2;
		domain.post = domain.post.union(Automaton.makeString(value));
		return domain;
	}

}
