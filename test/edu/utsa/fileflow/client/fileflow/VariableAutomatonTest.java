package edu.utsa.fileflow.client.fileflow;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import dk.brics.automaton.Automaton;
import edu.utsa.fileflow.testutils.GraphvizGenerator;

public class VariableAutomatonTest {

	@Test
	public void testCreateVariableAutomaton() {
		VariableAutomaton a = new VariableAutomaton("/file1");
		GraphvizGenerator.saveDOTToFile(a.getAutomaton().toDot(), "test/create_var_auto.dot");
	}

	@Test
	public void testConcatVariableAutomaton() {
//		Automaton x0 = Automaton.makeString("/file1");
//		Automaton x1 = Automaton.makeString(".txt");
//		Automaton x2 = Automaton.makeString("file2");
//		Automaton v = x0.concatenate(x1);
//		v = v.concatenate(x2);
		VariableAutomaton v1 = new VariableAutomaton("/dir1/");
		VariableAutomaton v2 = new VariableAutomaton("/file1");
		VariableAutomaton v = v1.concat(v2);
		GraphvizGenerator.saveDOTToFile(v.getAutomaton().toDot(), "test/concat_var_auto.dot");
	}

	@Test
	public void testEndsWith() {
		VariableAutomaton v = new VariableAutomaton("dir1");
		// check if v ends with separator
		assertFalse(v.endsWith(Automaton.makeChar('/')));
		assertTrue(v.endsWith(Automaton.makeChar('1')));
	}

	@Test
	public void testStartsWith() {
		VariableAutomaton v = new VariableAutomaton("/dir1");
		// check if v starts with separator
		assertFalse(v.startsWith(Automaton.makeChar('x')));
		assertTrue(v.startsWith(Automaton.makeChar('/')));
	}

}
