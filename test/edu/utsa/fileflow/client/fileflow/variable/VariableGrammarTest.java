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
 * This class tests the functionality of {@link VariableGrammar}.
 */
public class VariableGrammarTest {

    @Test
    public void testGrammarToAutomaton() throws Exception {
        // $x0 = 'a';
        Grammar g = new Grammar();
        Nonterminal x0 = g.addNonterminal();
        g.addAutomatonProduction(x0, Automaton.makeString("a"));
        MLFA mlfa = new Grammar2MLFA(g).convert();
        System.out.println(mlfa);
        List<MLFAState> states = mlfa.getStates();
        Automaton a = new MLFA2Automaton(mlfa).extract(new MLFAStatePair(states.get(1), states.get(0)));
        GraphvizGenerator.saveDOTToFile(a.toDot(), "tmp/automaton.dot");
    }

    @Test
    public void testVariableGrammar() throws Exception {
        VariableGrammar g = new VariableGrammar();
        g.addNonterminal("$x0");
        g.addAutomatonProduction("$x0", Automaton.makeString("a"));
        System.out.println(g.grammar.getCharsets());
    }
}
