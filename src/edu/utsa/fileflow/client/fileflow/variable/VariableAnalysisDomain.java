package edu.utsa.fileflow.client.fileflow.variable;

import edu.utsa.fileflow.analysis.AnalysisDomain;

/**
 * This class is analysis domain for variable analysis. It holds a grammar which
 * will track all productions and live variables in the entire program.
 * <p>
 * Created by Rodney on 2/11/2017.
 */
public class VariableAnalysisDomain extends AnalysisDomain<VariableAnalysisDomain> {

	VariableGrammar grammar = new VariableGrammar();
	LiveVariableMap liveVariables = new LiveVariableMap();

	@Override
	public VariableAnalysisDomain merge(VariableAnalysisDomain other) {
		// TODO: merge grammar
		liveVariables.merge(other.liveVariables);
		return this;
	}

	@Override
	public VariableAnalysisDomain top() {
		// TODO: implement a top rather than just a new domain
		return new VariableAnalysisDomain();
	}

	@Override
	public VariableAnalysisDomain bottom() {
		VariableAnalysisDomain bottom = new VariableAnalysisDomain();
		bottom.grammar = new VariableGrammar();
		bottom.liveVariables = new LiveVariableMap();
		return bottom;
	}

	@Override
	public int compareTo(VariableAnalysisDomain o) {
		if (!grammar.equals(o.grammar))
			return 1;
		if (!liveVariables.equals(o.liveVariables))
			return 1;
		return 0;
	}

	@Override
	public VariableAnalysisDomain clone() {
		VariableAnalysisDomain clone = bottom();
		clone.liveVariables = liveVariables.clone();
		clone.grammar = grammar.clone();
		return clone;
	}

}
