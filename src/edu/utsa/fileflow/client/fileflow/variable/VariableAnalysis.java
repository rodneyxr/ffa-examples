package edu.utsa.fileflow.client.fileflow.variable;

import edu.utsa.fileflow.analysis.Analysis;
import edu.utsa.fileflow.analysis.AnalysisException;
import edu.utsa.fileflow.cfg.FlowPointContext;
import edu.utsa.fileflow.client.AssignContext;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class overrides some methods that the analysis framework will call when
 * traversing the control flow graph of a script.
 * <p>
 * Created by Rodney on 2/11/2017.
 */
public class VariableAnalysis extends Analysis<VariableAnalysisDomain> {

	static Logger logger = Logger.getLogger("VariableAnalysis");

	@Override
	public VariableAnalysisDomain onFinish(VariableAnalysisDomain domain) throws AnalysisException {
		logger.log(Level.INFO, "\nLive Variables: {0}", domain.liveVariables);
		return domain;
	}

	@Override
	public VariableAnalysisDomain onBefore(VariableAnalysisDomain domain, FlowPointContext context) throws AnalysisException {
		return super.onBefore(domain, context);
	}

	@Override
	public VariableAnalysisDomain onAfter(VariableAnalysisDomain domain, FlowPointContext context) throws AnalysisException {
//		context.getFlowPoint().setDomain(domain);
		return super.onAfter(domain, context);
	}

	@Override
	public VariableAnalysisDomain enterAssignment(VariableAnalysisDomain domain, FlowPointContext context) throws AnalysisException {
		AssignContext ctx = new AssignContext(context);
		int flowpointID = context.getFlowPoint().id;
		Variable v0 = new Variable(ctx.var0, flowpointID);
		domain.liveVariables.addVariable(v0);

		// FIXME: this should be handled in the grammar
		if (ctx.literal != null && (ctx.var1 != null || ctx.var2 != null)) {
			throw new AnalysisException("literal and var cannot be both defined.");
		}

		return domain;
	}

}
