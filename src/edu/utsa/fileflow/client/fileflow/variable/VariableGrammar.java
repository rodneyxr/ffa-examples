package edu.utsa.fileflow.client.fileflow.variable;

import dk.brics.automaton.Automaton;
import dk.brics.string.grammar.Grammar;
import dk.brics.string.grammar.Nonterminal;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rodney on 2/12/2017.
 * <p>
 * This class is a wrapper for {@link dk.brics.string.grammar.Grammar}.
 */
public class VariableGrammar {

    Grammar grammar = new Grammar();

    // maps variables -> nonterminal keys in the grammar
    Map<String, Nonterminal> variables = new HashMap<>();

    public Nonterminal addNonterminal(String variable) {
        Nonterminal nonterminal = grammar.addNonterminal();
        variables.put(variable, nonterminal);
        return nonterminal;
    }

    // $x0 = 'a';
    public void addAutomatonProduction(String v, Automaton a) {
        grammar.addAutomatonProduction(variables.get(v), a);
    }

    // $x0 = $x1;
    public void addUnitProduction(String v1, String v2) {
        Nonterminal nt1 = variables.get(v1);
        Nonterminal nt2 = variables.get(v2);
        grammar.addUnitProduction(nt1, nt2);
    }

    // $x0 = $x1.$x2;
    public void addPairProduction(String v1, String v2, String v3) {
        Nonterminal nt1 = variables.get(v1);
        Nonterminal nt2 = variables.get(v2);
        Nonterminal nt3 = variables.get(v3);
        grammar.addPairProduction(nt1, nt2, nt3);
    }

}
