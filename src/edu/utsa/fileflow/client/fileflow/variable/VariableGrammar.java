package edu.utsa.fileflow.client.fileflow.variable;

import dk.brics.automaton.Automaton;
import dk.brics.string.grammar.Grammar;
import dk.brics.string.grammar.Nonterminal;
import dk.brics.string.grammar.operations.Grammar2MLFA;
import dk.brics.string.mlfa.MLFA;
import dk.brics.string.mlfa.operations.MLFA2Automaton;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rodney on 2/12/2017.
 * <p>
 * This class is a wrapper for {@link dk.brics.string.grammar.Grammar}.
 */
public class VariableGrammar implements Cloneable {

    Grammar grammar = new Grammar();

    // maps variables to nonterminals in the grammar
    private Map<Variable, Nonterminal> variables = new HashMap<>();

    /* Grammar to MLFA to Automaton */
    private MLFA mlfa;
    private MLFA2Automaton m2a;
    private boolean isDirty = true; // true when the MLFA needs to be recomputed

    public Nonterminal addNonterminal(Variable v) {
        isDirty = true;
        Nonterminal nonterminal = grammar.addNonterminal();
        variables.put(v, nonterminal);
        return nonterminal;
    }

    // $x0 = 'a';
    public void addAutomatonProduction(Variable v, Automaton a) {
        isDirty = true;
        grammar.addAutomatonProduction(variables.get(v), a);
    }

    // $x0 = $x1;
    public void addUnitProduction(Variable v1, Variable v2) {
        isDirty = true;
        Nonterminal nt1 = variables.get(v1);
        Nonterminal nt2 = variables.get(v2);
        grammar.addUnitProduction(nt1, nt2);
    }

    // $x0 = $x1.$x2;
    public void addPairProduction(Variable v1, Variable v2, Variable v3) {
        isDirty = true;
        Nonterminal nt1 = variables.get(v1);
        Nonterminal nt2 = variables.get(v2);
        Nonterminal nt3 = variables.get(v3);
        grammar.addPairProduction(nt1, nt2, nt3);
    }

    /**
     * Gets the variable from the grammar as an {@link Automaton}.
     *
     * @param v The variable to get.
     * @return an {@link Automaton} representing the variable
     */
    public Automaton getVariable(Variable v) {
        Grammar2MLFA g2m = new Grammar2MLFA(grammar);
        if (isDirty) {
            mlfa = g2m.convert();
            m2a = new MLFA2Automaton(mlfa);
            isDirty = false;
        }
        return m2a.extract(g2m.getMLFAStatePair(variables.get(v)));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VariableGrammar))
            return false;
        VariableGrammar other = (VariableGrammar) obj;
        return grammar.getNonterminals().equals(other.grammar.getNonterminals());
    }

    @Override
    public VariableGrammar clone() {
        VariableGrammar clone = new VariableGrammar();
        clone.grammar = grammar.copy();
        return clone;
    }

}
