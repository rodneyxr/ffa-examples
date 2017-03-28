package edu.utsa.fileflow.client.fileflow.grammar;

import dk.brics.automaton.Automaton;
import dk.brics.string.grammar.Grammar;
import dk.brics.string.grammar.Nonterminal;
import dk.brics.string.grammar.operations.Grammar2MLFA;
import dk.brics.string.mlfa.MLFA;
import dk.brics.string.mlfa.operations.MLFA2Automaton;
import edu.utsa.fileflow.analysis.Mergeable;
import edu.utsa.fileflow.client.fileflow.variable.Variable;

import java.util.*;

/**
 * This class is a wrapper for {@link dk.brics.string.grammar.Grammar}.
 * <p>
 * Created by Rodney on 2/12/2017.
 */
public class VariableGrammar implements Cloneable, Mergeable<VariableGrammar> {

	/**
	 * Contains a set of FlowPoint ID's. A new ID is usually added when a new
	 * nonterminal is inserted. Checking if visited.contains(FlowPoint.id) will
	 * tell if a nonterminal for this node has been inserted in the Grammar.
	 */
	Set<Integer> visited = new HashSet<>();

	/* The JSA Grammar that this class wraps */
	private Grammar grammar = new Grammar();

	/* Maps variables to nonterminals in the grammar */
	private Map<Variable, Nonterminal> variables = new HashMap<>();

	/**
	 * @return the set of visited FlowPoint ID's
	 */
	public Set<Integer> getVisited() {
		return Collections.unmodifiableSet(visited);
	}

	/**
	 * Gets the variable's value from the grammar as an {@link Automaton}.
	 *
	 * @param variableSet The set of live variables that represent the target variable.
	 * @return an {@link Automaton} representing the variable's literal value.
	 */
	public Automaton getVariableValue(Set<Variable> variableSet) {
		Automaton a = new Automaton();
		Grammar2MLFA g2m = new Grammar2MLFA(grammar);
		MLFA mlfa = g2m.convert();
		MLFA2Automaton m2a = new MLFA2Automaton(mlfa);

		for (Variable v : variableSet) {
			a = a.union(m2a.extract(g2m.getMLFAStatePair(variables.get(v))));
		}

		return a;
	}

	Nonterminal addNonterminal(Variable v) {
		visited.add(v.id);
		Nonterminal nonterminal = grammar.addNonterminal(v.toString());
		variables.put(v, nonterminal);
		return nonterminal;
	}

	// $x0 = 'a';
	void addAutomatonProduction(Variable v, Automaton a) {
		grammar.addAutomatonProduction(variables.get(v), a);
	}

	// $x0 = $x1;
	void addUnitProduction(Variable v0, Variable v1) {
		Nonterminal nt0 = variables.get(v0);
		Nonterminal nt1 = variables.get(v1);
		grammar.addUnitProduction(nt0, nt1);
	}

	// $x0 = $x1.$x2;
	void addPairProduction(Variable v0, Variable v1, Variable v2) {
		Nonterminal nt0 = variables.get(v0);
		Nonterminal nt1 = variables.get(v1);
		Nonterminal nt2 = variables.get(v2);
		grammar.addPairProduction(nt0, nt1, nt2);
	}

	/**
	 * No merge implementation is necessary since there is only one instance
	 * of Grammar throughout the analysis.
	 */
	@Override
	public VariableGrammar merge(VariableGrammar other) {
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof VariableGrammar))
			return false;
		VariableGrammar other = (VariableGrammar) obj;
		return variables.equals(other.variables) && visited.equals(other.visited);
	}

	@Override
	public String toString() {
		return grammar.toString();
	}

	@Override
	public VariableGrammar clone() {
		VariableGrammar clone = new VariableGrammar();
		clone.grammar = grammar;
		clone.visited = visited;
		clone.variables = variables;
		return clone;
	}

}
