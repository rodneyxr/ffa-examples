/**
 * This class represents a file structure using two automatons. One of the automatons contains all directories and the
 * other contains the files.
 * 
 * @author Rodney Rodriguez
 *
 */
package edu.utsa.fileflow.client.fileflow;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;

public class FileStructure implements Cloneable {

	public static final Automaton INVERSE_PATH = new RegExp("/.+/").toAutomaton();

	Automaton dirs;
	Automaton files;

	public FileStructure() {
		this(Automaton.makeChar('/'), Automaton.makeEmpty());
	}

	public static FileStructure top() {
		Automaton dirs = Automaton.makeChar('/');
		dirs.concatenate(Automaton.makeAnyString());
		return new FileStructure(dirs, Automaton.makeAnyString());
	}

	private FileStructure(Automaton dirs, Automaton files) {
		this.dirs = dirs;
		this.files = files;
	}

	/**
	 * Creates a file at the file path provided. Directory must exist for this operation to be successful.
	 * 
	 * @param fp
	 *            The file path to create the file.
	 * @return this FileStructure.
	 */
	public FileStructure createFile(Automaton fp) {
		files = files.union(fp);
		return this;
	}

	public FileStructure createDirectory(Automaton fp) {
		dirs = dirs.union(fp);
		return this;
	}

	public boolean directoryExists(String fp) {
		fp = clean(fp);
		return dirs.run(fp);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FileStructure))
			return false;
		FileStructure o = (FileStructure) obj;
		if (!dirs.equals(o.dirs))
			return false;
		if (!files.equals(o.files))
			return false;
		return true;
	}

	/**
	 * Graphviz DOT representation of this file structure.
	 * 
	 * @return a Graphviz DOT representation of the files automaton.
	 */
	public String toDot() {
		return files.toDot();
	}

	@Override
	protected FileStructure clone() {
		FileStructure clone = new FileStructure();
		clone.dirs = dirs.clone();
		clone.files = files.clone();
		return clone;
	}

	/**
	 * Cleans a string representing a file path.
	 * 
	 * @param fp
	 *            The file path to clean.
	 * @return a new String with the cleaned file path.
	 */
	private String clean(String fp) {
		fp = fp.replaceAll("/+", "/");
		return fp;
	}

}
