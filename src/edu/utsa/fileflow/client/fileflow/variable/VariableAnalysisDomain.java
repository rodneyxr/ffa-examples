package edu.utsa.fileflow.client.fileflow.variable;

import dk.brics.automaton.Automaton;
import edu.utsa.fileflow.analysis.AnalysisDomain;

import java.util.Set;

/**
 * This class is analysis domain for variable analysis. It holds a grammar which
 * will track all productions and live variables in the entire program.
 * <p>
 * Created by Rodney on 2/11/2017.
 */
public class VariableAnalysisDomain extends AnalysisDomain<VariableAnalysisDomain> {

	VariableGrammar grammar = new VariableGrammar();
	LiveVariableMap liveVariables = new LiveVariableMap();

	/**
	 * Gets the variable value as an automaton from the grammar.
	 *
	 * @param variable The variable name to get the value of.
	 * @return an {@link Automaton} of the variable value or <code>null</code> if undefined.
	 */
	public Automaton getVariable(String variable) {
		Set<Variable> v = liveVariables.getVariable(variable);
		if (v == null)
			return null;
		return grammar.getVariable(v);
	}

	@Override
	public VariableAnalysisDomain merge(VariableAnalysisDomain other) {
		grammar.merge(other.grammar);
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
