package edu.utsa.fileflow.client.fileflow;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;
import edu.utsa.fileflow.utilities.GraphvizGenerator;

public class VariableAutomatonTest {

	@Before
	public void setUp() throws Exception {
		Automaton.setMinimization(Automaton.MINIMIZE_BRZOZOWSKI);
		Automaton.setMinimizeAlways(true);
	}

	@Test
	public void testCreateVariableAutomaton() {
		VariableAutomaton a = new VariableAutomaton("/file1");
		GraphvizGenerator.saveDOTToFile(a.getAutomaton().toDot(), "test/create_var_auto.dot");
	}

	@Test
	public void testConcatVariableAutomaton() {
		VariableAutomaton v1 = new VariableAutomaton("/dir1/");
		VariableAutomaton v2 = new VariableAutomaton("/file1");
		VariableAutomaton v = v1.concatenate(v2);
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

	@Test
	public void testGetPathToFileComplex() {
		RegExp reg = new RegExp("(/dir[1-9]*/q)|(/df)");
		VariableAutomaton va = new VariableAutomaton(reg.toAutomaton());
		Automaton a = va.getSeparatedAutomaton();
		GraphvizGenerator.saveDOTToFile(a.toDot(), "test/complex.orig.dot");
		assertTrue(a.run("/df"));

		a = va.getParentDirectory().getSeparatedAutomaton();
		GraphvizGenerator.saveDOTToFile(a.toDot(), "test/complex.dot");
		assertTrue(a.run("/dir1/"));
		assertTrue(a.run("/dir4/"));
		assertTrue(a.run("/dir9/"));
		assertFalse(a.run("/df"));
	}

	@Test
	public void testGetPathToFileSingleton() {
		VariableAutomaton va = new VariableAutomaton("/dir1/file1");
		Automaton a = va.getSeparatedAutomaton();
		GraphvizGenerator.saveDOTToFile(a.toDot(), "test/singleton.va.orig.dot");
		assertTrue(a.run("/dir1/file1"));
		assertFalse(a.run("/dir1/file1/"));

		a = va.getParentDirectory().getSeparatedAutomaton();
		GraphvizGenerator.saveDOTToFile(a.toDot(), "test/singleton.dot");
		assertTrue(a.run("/dir1/"));
		assertFalse(a.run("/dir1/file1"));
	}

	@Test
	public void testGetPathToFileInRoot() throws Exception {
		VariableAutomaton va = new VariableAutomaton("/test");
		Automaton a = va.getSeparatedAutomaton();
		GraphvizGenerator.saveDOTToFile(a.toDot(), "test/file_in_root.orig.dot");
		assertTrue(a.run("/test"));

		a = va.getParentDirectory().getSeparatedAutomaton();
		GraphvizGenerator.saveDOTToFile(a.toDot(), "test/file_in_root.dot");
		assertTrue(a.run("/"));
		assertFalse(a.run("/test"));
	}

	@Test
	public void testGetPathToDirectoryInRoot() throws FileStructureException {
		VariableAutomaton va = new VariableAutomaton("/home/");
		Automaton a = va.getSeparatedAutomaton();
		GraphvizGenerator.saveDOTToFile(a.toDot(), "test/dir_in_root.orig.dot");
		assertTrue(a.run("/home/"));

		a = va.getParentDirectory().getSeparatedAutomaton();
		GraphvizGenerator.saveDOTToFile(a.toDot(), "test/dir_in_root.dot");
		assertTrue(a.run("/"));
		assertFalse(a.run("/home/"));
	}

	@Test
	public void testTransitionToNull() {
		Automaton a = Automaton.makeChar('/');
		State s0 = a.getInitialState().getTransitions().toArray(new Transition[1])[0].getDest();
		assertTrue(s0.getTransitions().isEmpty());
	}

	@Test
	public void testJoinPaths() {
		VariableAutomaton v1 = new VariableAutomaton("a");
		VariableAutomaton v2 = new VariableAutomaton("b");
		VariableAutomaton ab = v1.join(v2);
		assertTrue(ab.isSamePathAs(new VariableAutomaton("a/b")));
		v1 = new VariableAutomaton("/a/");
		v2 = new VariableAutomaton("/b/");
		ab = v1.join(v2);
		assertTrue(ab.isSamePathAs(new VariableAutomaton("/a/b/")));
		assertTrue(ab.isDirectory());
	}

}
