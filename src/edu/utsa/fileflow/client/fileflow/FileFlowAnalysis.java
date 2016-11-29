package edu.utsa.fileflow.client.fileflow;

import dk.brics.automaton.Automaton;
import edu.utsa.fileflow.analysis.AnalysisException;
import edu.utsa.fileflow.analysis.BaseAnalysis;
import edu.utsa.fileflow.antlr.FileFlowParser.ExpressionContext;
import edu.utsa.fileflow.antlr.FileFlowParser.FunctionCallContext;
import edu.utsa.fileflow.antlr.FileFlowParser.ValueContext;
import edu.utsa.fileflow.cfg.FlowPointContext;
import edu.utsa.fileflow.testutils.GraphvizGenerator;

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
		// NOTE: touch $x0.$x1 not supported
		ValueContext v1, v2 = null;
		Automaton s1 = null, s2 = Automaton.makeEmpty();
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
			s1 = FileStructure.makeFileAutomaton(v1.String().getText());
		} else { // if v1 is a variable
			s1 = domain.table.get(v1.String().getText());
		}

		// set s2 if not null
		if (v2 != null) {
			if (v2.Variable() == null) { // if v2 is a string
				// FIXME: Should be a regular automaton, not a file path
				s2 = FileStructure.makeFileAutomaton(v2.String().getText());
			} else { // if v2 is a variable
				s2 = domain.table.get(v2.String().getText());
			}
		}

		// get the value of the expression
		Automaton value = domain.post.files.union(s1);
		if (!s2.isEmpty())
			value = value.concatenate(s2);

		// add the automaton to the
		domain.post.createFile(value);

		return domain;
	}

}
