package edu.utsa.fileflow.client.fileflow.variable;

import dk.brics.string.grammar.Grammar;
import edu.utsa.fileflow.analysis.AnalysisDomain;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Rodney on 2/11/2017.
 * <p>
 * This class contains the analysis domain for variable analysis. It holds a grammar which w
 */
public class VariableAnalysisDomain extends AnalysisDomain<VariableAnalysisDomain> {

    Grammar grammar;
    Set<Variable> liveVariables;

    @Override
    public VariableAnalysisDomain merge(VariableAnalysisDomain domain) {
        return null;
    }

    @Override
    public VariableAnalysisDomain top() {
        // TODO: implement a top rather than just a new domain
        return new VariableAnalysisDomain();
    }

    @Override
    public VariableAnalysisDomain bottom() {
        VariableAnalysisDomain bottom = new VariableAnalysisDomain();
        bottom.grammar = new Grammar();
        bottom.liveVariables = new HashSet<>();
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
        return null;
    }

}
