package edu.utsa.fileflow.client.fileflow;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.FiniteStateTransducer;
import edu.utsa.fileflow.utilities.GraphvizGenerator;

public class TransducersTest {

	private static final String DOT_DIR = "test/transducers/";
	
	@Before
	public void setUp() throws Exception {
		Automaton.setMinimization(Automaton.MINIMIZE_BRZOZOWSKI);
		Automaton.setMinimizeAlways(true);
	}

	@Test
	public void testCreateFSTParent() {
		FiniteStateTransducer fst = Transducers.parentDir();
		save(fst, "fst/fst_parent.dot");
	}

	@Test
	public void testCreateFSTRemoveDoubleSeparator() {
		FiniteStateTransducer fst = Transducers.removeDoubleSeparator();
		save(fst, "fst/fst_rm_double_sep.dot");
	}

	@Test
	public void testCreateFSTRemoveLastSeparator() {
		FiniteStateTransducer fst = Transducers.removeLastSeparator();
		save(fst, "fst/fst_rm_last_sep.dot");
	}
	
	@Test
	public void testCreateFSTBasename() {
		FiniteStateTransducer fst = Transducers.basename();
		save(fst, "fst/fst_basename.dot");
	}

	@Test
	public void testBasename() {
		// test the basename of a file
		Automaton a = Automaton.makeString("/home/user/bashrc");
		save(a, "fst_test/basename_file.orig.dot");
		a = Transducers.basename(a);
		save(a, "fst_test/basename_file.dot");
		assertTrue(a.run("bashrc"));
		assertFalse(a.run("/home/user/bashrc"));

		// test the basename of a directory
		a = Automaton.makeString("/home/user/");
		save(a, "fst_test/basename_dir.orig.dot");
		a = Transducers.basename(a);
		save(a, "fst_test/basename_dir.dot");
		assertTrue(a.run("user"));
		assertFalse(a.run("user/"));
		assertFalse(a.run("/home/user/"));
	}

	@Test
	public void testRemoveDoubleSepEnd() {
		Automaton a = Automaton.makeString("a//");
		save(a, "fst_test/rm_double_sep_end.orig.dot");
		a = Transducers.removeDoubleSeparators(a);
		save(a, "fst_test/rm_double_sep_end.dot");
	}

	@Test
	public void testRemoveDoubleSep() {
		// Automaton expected = Automaton.makeString("dir1/file1");
		Automaton a1 = Automaton.makeString("dir1/");
		Automaton a2 = Automaton.makeString("/file1");
		Automaton a = a1.concatenate(a2);
		save(a, "fst_test/rm_double_sep.orig.dot");
		a = Transducers.removeDoubleSeparators(a);
		save(a, "fst_test/rm_double_sep.dot");
	}

	@Test
	public void testRemoveLastSeparatorFST() {
		Automaton a = Automaton.makeString("/a/b/c/");
		assertTrue(a.run("/a/b/c/"));
		save(a, "fst_test/rm_last_sep.orig.dot");

		a = Transducers.removeLastSeparator(a);
		assertFalse(a.run("/a/b/c/"));
		assertTrue(a.run("/a/b/c"));
		save(a, "fst_test/rm_last_sep.dot");

		a = Automaton.makeString("/a/b/c");
		assertTrue(a.run("/a/b/c"));
		a = Transducers.removeLastSeparator(a);
		assertTrue(a.run("/a/b/c"));

		a = Automaton.makeString("/a");
		assertTrue(a.run("/a"));
		a = Transducers.removeLastSeparator(a);
		assertTrue(a.run("/a"));
	}
	
	private void save(Automaton a, String filepath) {
		GraphvizGenerator.saveDOTToFile(a.toDot(), DOT_DIR + filepath);
	}

}
