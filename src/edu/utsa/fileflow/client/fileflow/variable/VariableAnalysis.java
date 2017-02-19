package edu.utsa.fileflow.client.fileflow.variable;

import edu.utsa.fileflow.analysis.Analysis;
import edu.utsa.fileflow.analysis.AnalysisException;
import edu.utsa.fileflow.antlr.FileFlowParser.*;
import edu.utsa.fileflow.cfg.FlowPointContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Rodney on 2/11/2017.
 * <p>
 * This class overrides some methods that the analysis framework will call when
 * traversing the control flow graph of a script.
 */
public class VariableAnalysis extends Analysis<VariableAnalysisDomain> {

	private Logger logger = Logger.getLogger("VariableAnalysis");

	@Override
	public VariableAnalysisDomain onFinish(VariableAnalysisDomain domain) throws AnalysisException {
//        System.out.println("VariableAnalysis.onFinish: " + domain.liveVariables);
		logger.log(Level.INFO, "\nLive Variables: {0}", domain.liveVariables);
		return domain;
	}

	/**
	 * Supported Operations:
	 * var = var
	 * var = var.var
	 * var = '$literal'
	 */
	@Override
	public VariableAnalysisDomain enterAssignment(VariableAnalysisDomain domain, FlowPointContext context) throws AnalysisException {
		AssignContext ctx = new AssignContext(context);
		Variable v = new Variable(ctx.var0, context.getFlowPoint().getID());
		domain.liveVariables.addVariable(v);
		return domain;
	}

	class AssignContext {
		final String var0;
		final String var1;
		final String var2;
		final String literal;
		final boolean isConcat;
		final boolean isArray;
		final boolean isEmptyArray;
		final boolean isInput;

		public AssignContext(FlowPointContext fpctx) {
			String $var0 = null;
			String $var1 = null;
			String $var2 = null;
			String $literal = null;
			boolean $isConcat = false;
			boolean $isArray;
			boolean $isEmptyArray;
			boolean $isInput;

			AssignmentContext ctx = (AssignmentContext) fpctx.getContext();
			VarValueContext var = ctx.varValue();
			ArrayValueContext arr = ctx.arrayValue();

			// true if variable on left side is an array
			$isArray = (ctx.Index() != null);

			ExpressionContext expr = null;
			TerminalNode input = null;
			TerminalNode emptyArray = null;

			// if assignment variable is array and has index
			if ($isArray) {
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
			if ($isArray)
				key = key + ctx.Index().getText();
			$var0 = key;

			// if array variable is assigned to empty array
			$isEmptyArray = emptyArray != null;

			// if user input is required
			$isInput = input != null;

			if (!$isEmptyArray && !$isInput) {

				// get the first term
				ValueContext term1 = expr.value(0);
				if (term1.Variable() != null) {
					// term1 is a variable
					String text = term1.Variable().getText();
					if (term1.Index() != null)
						text += term1.Index().getText();
					$var1 = text;
				} else {
					// term1 is a $literal
					$literal = term1.String().getText();
				}

				// check for concatenation
				if (expr.value().size() == 2) {
					ValueContext term2 = expr.value(1);
					if (term2.Variable() != null) {
						// term2 is a variable
						String text = term2.Variable().getText();
						if (term2.Index() != null)
							text += term2.Index().getText();
						$var2 = text;
					} else {
						// term2 is a string
						// this.$var2 = term2.String().getText();
						System.err.println("VariableAnalysis Error: $var2 cannot be a $literal.");
						System.exit(1);
					}

					// concatenate $var1 and $var2
					$isConcat = true;
				}
			}

			this.var0 = $var0;
			this.var1 = $var1;
			this.var2 = $var2;
			this.literal = $literal;
			this.isConcat = $isConcat;
			this.isArray = $isArray;
			this.isEmptyArray = $isEmptyArray;
			this.isInput = $isInput;
		}

	}
}
