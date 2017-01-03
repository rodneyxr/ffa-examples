package edu.utsa.fileflow.client.fileflow;

import org.antlr.v4.runtime.tree.TerminalNode;

import edu.utsa.fileflow.analysis.AnalysisException;
import edu.utsa.fileflow.analysis.BaseAnalysis;
import edu.utsa.fileflow.antlr.FileFlowParser.ArrayValueContext;
import edu.utsa.fileflow.antlr.FileFlowParser.AssignmentContext;
import edu.utsa.fileflow.antlr.FileFlowParser.ExpressionContext;
import edu.utsa.fileflow.antlr.FileFlowParser.FunctionCallContext;
import edu.utsa.fileflow.antlr.FileFlowParser.ValueContext;
import edu.utsa.fileflow.antlr.FileFlowParser.VarValueContext;
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
			va = domain.table.get(v.Variable().getText());
		} else { // if v is a string literal
			va = new VariableAutomaton(v.String().getText());
		}

		// add the automaton to the
		domain.post.createFile(va);

		return domain;
	}

	@Override
	public FileFlowAnalysisDomain enterAssignment(FileFlowAnalysisDomain domain, FlowPointContext context)
			throws AnalysisException {
		// assignment
		// : Variable Index '=' arrayValue
		// | Variable '=' varValue

		AssignmentContext ctx = (AssignmentContext) context.getContext();
		VarValueContext var = ctx.varValue();
		ArrayValueContext arr = ctx.arrayValue();

		// true if variable on left side is an array
		boolean isArray = (ctx.Index() != null);

		ExpressionContext expr = null;
		TerminalNode input = null;
		TerminalNode emptyArray = null;

		// if assignment variable is array and has index
		if (isArray) {
			if (arr.varValue() == null) {
				emptyArray = arr.EmptyValue();
			} else {
				expr = arr.varValue().expression();
				input = arr.varValue().Input();
			}
		} else {
			// if assignment is a regular variable with no index
			expr = var.expression();
			input = var.Input();
		}

		// variable name (key to update)
		String key = ctx.Variable().getText();
		if (isArray)
			key = key + ctx.Index().getText();

		VariableAutomaton t1 = null;
		VariableAutomaton t2 = null;

		// if array variable is assigned to empty array
		if (emptyArray != null) {
			t1 = VariableAutomaton.bottom();
			VariableAutomaton old = domain.table.get(key);
			if (old != null) {
				// domain.table.put(key, t1.concat(old)) // t1.concat(old)
			} else {
				domain.table.put(key, t1);
			}
			return domain;
		}

		// if user input is required
		if (input != null) {
			// INPUT is equivalent to '*'
			VariableAutomaton val = VariableAutomaton.top();
			domain.table.put(key, val);
			return domain;
		}

		// get the first term
		ValueContext val = expr.value(0);
		TerminalNode term1 = val.Variable();
		if (term1 != null) {
			// term1 is a variable
			String term1Text = term1.getText();
			if (val.Index() != null)
				term1Text += val.Index().getText();
			t1 = domain.table.get(term1Text);
			if (t1 == null) {
				// TODO: throw exception
				System.err.println("Analysis Error: variable '" + term1Text + "' not defined");
				System.exit(1);
			}
		} else {
			// term1 is a string
			term1 = val.String();
			t1 = new VariableAutomaton(term1.getText());
		}

		// check for concatenation
		if (expr.value().size() == 2) {
			val = expr.value(1);
			TerminalNode term2 = val.Variable();
			if (term2 != null) {
				// term2 is a variable
				String term2Text = term2.getText();
				if (val.Index() != null)
					term2Text += val.Index().getText();
				t2 = domain.table.get(term2Text);
				if (t2 == null) {
					// TODO: throw exception
					System.err.println("Analysis Error: variable '" + term2Text + "' not defined");
					System.exit(1);
				}
			} else {
				// term2 is a string
				term2 = val.String();
				// TODO: concat before making automaton?
				t2 = new VariableAutomaton(term2.getText());
			}

			VariableAutomaton old = domain.table.get(key);
			if (old != null && isArray) {
				// merge -> union
				domain.table.put(key, t1.concat(t2).union(old));
			} else
				domain.table.put(key, t1.concat(t2));
		} else {
			VariableAutomaton old = domain.table.get(key);
			if (old != null && isArray)
				t1.union(old); // merge -> union
			domain.table.put(key, t1);
		}

		return domain;
	}

}
