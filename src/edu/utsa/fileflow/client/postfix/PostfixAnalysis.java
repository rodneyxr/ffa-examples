package edu.utsa.fileflow.client.postfix;

import org.antlr.v4.runtime.tree.TerminalNode;

import edu.utsa.fileflow.analysis.BaseAnalysis;
import edu.utsa.fileflow.antlr.FileFlowParser.AssignmentContext;
import edu.utsa.fileflow.antlr.FileFlowParser.ExpressionContext;
import edu.utsa.fileflow.antlr.FileFlowParser.ValueContext;
import edu.utsa.fileflow.cfg.FlowPointContext;

public class PostfixAnalysis extends BaseAnalysis<PostfixAnalysisDomain> {

	@Override
	public PostfixAnalysisDomain onFinish(PostfixAnalysisDomain domain) {
		// domain.table.forEach((k, v) -> System.out.println(k + " : " + v));
		return domain;
	}

	@Override
	public PostfixAnalysisDomain onBefore(PostfixAnalysisDomain domain, FlowPointContext context) {
		domain.table.forEach((k, v) -> System.out.println(k + " : " + v));
		System.out.println("==========================================================");
		return super.onAfter(domain, context);
	}

	@Override
	public PostfixAnalysisDomain enterAssignment(PostfixAnalysisDomain domain, FlowPointContext context) {
		AssignmentContext ctx = (AssignmentContext) context.getContext();
		ExpressionContext expr = ctx.varValue().expression();
		if (expr == null) {
			expr = ctx.arrayValue().varValue().expression();
		}

		// variable name (key to update)
		String key = ctx.Variable().getText();
		PostfixItem t1 = null;
		PostfixItem t2 = null;

		// get the first term
		ValueContext val = expr.value(0);
		TerminalNode term1 = val.Variable();
		if (term1 != null) {
			// term1 is a variable
			t1 = domain.table.get(term1);
			if (t1 == null) {
				// FIXME: variable term1 not defined
				System.err.println("Analysis Error: variable '" + term1.getText() + "' not defined");
				System.exit(1);
			}
		} else {
			// term1 is a string
			term1 = val.String();
			t1 = new PostfixItem(term1.getText().substring(1, term1.getText().length() - 1));
		}

		// check for concatenation
		if (expr.value().size() == 2) {
			val = expr.value(1);
			TerminalNode term2 = val.Variable();
			if (term2 != null) {
				// term2 is a variable
				t2 = domain.table.get(term2);
				if (t2 == null) {
					// FIXME: variable term2 not defined
					System.err.println("Analysis Error: variable '" + term2.getText() + "' not defined");
					System.exit(1);
				}
			} else {
				// term2 is a string
				term2 = val.String();
				t2 = new PostfixItem(term2.getText().substring(1, term2.getText().length() - 1));
			}

			domain.table.put(key, t1.concat(t2));
		} else {
			domain.table.put(key, t1);
		}

		return domain;
	}

}
