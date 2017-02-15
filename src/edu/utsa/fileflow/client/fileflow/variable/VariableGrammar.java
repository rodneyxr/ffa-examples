package edu.utsa.fileflow.client.fileflow.variable;

import dk.brics.automaton.Automaton;
import dk.brics.string.grammar.Grammar;
import dk.brics.string.grammar.Nonterminal;
import dk.brics.string.grammar.operations.Grammar2MLFA;
import dk.brics.string.mlfa.MLFA;
import dk.brics.string.mlfa.MLFAState;
import dk.brics.string.mlfa.MLFAStatePair;
import dk.brics.string.mlfa.operations.MLFA2Automaton;
import edu.utsa.fileflow.utilities.GraphvizGenerator;
import org.junit.Test;

import java.util.List;

/**
 * Created by Rodney on 2/12/2017.
 * <p>
 * This class is a wrapper for {@link dk.brics.string.grammar.Grammar}.
 */
public class VariableGrammar {

    Grammar grammar = new Grammar();

    @Test
    public void addUnitProduction() throws Exception {
        Nonterminal nt0 = grammar.addNonterminal();
        Nonterminal nt1 = grammar.addNonterminal();
        grammar.addUnitProduction(nt0, nt1);
        System.out.println(grammar.getNonterminals());
        MLFA mlfa = new Grammar2MLFA(grammar).convert();
        System.out.println(mlfa.toString());
        List<MLFAState> states = mlfa.getStates();
        Automaton a = new MLFA2Automaton(mlfa).extract(new MLFAStatePair(states.get(3), states.get(2)));
        GraphvizGenerator.saveDOTToFile(a.toDot(), "test/grammar.dot");
    }

}
