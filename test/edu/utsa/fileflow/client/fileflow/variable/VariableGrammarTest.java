package edu.utsa.fileflow.client.fileflow.variable;

import dk.brics.automaton.Automaton;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.junit.Assert.assertThat;

/**
 * Created by Rodney on 2/12/2017.
 * <p>
 * This class tests the functionality of {@link VariableGrammar}.
 */
public class VariableGrammarTest {

    @Test
    public void testGetVariable() throws Exception {
        // $x0 = 'a';
        VariableGrammar g = new VariableGrammar();
        Variable x0 = new Variable("$x0", 0);
        g.addNonterminal(x0);
        g.addAutomatonProduction(x0, Automaton.makeString("a"));
        Automaton a = g.getVariable(x0);
        String dot = a.toDot();
        // GraphvizGenerator.saveDOTToFile(dot, "tmp/automaton.dot");
        assertThat(dot, CoreMatchers.containsString("initial -> 0"));
        assertThat(dot, CoreMatchers.containsString("0 -> 1 [label=\"a\"]"));
        assertThat(dot, CoreMatchers.containsString("1 [shape=doublecircle,label=\"\"];"));
    }

}
