package edu.utsa.fileflow.client.fileflow;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import dk.brics.automaton.Automaton;

public class FileStructureTest {

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
		Automaton fp = Automaton.makeString("/dir1/file1");
		fp.minimize();
		final Automaton ip = FileStructure.INVERSE_PATH;

		// Automaton pathToFile = fp.(Automaton.makeString("/dir1/"));
		System.out.println(ip.getCommonPrefix());
		// System.out.println(pathToFile);
		// GraphvizGenerator.saveDOTToFile(pathToFile.toDot(), "automaton.dot");

	}

}
