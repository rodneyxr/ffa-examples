package edu.utsa.fileflow.client.fileflow.variable;

import dk.brics.automaton.Automaton;
import edu.utsa.fileflow.utilities.GraphvizGenerator;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

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
        Set<Variable> set = new HashSet<>();
        set.add(x0);
        Automaton a = g.getVariableValue(set);
        String dot = a.toDot();
//         GraphvizGenerator.saveDOTToFile(dot, "tmp/automaton.dot");
        assertThat(dot, CoreMatchers.containsString("[label=\"a\"]"));
        assertThat(dot, CoreMatchers.containsString("[shape=doublecircle,label=\"\"];"));
    }

}
