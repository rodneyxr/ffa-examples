package edu.utsa.fileflow.client.fileflow;

import edu.utsa.fileflow.analysis.AnalysisException;
import edu.utsa.fileflow.analysis.BaseAnalysis;
import edu.utsa.fileflow.antlr.FileFlowParser.FunctionCallContext;
import edu.utsa.fileflow.antlr.FileFlowParser.ValueContext;
import edu.utsa.fileflow.cfg.FlowPointContext;
import edu.utsa.fileflow.testutils.GraphvizGenerator;

public class FileFlowAnalysis extends BaseAnalysis<FileFlowAnalysisDomain> {

	@Override
	public FileFlowAnalysisDomain onFinish(FileFlowAnalysisDomain domain) throws AnalysisException {
		// write automaton to DOT file
		GraphvizGenerator.saveDOTToFile(domain.post.toDot(), "scripts/automaton.dot");
		return domain;
	}

	@Override
	public FileFlowAnalysisDomain touch(FileFlowAnalysisDomain domain, FlowPointContext context)
			throws AnalysisException {
		// touch $x0
		// touch 'file'
		// touch only accepts one value (ex: `touch $x0` or `touch 'file'`)
		FunctionCallContext ctx = (FunctionCallContext) context.getContext();
		ValueContext v = ctx.value(0);
		VariableAutomaton va;

		// get the variable from the symbol table or create a new one
		if (v.Variable() != null) { // if v is a variable
			throw new RuntimeException("Variables are not yet implemented.");
			// va = domain.table.get(v.String().getText());
			// va = new VariableAutomaton(v.String().getText());
		} else { // if v is a string literal
			va = new VariableAutomaton(v.String().getText());
		}

		// add the automaton to the
		domain.post.createFile(va);

		return domain;
	}

}
