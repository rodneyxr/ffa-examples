package edu.utsa.fileflow.client.fileflow;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import dk.brics.automaton.Automaton;
import edu.utsa.fileflow.testutils.GraphvizGenerator;

public class FileStructureTest {

	@Before
	public void setUp() throws Exception {
		Automaton.setMinimization(Automaton.MINIMIZE_BRZOZOWSKI);
		Automaton.setMinimizeAlways(true);
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

}
