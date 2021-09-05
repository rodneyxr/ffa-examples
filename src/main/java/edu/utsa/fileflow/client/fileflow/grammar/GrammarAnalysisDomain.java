package edu.utsa.fileflow.client.fileflow.grammar;

import dk.brics.automaton.Automaton;
import edu.utsa.fileflow.analysis.AnalysisDomain;
import edu.utsa.fileflow.client.fileflow.variable.LiveVariableMap;
import edu.utsa.fileflow.client.fileflow.variable.Variable;

import java.util.Set;

/**
 * This class is analysis domain for grammar section of the file flow analysis. It holds a grammar which
 * will track all productions in the entire program.
 * <p>
 * Created by Rodney on 3/27/2017.
 */
public class GrammarAnalysisDomain extends AnalysisDomain<GrammarAnalysisDomain> {

	static {
		Automaton.setMinimization(Automaton.MINIMIZE_BRZOZOWSKI);
		Automaton.setMinimizeAlways(true);
	}

	VariableGrammar grammar = new VariableGrammar();

	/**
	 * Gets the variable value as an automaton from the grammar.
	 *
	 * @param variable The variable name to get the value of.
	 * @return an {@link Automaton} of the variable value or <code>null</code> if undefined.
	 */
	public Automaton getVariable(String variable, LiveVariableMap liveVariables) {
		Set<Variable> v = liveVariables.getVariable(variable);
		if (v == null)
			return null;
		return grammar.getVariableValue(v);
	}

	@Override
	public GrammarAnalysisDomain merge(GrammarAnalysisDomain other) {
		grammar.merge(other.grammar);
		return this;
	}

	@Override
	public GrammarAnalysisDomain top() {
		// TODO: implement a top rather than just a new domain
		return new GrammarAnalysisDomain();
	}

	@Override
	public GrammarAnalysisDomain bottom() {
		GrammarAnalysisDomain bottom = new GrammarAnalysisDomain();
		bottom.grammar = new VariableGrammar();
		return bottom;
	}

	@Override
	public int compareTo(GrammarAnalysisDomain o) {
		if (!grammar.equals(o.grammar))
			return 1;
		return 0;
	}

	@Override
	public GrammarAnalysisDomain clone() {
		GrammarAnalysisDomain clone = bottom();
		clone.grammar = grammar.clone();
		return clone;
	}
}
