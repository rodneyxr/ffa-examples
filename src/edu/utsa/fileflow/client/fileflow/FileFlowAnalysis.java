package edu.utsa.fileflow.client.fileflow;

import edu.utsa.fileflow.analysis.Analysis;
import edu.utsa.fileflow.analysis.AnalysisException;
import edu.utsa.fileflow.antlr.FileFlowParser.FunctionCallContext;
import edu.utsa.fileflow.antlr.FileFlowParser.ValueContext;
import edu.utsa.fileflow.cfg.FlowPointContext;
import edu.utsa.fileflow.client.AssignContext;
import edu.utsa.fileflow.client.fileflow.grammar.GrammarAnalysisDomain;
import edu.utsa.fileflow.client.fileflow.variable.VariableAnalysisDomain;
import edu.utsa.fileflow.utilities.GraphvizGenerator;

public class FileFlowAnalysis extends Analysis<FileFlowAnalysisDomain> {

	private GrammarAnalysisDomain gDomain;
	private VariableAnalysisDomain vDomain;

	@Override
	public FileFlowAnalysisDomain onFinish(FileFlowAnalysisDomain domain) throws AnalysisException {
		// write automaton to DOT file
		GraphvizGenerator.saveDOTToFile(domain.post.toDot(), "scripts/automaton.dot");
		return domain;
	}

	@Override
	public FileFlowAnalysisDomain onBefore(FileFlowAnalysisDomain domain, FlowPointContext context) throws AnalysisException {
		gDomain = (GrammarAnalysisDomain) context.getFlowPoint().getDomain(GrammarAnalysisDomain.class);
		vDomain = (VariableAnalysisDomain) context.getFlowPoint().getDomain(VariableAnalysisDomain.class);
		return super.onBefore(domain, context);
	}

	@Override
	public FileFlowAnalysisDomain touch(FileFlowAnalysisDomain domain, FlowPointContext context) throws AnalysisException {
		VariableAutomaton va = getValue(domain, context, 0);

		// add the automaton to the file structure
		try {
			domain.post.createFile(va);
		} catch (FileStructureException e) {
			throw new AnalysisException(e.getMessage());
		}

		return domain;
	}

	@Override
	public FileFlowAnalysisDomain mkdir(FileFlowAnalysisDomain domain, FlowPointContext context) throws AnalysisException {
		VariableAutomaton va = getValue(domain, context, 0);

		// add the automaton to the file structure
		try {
			domain.post.createDirectory(va);
		} catch (FileStructureException e) {
			throw new AnalysisException(e.getMessage());
		}

		return domain;
	}

	@Override
	public FileFlowAnalysisDomain copy(FileFlowAnalysisDomain domain, FlowPointContext context) throws AnalysisException {
		VariableAutomaton v1 = getValue(domain, context, 0);
		VariableAutomaton v2 = getValue(domain, context, 1);

		try {
			domain.post.copy(v1, v2);
		} catch (FileStructureException e) {
			throw new AnalysisException(e.getMessage());
		}

		return domain;
	}

	@Override
	public FileFlowAnalysisDomain remove(FileFlowAnalysisDomain domain, FlowPointContext context) throws AnalysisException {
		VariableAutomaton va = getValue(domain, context, 0);

		// remove the automaton from the file structure
		try {
			domain.post.removeFile(va);
		} catch (FileStructureException e) {
			throw new AnalysisException(e.getMessage());
		}

		return domain;
	}

	@Override
	public FileFlowAnalysisDomain removeRecursive(FileFlowAnalysisDomain domain, FlowPointContext context) throws AnalysisException {
		VariableAutomaton va = getValue(domain, context, 0);

		// remove the automaton from the file structure
		try {
			domain.post.removeFileRecursive(va);
		} catch (FileStructureException e) {
			throw new AnalysisException(e.getMessage());
		}

		return domain;
	}

	@Override
	public FileFlowAnalysisDomain assertFunc(FileFlowAnalysisDomain domain, FlowPointContext context) throws AnalysisException {
		FunctionCallContext ctx = (FunctionCallContext) context.getContext();
		String s1 = null, s2 = null;
		if (ctx.condition().children.size() == 2) {
			s1 = ctx.condition().getChild(0).getText();
			s2 = ctx.condition().getChild(1).getText();
		}
		if (s1 == null || s2 == null) {
			System.err.println("Invalid command");
			return domain;
		}

		if (s1.equals("exists")) {
			VariableAutomaton va = domain.table.get(s2);
			boolean exists = domain.post.fileExists(va);
			if (!exists) {
				System.out.printf("WARNING: '%s**' does not exist\n", va);
			}
		} else if (s1.equals("!") && s2.startsWith("exists")) {
			String b = ctx.condition().getChild(1).getChild(1).getText();
			VariableAutomaton va = domain.table.get(b);
			boolean exists = domain.post.fileExists(va);
			if (exists) {
				System.out.printf("WARNING: '%s**' exists\n", va);
			}
		} else {
			System.err.println("Invalid assertion.");
			return domain;
		}
		return domain;
	}

	@Override
	public FileFlowAnalysisDomain enterAssignment(FileFlowAnalysisDomain domain, FlowPointContext context) throws AnalysisException {
		AssignContext ctx = new AssignContext(context);
		domain.table.put(ctx.var0, new VariableAutomaton(gDomain.getVariable(ctx.var0, vDomain.getLiveVariables())));
		// TODO: handle arrays and user INPUT
		return super.enterAssignment(domain, context);
	}

	/**
	 * Gets the nth parameter of a function call.
	 */
	private VariableAutomaton getValue(FileFlowAnalysisDomain domain, FlowPointContext context, int n) {
		FunctionCallContext ctx = (FunctionCallContext) context.getContext();
		ValueContext v = ctx.value(n);

		// get the variable from the symbol table or create a new one
		if (v.Variable() != null) // if v is a variable
			return domain.table.get(v.Variable().getText());

		// v is a string literal
		return new VariableAutomaton(v.String().getText());
	}

}
