package edu.utsa.fileflow.client.prefix;

import org.antlr.v4.runtime.tree.TerminalNode;

import edu.utsa.fileflow.analysis.BaseAnalysis;
import edu.utsa.fileflow.antlr.FileFlowParser.AssignmentContext;
import edu.utsa.fileflow.antlr.FileFlowParser.ExpressionContext;
import edu.utsa.fileflow.antlr.FileFlowParser.ValueContext;
import edu.utsa.fileflow.cfg.FlowPointContext;

public class PrefixAnalysis extends BaseAnalysis<PrefixAnalysisDomain> {

	@Override
	public PrefixAnalysisDomain onFinish(PrefixAnalysisDomain domain) {
		System.out.println();
		System.out.println("***** ON FINISH *****");
		domain.table.forEach((k, v) -> System.out.printf("(%s.java): [%s : %s]\n", getClass().getSimpleName(), k, v));
		return domain;
	}

	@Override
	public PrefixAnalysisDomain onBefore(PrefixAnalysisDomain domain, FlowPointContext context) {
		domain.table.forEach((k, v) -> System.out.printf("(%s.java): [%s : %s]\n", getClass().getSimpleName(), k, v));
		System.out.println("==========================================================");
		return super.onAfter(domain, context);
	}

	@Override
	public PrefixAnalysisDomain enterAssignment(PrefixAnalysisDomain domain, FlowPointContext context) {
		AssignmentContext ctx = (AssignmentContext) context.getContext();
		ExpressionContext expr = ctx.expression();

		// variable name (key to update)
		String key = ctx.Variable().getText();
		PrefixItem t1 = null;
		PrefixItem t2 = null;

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
			t1 = new PrefixItem(term1.getText().substring(1, term1.getText().length() - 1));
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
				t2 = new PrefixItem(term2.getText().substring(1, term2.getText().length() - 1));
			}

			domain.table.put(key, t1.concat(t2));
		} else {
			domain.table.put(key, t1);
		}

		return domain;
	}

}
