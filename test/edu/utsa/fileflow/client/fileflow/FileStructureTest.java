package edu.utsa.fileflow.client.fileflow;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;
import edu.utsa.fileflow.testutils.GraphvizGenerator;

public class FileStructureTest {

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
	public void testGetPathToFile() {
		Automaton.setMinimizeAlways(true);
		Automaton pathToFile = Automaton.makeString("/dir1/");
		Automaton a = Automaton.makeChar('/');
		a = a.union(pathToFile);
		a = a.union(Automaton.makeString("/dir1/file1"));
		a = FileStructure.getPathToFile(a);
		GraphvizGenerator.saveDOTToFile(a.toDot(), "automaton.dot");
	}

	@Test
	public void testTransitionToNull() {
		Automaton a = Automaton.makeChar('/');
		State s0 = a.getInitialState().getTransitions().toArray(new Transition[1])[0].getDest();
		assertTrue(s0.getTransitions().size() == 0);
	}

}
