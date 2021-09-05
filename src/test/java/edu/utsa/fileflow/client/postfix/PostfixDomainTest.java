package edu.utsa.fileflow.client.postfix;

import edu.utsa.fileflow.analysis.Analysis;
import edu.utsa.fileflow.analysis.AnalysisException;
import edu.utsa.fileflow.analysis.Analyzer;
import edu.utsa.fileflow.cfg.FlowPoint;
import edu.utsa.fileflow.utilities.FileFlowHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class PostfixDomainTest {

    final PostfixAnalysisDomain FACTORY = new PostfixAnalysisDomain();

    // holds the text of a script to be analyzed
    StringBuilder script;

    /**
     * @throws java.lang.Exception
     */
    @BeforeEach
    public void setUp() throws Exception {
        // reset the script for before each test case
        script = new StringBuilder();
    }

    @Test
    public void testPostfixDomainClone() {
        PostfixAnalysisDomain d1 = FACTORY.bottom();

        // create a reference of $x0 and store it in domain d1
        d1.table.put("$x0", new PostfixItem("a"));

        // create a clone of d1, called d2, that should contain different
        // references of each prefix item
        PostfixAnalysisDomain d2 = d1.clone();

        // assert table contents are the same
        assertEquals(d1.table, d2.table);

        // assert prefix items are different objects in memory
        assertNotSame(d1.table.get("$x0"), d2.table.get("$x0"));

        // change d1 and make sure that it does not affect d2's values
        d1.table.get("$x0").setPostfix("modified");

        assertNotEquals(d1.table.get("$x0"), d2.table.get("$x0"));
    }

    @Test
    public void testPostfixDomainMerge() throws Exception {
        PostfixAnalysisDomain source = FACTORY.bottom();
        PostfixAnalysisDomain target = FACTORY.bottom();

        // $x0 = 'abc'
        source.table.put("$x0", new PostfixItem("abc"));
        // $x0 = 'ac'
        target.table.put("$x0", new PostfixItem("ac"));

        // merge and store result
        PostfixAnalysisDomain result = source.merge(target);
        // cut to common postfix
        // postfix = 'c'
        // anything before postfix is unknown

        // check answer
        PostfixItem expected = new PostfixItem("c");
        expected.setPostfix("c"); // this sets unknown to true; (*c)

        assertTrue(result.table.containsValue(expected));
    }

    @Test
    public void testScriptAssignment() throws Exception {
        s("$x0 = 'a';");
        PostfixAnalysisDomain result = getResult();
        assertEquals(new PostfixItem("a"), result.table.get("$x0"));
    }

    @Test
    public void testScriptBasicIfStat() throws Exception {
        s("$x0 = 'abc';");
        s("if (other) {");
        s("    $x0 = 'ac';");
        s("}");
        PostfixAnalysisDomain result = getResult();
        assertEquals(new PostfixItem("c", true), result.table.get("$x0"));
    }

    @Test
    public void testScriptMerge() throws Exception {
        s("$x0 = 'a';");
        s("$x1 = 'ac';");
        s("if (other) {");
        s("    $x1 = $x0.$x1;");
        s("}");
        PostfixAnalysisDomain result = getResult();
        assertEquals(new PostfixItem("a", false), result.table.get("$x0"));
        assertEquals(new PostfixItem("ac", true), result.table.get("$x1"));
    }

    @Test
    public void testScriptLoop() throws Exception {
        s("$x0 = 'ab';");
        s("$x1 = 't';");
        s("while (other) {");
        s("    $x0 = $x0.$x1;");
        s("}");
        PostfixAnalysisDomain result = getResult();
        assertEquals(new PostfixItem("", true), result.table.get("$x0"));
        assertEquals(new PostfixItem("t", false), result.table.get("$x1"));
    }

    @Test
    public void testScriptInput() throws Exception {
        s("$x0 = INPUT;");
        PostfixAnalysisDomain result = getResult();
        assertEquals(new PostfixItem("", true), result.table.get("$x0"));
    }

    @Test
    public void testScriptEmptyArray() throws Exception {
        s("$x0 = INPUT;");
        s("$x1[?] = [];");
        s("while (other) {");
        s("    $x1[?] = 'header-'.$x0;");
        s("    $x1[?] = $x1[?].'.txt';");
        s("}");
        s("$x2 = '/home/'.$x1[?];"); // $x1[?] may not be defined here

        Exception exception = assertThrows(AnalysisException.class, this::getResult);
        assertTrue(exception.getMessage().contains("Undefined PostfixItem; Cannot concat with BOTTOM."));
    }

    /**
     * Analyzes the script and returns the resulting domain.
     *
     * @return the result of the analysis on the script after exit.
     * @throws FileNotFoundException
     * @throws IOException
     * @throws AnalysisException
     */
    private PostfixAnalysisDomain getResult() throws Exception {
        FlowPoint cfg = FileFlowHelper.generateControlFlowGraphFromScript(script.toString());
        PostfixAnalysisDomain domain = new PostfixAnalysisDomain();
        PostfixAnalysis analysis = new PostfixAnalysis();
        Analyzer<PostfixAnalysisDomain, PostfixAnalysis> analyzer = new Analyzer<>(domain, analysis);
        return analyzer.analyze(cfg);
    }

    /**
     * Adds a new line to the script.
     *
     * @param line The line to be added to the script.
     */
    private void s(String line) {
        script.append(line);
        script.append("\n");
    }

}
