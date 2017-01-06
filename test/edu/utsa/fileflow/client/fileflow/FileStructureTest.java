package edu.utsa.fileflow.client.fileflow;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.FiniteStateTransducer;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;
import edu.utsa.fileflow.testutils.GraphvizGenerator;

public class FileStructureTest {

	final Automaton VALID_CHARS = new RegExp("[a-zA-Z0-9.-_]{1}").toAutomaton();
	final FiniteStateTransducer FST_PARENT = FiniteStateTransducer.parentDir();
	final FiniteStateTransducer FST_REMOVE_SEP = FiniteStateTransducer.removeDoubleSeparator();

	@Test
	public void testCreateFSTParent() {
		FiniteStateTransducer fst = FiniteStateTransducer.parentDir();
		GraphvizGenerator.saveDOTToFile(fst.toDot(), "test/fst_parent.dot");
	}

	@Test
	public void testCreateFSTRemoveDoubleSeparator() {
		FiniteStateTransducer fst = FiniteStateTransducer.removeDoubleSeparator();
		GraphvizGenerator.saveDOTToFile(fst.toDot(), "test/fst_rm_double_sep.dot");
	}

	@Test
	public void testRemoveDoubleSepEnd() {
		// Automaton expected = Automaton.makeString("dir1/file1");
		Automaton a = Automaton.makeString("a//");
		GraphvizGenerator.saveDOTToFile(a.toDot(), "test/rm_double_sep_end.orig.dot");
		a = FST_REMOVE_SEP.intersection(a);
		GraphvizGenerator.saveDOTToFile(a.toDot(), "test/rm_double_sep_end.dot");
	}

	@Test
	public void testRemoveDoubleSep() {
		// Automaton expected = Automaton.makeString("dir1/file1");
		Automaton a1 = Automaton.makeString("dir1/");
		Automaton a2 = Automaton.makeString("/file1");
		Automaton a = a1.concatenate(a2);
		GraphvizGenerator.saveDOTToFile(a.toDot(), "test/rm_double_sep.orig.dot");
		a = FST_REMOVE_SEP.intersection(a);
		GraphvizGenerator.saveDOTToFile(a.toDot(), "test/rm_double_sep.dot");
	}

	@Test
	public void testDirectoryExists() {
		FileStructure fs = new FileStructure();
		fs.createDirectory(Automaton.makeString("/home"));
		// '/' exists by default since it is root
		assertTrue("'/' should exist", fs.directoryExists("/"));
		assertTrue("'/home' should exist", fs.directoryExists("/home"));
		assertFalse("'/fake' should not exist", fs.directoryExists("/fake"));
	}

	@Test
	public void testGetPathToFileComplex() {
		Automaton a = Automaton.makeChar('/');
		RegExp reg = new RegExp("/dir[1-9]*/q");
		RegExp r2 = new RegExp("/dir[1-9]*/");
		a = a.union(Automaton.makeString("/dir1/q"));
		a = a.union(Automaton.makeString("/df"));
		a = a.union(reg.toAutomaton());
		a = a.union(r2.toAutomaton());
		GraphvizGenerator.saveDOTToFile(a.toDot(), "test/complex.orig.dot");
		assertTrue(a.run("/df"));

		a = FileStructure.getParentDirectory(a);
		GraphvizGenerator.saveDOTToFile(a.toDot(), "test/complex.dot");
		assertTrue(a.run("/dir1/"));
		assertTrue(a.run("/dir4/"));
		assertTrue(a.run("/dir9/"));
		assertFalse(a.run("/df"));
	}

	@Test
	public void testGetPathToFileSingleton() {
		Automaton pathToFile = Automaton.makeString("/dir1/");
		Automaton a = Automaton.makeChar('/');
		a = a.union(pathToFile);
		a = a.union(Automaton.makeString("/dir1/file1"));
		assertTrue(a.run("/dir1/file1"));
		assertFalse(a.run("/dir1/file1/"));
		GraphvizGenerator.saveDOTToFile(a.toDot(), "test/singleton.orig.dot");

		a = FileStructure.getParentDirectory(a);
		GraphvizGenerator.saveDOTToFile(a.toDot(), "test/singleton.dot");
		assertTrue(a.run("/dir1/"));
		assertFalse(a.run("/dir1/file1"));
	}

	@Test
	public void testGetPathToFileInRoot() throws Exception {
		FileStructure fs = new FileStructure();
		fs.createFile(new VariableAutomaton("test"));
		GraphvizGenerator.saveDOTToFile(fs.files.toDot(), "test/file_in_root.orig.dot");
		// TODO: create assertions

		Automaton a = FileStructure.getParentDirectory(fs.files);
		GraphvizGenerator.saveDOTToFile(a.toDot(), "test/file_in_root.dot");
	}

	@Test
	public void testGetPathToDirectoryInRoot() {
		FileStructure fs = new FileStructure();
		fs.createDirectory(Automaton.makeString("/home"));
		GraphvizGenerator.saveDOTToFile(fs.files.toDot(), "test/dir_in_root.orig.dot");
		Automaton a = FileStructure.getParentDirectory(fs.files);
		GraphvizGenerator.saveDOTToFile(a.toDot(), "test/dir_in_root.dot");
	}

	@Test
	public void testTransitionToNull() {
		Automaton a = Automaton.makeChar('/');
		State s0 = a.getInitialState().getTransitions().toArray(new Transition[1])[0].getDest();
		assertTrue(s0.getTransitions().isEmpty());
	}

	@Test
	public void testMakeFileAutomaton() {
		String fpText = "file1";
		Automaton fp = FileStructure.makeFileAutomaton(fpText);
		GraphvizGenerator.saveDOTToFile(fp.toDot(), "test/make_file.dot");
		assertTrue(fp.run("/"));
		assertFalse(fp.run("//"));
		assertTrue(fp.run("/file1"));
	}

	@Test
	public void testMakeDirAutomaton() {
		String fpText = "dir";
		Automaton fp = FileStructure.makeDirAutomaton(fpText);
		GraphvizGenerator.saveDOTToFile(fp.toDot(), "test/make_dir.dot");
		assertTrue(fp.run("/"));
		assertFalse(fp.run("//"));

		assertTrue(fp.run("/dir/"));
		assertFalse(fp.run("/dir"));
	}

}
