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

public class FileStructureTest {

	@Before
	public void setUp() throws Exception {
		Automaton.setMinimization(Automaton.MINIMIZE_BRZOZOWSKI);
		Automaton.setMinimizeAlways(true);
	}

	final Automaton VALID_CHARS = new RegExp("[a-zA-Z0-9.-_]{1}").toAutomaton();
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
	public void testDirectoryExists() throws FileStructureException {
		FileStructure fs = new FileStructure();
		fs.createDirectory(new VariableAutomaton("/home"));
		// '/' exists by default since it is root
		assertTrue("'/' should exist", fs.fileExists(new VariableAutomaton("/")));
		assertTrue("'/home' should exist", fs.fileExists(new VariableAutomaton("/home")));
		assertFalse("'/fake' should not exist", fs.fileExists(new VariableAutomaton("/fake")));
	}

	@Test
	public void testFileExists() throws FileStructureException {
		FileStructure fs = new FileStructure();
		fs.createDirectory(new VariableAutomaton("/home"));
		fs.createFile(new VariableAutomaton("/home/file"));

		// '/' exists by default since it is root
		assertTrue("'/' should exist", fs.fileExists(new VariableAutomaton("/")));
		assertTrue("'/home' should exist", fs.fileExists(new VariableAutomaton("/home")));
		assertTrue("'/home/file' should exist", fs.fileExists(new VariableAutomaton("/home/file")));
		assertFalse("'/fake' should not exist", fs.fileExists(new VariableAutomaton("/fake")));
	}

	@Test
	public void testSubsetOf() {
		Automaton a = Automaton.makeString("/");
		a = a.union(Automaton.makeString("/dir1/"));
		a = a.union(Automaton.makeString("/dir1/file1"));
		GraphvizGenerator.saveDOTToFile(a.toDot(), "tmp/subset.dot");
		assertTrue(Automaton.makeString("/dir1/file1").subsetOf(a));
	}

	@Test(expected = FileStructureException.class)
	public void testCreateFileWhenDirectoryAlreadyExists() throws FileStructureException {
		FileStructure fs = new FileStructure();

		// create and assert '/a/'
		fs.createDirectory(new VariableAutomaton("/a/"));
		assertTrue(fs.fileExists(new VariableAutomaton("/a/")));

		// attempt to create file at existing directory '/a' (should fail)
		fs.createFile(new VariableAutomaton("/a"));
	}

	@Test(expected = FileStructureException.class)
	public void testCreateDirectoryWhenFileAlreadyExists() throws FileStructureException {
		FileStructure fs = new FileStructure();

		// create and assert '/a'
		fs.createFile(new VariableAutomaton("/a"));
		assertTrue(fs.fileExists(new VariableAutomaton("/a")));

		// attempt to create directory at existing file '/a' (should fail)
		fs.createDirectory(new VariableAutomaton("/a/"));
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
