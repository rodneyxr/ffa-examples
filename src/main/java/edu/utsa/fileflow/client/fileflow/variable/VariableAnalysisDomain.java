package edu.utsa.fileflow.client.fileflow.variable;

import dk.brics.automaton.Automaton;
import edu.utsa.fileflow.analysis.AnalysisDomain;

/**
 * This class is analysis domain for variable analysis. It will track all live variables in the entire program.
 * <p>
 * Created by Rodney on 2/11/2017.
 */
public class VariableAnalysisDomain extends AnalysisDomain<VariableAnalysisDomain> {

	static {
		Automaton.setMinimization(Automaton.MINIMIZE_BRZOZOWSKI);
		Automaton.setMinimizeAlways(true);
	}

	LiveVariableMap liveVariables = new LiveVariableMap();

	public LiveVariableMap getLiveVariables() {
		return liveVariables;
	}

	@Override
	public VariableAnalysisDomain merge(VariableAnalysisDomain other) {
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
		bottom.liveVariables = new LiveVariableMap();
		return bottom;
	}

	@Override
	public int compareTo(VariableAnalysisDomain o) {
		if (!liveVariables.equals(o.liveVariables))
			return 1;
		return 0;
	}

	@Override
	public VariableAnalysisDomain clone() {
		VariableAnalysisDomain clone = bottom();
		clone.liveVariables = liveVariables.clone();
		return clone;
	}

}
