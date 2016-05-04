package edu.utsa.fileflow.client.prefix;

import edu.utsa.fileflow.analysis.Analysis;
import edu.utsa.fileflow.analysis.AnalysisDomain;
import edu.utsa.fileflow.antlr.FileFlowParser.AssignmentContext;
import edu.utsa.fileflow.cfg.FlowPointContext;

public class PrefixAnalysis implements Analysis<PrefixAnalysisDomain> {

	@Override
	public AnalysisDomain onBefore(PrefixAnalysisDomain domain, FlowPointContext context) {
		// TODO Auto-generated method stub
		return domain;
	}

	@Override
	public AnalysisDomain onAfter(PrefixAnalysisDomain domain, FlowPointContext context) {
		// TODO Auto-generated method stub
		return domain;
	}

	@Override
	public AnalysisDomain touch(PrefixAnalysisDomain domain, FlowPointContext context) {
		// TODO Auto-generated method stub
		return domain;
	}

	@Override
	public AnalysisDomain mkdir(PrefixAnalysisDomain domain, FlowPointContext context) {
		// TODO Auto-generated method stub
		return domain;
	}

	@Override
	public AnalysisDomain copy(PrefixAnalysisDomain domain, FlowPointContext context) {
		// TODO Auto-generated method stub
		return domain;
	}

	@Override
	public AnalysisDomain remove(PrefixAnalysisDomain domain, FlowPointContext context) {
		// TODO Auto-generated method stub
		return domain;
	}

	@Override
	public AnalysisDomain enterProg(PrefixAnalysisDomain domain, FlowPointContext context) {
		// TODO Auto-generated method stub
		return domain;
	}

	@Override
	public AnalysisDomain exitProg(PrefixAnalysisDomain domain, FlowPointContext context) {
		// TODO Auto-generated method stub
		return domain;
	}

	@Override
	public AnalysisDomain enterAssignment(PrefixAnalysisDomain domain, FlowPointContext context) {
		// TODO Auto-generated method stub
		AssignmentContext ctx = (AssignmentContext) context.getContext();
		System.out.println(ctx.getText());
		return domain;
	}

	@Override
	public AnalysisDomain enterWhileStatement(PrefixAnalysisDomain domain, FlowPointContext context) {
		// TODO Auto-generated method stub
		return domain;
	}

	@Override
	public AnalysisDomain exitWhileStatement(PrefixAnalysisDomain domain, FlowPointContext context) {
		// TODO Auto-generated method stub
		return domain;
	}

}
