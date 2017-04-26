package edu.utsa.fileflow.client.fileflow;

import edu.utsa.fileflow.analysis.AnalysisException;
import edu.utsa.fileflow.client.fileflow.tools.FFADriver;
import edu.utsa.fileflow.utilities.GraphvizGenerator;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Rodney on 4/26/2017.
 * <p>
 * This class tests various File Flow Analysis scripts.\
 * <p>
 * TODO: Test undefined variables
 */
public class FFATest {

	@Test
	public void testSimpleAnalysis() throws Exception {
		FFADriver driver = FFADriver.run("" +
				"$x0 = 'a';" +
				"touch $x0;"
		);
		FileStructure post = driver.ffaResult.post;
		GraphvizGenerator.saveDOTToFile(post.files.toDot(), "test/ffa/testSimpleAnalysis.dot");
		assertTrue("'/a' should exist", post.fileExists(new VariableAutomaton("a")));
	}

	@Test
	public void testAnalysis00() throws Exception {
		FFADriver driver = FFADriver.run("" +
				"$x0 = 'a';" +      // x0 = a
				"$x1 = 'b';" +      // x1 = b
				"$x2 = 'c';" +      // x2 = c
				"$x3 = $x0;" +      // x3 = a
				"$x3 = $x1;" +      // x3 = b
				"if (other) {" +    // BRANCH
				"   $x3 = $x2;" +   // x3 = c
				"}" +               // MERGE
				"$x4 = $x3;" +      // x4 = c
				"touch $x3;"        // touch (b | a)
		);
		FileStructure post = driver.ffaResult.post;
		GraphvizGenerator.saveDOTToFile(post.files.toDot(), "test/ffa/test_00.dot");
		assertTrue(post.fileExists(new VariableAutomaton("/b")));
		assertTrue(post.fileExists(new VariableAutomaton("/c")));
	}

	@Test
	public void testAnalysis01() throws Exception {
		FFADriver driver = FFADriver.run("" +
				"$x0 = '/';" +          // x0 = /
				"$x1 = 'a';" +          // x1 = a
				"mkdir $x1;" +          // mkdir a
				"$x1 = $x1.$x0;" +      // x1 = a/
				"$x2 = 'b';" +          // x2 = b
				"$x1 = $x1.$x2;" +      // x1 = a/b
				"touch $x1;"            // touch a/b
		);
		FileStructure post = driver.ffaResult.post;
		GraphvizGenerator.saveDOTToFile(post.files.toDot(), "test/ffa/test_01.dot");
		assertTrue(post.isDirectory(new VariableAutomaton("/a")));
		assertTrue(post.isRegularFile(new VariableAutomaton("/a/b")));
	}

	/**
	 * Cannot touch the same file twice
	 */
	@Test(expected = AnalysisException.class)
	public void testAnalysis02() throws Exception {
		FFADriver driver = FFADriver.run("" +
				"$x0 = 'a';" +
				"$x1 = 'b';" +
				"while (other) {" +
				"    $x0 = $x1;" +
				"    touch $x0;" +
				"}" +
				"touch $x1;"
		);
		FileStructure post = driver.ffaResult.post;
		GraphvizGenerator.saveDOTToFile(post.files.toDot(), "test/ffa/test_02.dot");
	}

	@Test
	public void testAnalysis03() throws Exception {
		FFADriver driver = FFADriver.run("" +
				"$x0 = 'a';" +
				"while (other) {" +
				"    $x0 = $x0.$x0;" +
				"    touch $x0;" +
				"}"
		);
		FileStructure post = driver.ffaResult.post;
		GraphvizGenerator.saveDOTToFile(post.files.toDot(), "test/ffa/test_03.dot");
		assertTrue(post.isRegularFile(new VariableAutomaton("/a")));
		assertTrue(post.isRegularFile(new VariableAutomaton("/aa")));
	}

	@Test
	public void testAnalysis04() throws Exception {
		FFADriver driver = FFADriver.run("" +
				"$x0 = 'a';" +
				"$x0 = $x0.$x0;" +
				"touch $x0;"
		);
		FileStructure post = driver.ffaResult.post;
		GraphvizGenerator.saveDOTToFile(post.files.toDot(), "test/ffa/test_04.dot");
		assertTrue(post.isRegularFile(new VariableAutomaton("/aa")));
		assertFalse(post.fileExists(new VariableAutomaton("/a")));
	}

}
