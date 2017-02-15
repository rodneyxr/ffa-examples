package edu.utsa.fileflow.client.fileflow.variable;

import edu.utsa.fileflow.analysis.Analysis;
import edu.utsa.fileflow.analysis.AnalysisException;
import edu.utsa.fileflow.cfg.FlowPointContext;

/**
 * Created by Rodney on 2/11/2017.
 * <p>
 * This class overrides some methods that the analysis framework will call when
 * traversing the control flow graph of a script.
 */
public class VariableAnalysis extends Analysis<VariableAnalysisDomain> {

    @Override
    public VariableAnalysisDomain enterAssignment(VariableAnalysisDomain domain, FlowPointContext context) throws AnalysisException {
        
        return domain;
    }
}
