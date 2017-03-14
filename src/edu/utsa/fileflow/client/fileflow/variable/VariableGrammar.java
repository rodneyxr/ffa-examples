package edu.utsa.fileflow.client.fileflow.variable;

import dk.brics.automaton.Automaton;
import dk.brics.string.grammar.Grammar;
import dk.brics.string.grammar.Nonterminal;
import dk.brics.string.grammar.operations.Grammar2MLFA;
import dk.brics.string.mlfa.MLFA;
import dk.brics.string.mlfa.operations.MLFA2Automaton;
import edu.utsa.fileflow.analysis.Mergeable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class is a wrapper for {@link dk.brics.string.grammar.Grammar}.
 * <p>
 * Created by Rodney on 2/12/2017.
 */
public class VariableGrammar implements Cloneable, Mergeable<VariableGrammar> {

    private Grammar grammar = new Grammar();

    // maps variables to nonterminals in the grammar
    private Map<Variable, Nonterminal> variables = new HashMap<>();
    private Set<Integer> inserted = new HashSet<>();
    public Set<Integer> visited = new HashSet<>();

    /* Grammar to MLFA to Automaton */
    private MLFA mlfa;
    private MLFA2Automaton m2a;
    private boolean isDirty = true; // true when the MLFA needs to be recomputed

    Nonterminal addNonterminal(Variable v) {
        isDirty = true;
        visited.add(v.id);
        Nonterminal nonterminal = grammar.addNonterminal(v.toString());
        variables.put(v, nonterminal);
        return nonterminal;
    }

    // $x0 = 'a';
    public void addAutomatonProduction(Variable v, Automaton a) {
        if (insert(v.id))
            grammar.addAutomatonProduction(variables.get(v), a);
    }

    // $x0 = $x1;
    public void addUnitProduction(Variable v0, Variable v1) {
        if (insert(v0.id)) {
            Nonterminal nt0 = variables.get(v0);
            Nonterminal nt1 = variables.get(v1);
            grammar.addUnitProduction(nt0, nt1);
        }
    }

    // $x0 = $x1.$x2;
    public void addPairProduction(Variable v0, Variable v1, Variable v2) {
        if (insert(v0.id)) {
            Nonterminal nt0 = variables.get(v0);
            Nonterminal nt1 = variables.get(v1);
            Nonterminal nt2 = variables.get(v2);
            grammar.addPairProduction(nt0, nt1, nt2);
        }
    }

    /**
     * Gets the variable from the grammar as an {@link Automaton}.
     *
     * @param variableSet The variable to get.
     * @return an {@link Automaton} representing the variable
     */
    public Automaton getVariable(Set<Variable> variableSet) {
        Automaton a = new Automaton();
        Grammar2MLFA g2m = new Grammar2MLFA(grammar);
        if (isDirty) {
            mlfa = g2m.convert();
            m2a = new MLFA2Automaton(mlfa);
            isDirty = false;
        }

        for (Variable v : variableSet) {
            System.out.println(variables.get(v));
            a = a.union(m2a.extract(g2m.getMLFAStatePair(variables.get(v))));
        }
        // for some reason, the extract invalidates the mlfa
        isDirty = true;

        return a;
    }

    /**
     * Inserts the ID of a variable into the inserted set. If it is not in the
     * set it will be added and isDirty will be set to true.
     *
     * @param id The ID of the variable to be added to the set.
     * @return true if the variable was inserted; false if it already exists.
     */
    private boolean insert(int id) {
        boolean added = inserted.add(id);
        if (added)
            isDirty = true;
        return added;
    }

    @Override
    public VariableGrammar merge(VariableGrammar other) {
        // all references of grammar are the same object
//		variables.putAll(other.variables);
//		inserted.addAll(other.inserted);
//		visited.addAll(other.visited);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VariableGrammar))
            return false;
        VariableGrammar other = (VariableGrammar) obj;
        if (!variables.equals(other.variables))
            return false;
        return inserted.equals(other.inserted);
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
        clone.inserted = inserted;
        // TODO: change putAll and addAll
//		clone.variables.putAll(variables);
//		clone.inserted.addAll(inserted);
//		clone.visited.addAll(visited);
        return clone;
    }

}
