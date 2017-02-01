package edu.utsa.fileflow.client.postfix;

import org.antlr.v4.runtime.tree.TerminalNode;

import edu.utsa.fileflow.analysis.AnalysisException;
import edu.utsa.fileflow.analysis.Analysis;
import edu.utsa.fileflow.antlr.FileFlowParser.ArrayValueContext;
import edu.utsa.fileflow.antlr.FileFlowParser.AssignmentContext;
import edu.utsa.fileflow.antlr.FileFlowParser.ExpressionContext;
import edu.utsa.fileflow.antlr.FileFlowParser.ValueContext;
import edu.utsa.fileflow.antlr.FileFlowParser.VarValueContext;
import edu.utsa.fileflow.cfg.FlowPointContext;

public class PostfixAnalysis extends Analysis<PostfixAnalysisDomain> {

	@Override
	public PostfixAnalysisDomain onFinish(PostfixAnalysisDomain domain) {
		if (PostfixMain.DEBUG) {
			System.out.println();
			System.out.println("***** ON FINISH *****");
			domain.table
					.forEach((k, v) -> System.out.printf("(%s.java): [%s : %s]\n", getClass().getSimpleName(), k, v));
		}
		return domain;
	}

	@Override
	public PostfixAnalysisDomain onBefore(PostfixAnalysisDomain domain, FlowPointContext context) {
		if (PostfixMain.DEBUG) {
			domain.table
					.forEach((k, v) -> System.out.printf("(%s.java): [%s : %s]\n", getClass().getSimpleName(), k, v));
			System.out.println("==========================================================");
		}
		return domain;
	}

	@Override
	public PostfixAnalysisDomain enterAssignment(PostfixAnalysisDomain domain, FlowPointContext context)
			throws AnalysisException {
		AssignmentContext ctx = (AssignmentContext) context.getContext();
		VarValueContext var = ctx.varValue();
		ArrayValueContext arr = ctx.arrayValue();

		// true if variable on left side is an array
		boolean hasIndex = (ctx.Index() != null);

		ExpressionContext expr = null;
		TerminalNode input = null;
		TerminalNode empty = null;

		// if assignment variable is array and has index
		if (hasIndex) {
			if (arr.varValue() == null) {
				empty = arr.EmptyValue();
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
		if (hasIndex)
			key = key + ctx.Index().getText();

		PostfixItem t1 = null;
		PostfixItem t2 = null;

		// if array variable is assigned to empty array
		if (empty != null) {
			t1 = PostfixItem.bottom();
			PostfixItem old = domain.table.get(key);
			if (old != null) {
				domain.table.put(key, t1.concat(old));
			} else {
				domain.table.put(key, t1);
			}
			return domain;
		}

		// if user input is required
		if (input != null) {
			// INPUT is equivalent to '*'
			PostfixItem val = new PostfixItem("", true);
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
			t1 = new PostfixItem(term1.getText());
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
				t2 = new PostfixItem(term2.getText());
			}

			PostfixItem old = domain.table.get(key);
			if (old != null && hasIndex) {
				domain.table.put(key, t1.concat(t2).merge(old));
			} else
				domain.table.put(key, t1.concat(t2));
		} else {
			PostfixItem old = domain.table.get(key);
			if (old != null && hasIndex)
				t1.merge(old);
			domain.table.put(key, t1);
		}

		return domain;
	}

}
