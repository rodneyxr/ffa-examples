package edu.utsa.fileflow.client.fileflow;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.FiniteStateTransducer;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;
import edu.utsa.fileflow.testutils.GraphvizGenerator;

public class VariableAutomatonTest {

	@Before
	public void setUp() throws Exception {
		Automaton.setMinimization(Automaton.MINIMIZE_BRZOZOWSKI);
		Automaton.setMinimizeAlways(true);
	}

	final FiniteStateTransducer FST_PARENT = Transducers.parentDir();
	final FiniteStateTransducer FST_REMOVE_DOUBLE_SEP = Transducers.removeDoubleSeparator();
	final FiniteStateTransducer FST_REMOVE_LAST_SEP = Transducers.removeLastSeparator();

	@Test
	public void testCreateFSTParent() {
		FiniteStateTransducer fst = Transducers.parentDir();
		GraphvizGenerator.saveDOTToFile(fst.toDot(), "test/fst/fst_parent.dot");
	}

	@Test
	public void testCreateFSTRemoveDoubleSeparator() {
		FiniteStateTransducer fst = Transducers.removeDoubleSeparator();
		GraphvizGenerator.saveDOTToFile(fst.toDot(), "test/fst/fst_rm_double_sep.dot");
	}

	@Test
	public void testCreateFSTRemoveLastSeparator() {
		FiniteStateTransducer fst = Transducers.removeLastSeparator();
		GraphvizGenerator.saveDOTToFile(fst.toDot(), "test/fst/fst_rm_last_sep.dot");
	}

	@Test
	public void testRemoveDoubleSepEnd() {
		Automaton a = Automaton.makeString("a//");
		GraphvizGenerator.saveDOTToFile(a.toDot(), "test/rm_double_sep_end.orig.dot");
		a = FST_REMOVE_DOUBLE_SEP.intersection(a);
		GraphvizGenerator.saveDOTToFile(a.toDot(), "test/rm_double_sep_end.dot");
	}

	@Test
	public void testRemoveDoubleSep() {
		// Automaton expected = Automaton.makeString("dir1/file1");
		Automaton a1 = Automaton.makeString("dir1/");
		Automaton a2 = Automaton.makeString("/file1");
		Automaton a = a1.concatenate(a2);
		GraphvizGenerator.saveDOTToFile(a.toDot(), "test/rm_double_sep.orig.dot");
		a = FST_REMOVE_DOUBLE_SEP.intersection(a);
		GraphvizGenerator.saveDOTToFile(a.toDot(), "test/rm_double_sep.dot");
	}

	@Test
	public void testRemoveLastSeparatorFST() {
		Automaton a = Automaton.makeString("/a/b/c/");
		assertTrue(a.run("/a/b/c/"));
		GraphvizGenerator.saveDOTToFile(a.toDot(), "test/fst_test/rm_last_sep.orig.dot");

		a = FST_REMOVE_LAST_SEP.intersection(a);
		assertFalse(a.run("/a/b/c/"));
		assertTrue(a.run("/a/b/c"));
		GraphvizGenerator.saveDOTToFile(a.toDot(), "test/fst_test/rm_last_sep.dot");

		a = Automaton.makeString("/a/b/c");
		assertTrue(a.run("/a/b/c"));
		a = FST_REMOVE_LAST_SEP.intersection(a);
		assertTrue(a.run("/a/b/c"));

		a = Automaton.makeString("/a");
		assertTrue(a.run("/a"));
		a = FST_REMOVE_LAST_SEP.intersection(a);
		assertTrue(a.run("/a"));
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

}
