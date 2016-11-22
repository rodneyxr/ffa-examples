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

	FiniteStateTransducer FST = FiniteStateTransducer.parentDir();

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Automaton.setMinimizeAlways(true);
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
		Automaton.setMinimizeAlways(true);
		Automaton a = Automaton.makeChar('/');
		RegExp reg = new RegExp("/dir[1-9]*/q");
		RegExp r2 = new RegExp("/dir[1-9]*/");
		a = a.union(Automaton.makeString("/dir1/q"));
		a = a.union(Automaton.makeString("/df"));
		a = a.union(reg.toAutomaton());
		a = a.union(r2.toAutomaton());
		GraphvizGenerator.saveDOTToFile(a.toDot(), "complex.orig.dot");
		a = FileStructure.getPathToFile(a);
		GraphvizGenerator.saveDOTToFile(a.toDot(), "complex.dot");
		// TODO: Create some assertions
	}

	@Test
	public void testGetPathToFileSingleton() {
		Automaton.setMinimizeAlways(true);
		Automaton pathToFile = Automaton.makeString("/dir1/");
		Automaton a = Automaton.makeChar('/');
		a = a.union(pathToFile);
		a = a.union(Automaton.makeString("/dir1/file1"));
		GraphvizGenerator.saveDOTToFile(a.toDot(), "singleton.orig.dot");
		a = FileStructure.getPathToFile(a);
		GraphvizGenerator.saveDOTToFile(a.toDot(), "singleton.dot");
		// TODO: Create some assertions
	}

	@Test
	public void testTransitionToNull() {
		Automaton a = Automaton.makeChar('/');
		State s0 = a.getInitialState().getTransitions().toArray(new Transition[1])[0].getDest();
		assertTrue(s0.getTransitions().isEmpty());
	}

}
