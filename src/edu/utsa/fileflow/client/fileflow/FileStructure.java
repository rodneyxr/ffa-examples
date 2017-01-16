/**
 * This class represents a file structure using an automaton.
 * 
 * @author Rodney Rodriguez
 *
 */
package edu.utsa.fileflow.client.fileflow;

import dk.brics.automaton.Automaton;

public class FileStructure implements Cloneable {

	static {
		Automaton.setMinimization(Automaton.MINIMIZE_BRZOZOWSKI);
		Automaton.setMinimizeAlways(true);
	}

	private static final Automaton SEPARATOR = Automaton.makeChar('/');

	private VariableAutomaton cwd = new VariableAutomaton("/");
	Automaton files;

	public FileStructure() {
		this.files = SEPARATOR.clone();
	}

	private FileStructure(Automaton files) {
		this.files = files;
	}

	public static FileStructure top() {
		Automaton files = SEPARATOR.clone();
		files.concatenate(Automaton.makeAnyString());
		return new FileStructure(files);
	}

	/**
	 * @return a clone of the separator automaton.
	 */
	public static Automaton separator() {
		return SEPARATOR.clone();
	}

	/**
	 * Cleans a string representing a file path.
	 * 
	 * @param fp
	 *            The file path to clean.
	 * @return a new String with the cleaned file path.
	 */
	public static String clean(String fp) {
		fp = fp.trim();
		fp = fp.replaceAll("[/\\\\]+", "/");
		return fp;
	}

	/**
	 * Creates a file at the file path provided. Parent directory must exist for
	 * this operation to be successful. <code>fp</code> must not represent a
	 * directory (include a trailing slash).
	 * 
	 * @param fp
	 *            The file path to create the file.
	 * @throws FileStructureException
	 *             if the parent directory does not exist
	 */
	public void createFile(VariableAutomaton fp) throws FileStructureException {
		if (fp.isDirectory())
			throw new FileStructureException(String.format("touch: cannot touch '%s': Cannot touch a directory",
					fp.getAutomaton().getCommonPrefix()));

		// if the parent directory does not exist throw an exception
		if (!fileExists(fp.getParentDirectory()))
			throw new FileStructureException(String.format("touch: cannot touch '%s': No such file or directory",
					fp.getAutomaton().getCommonPrefix()));

		// if the file already exists, throw an exception
		if (fileExists(fp))
			throw new FileStructureException(String.format("touch: cannot touch '%s': File already exists",
					fp.getAutomaton().getCommonPrefix()));

		union(fp);
	}

	/**
	 * Creates a directory in the file structure at the path provided. If the
	 * path to that directory does not exist, it will be created.
	 * 
	 * @param fp
	 *            The file path to the directory to be created.
	 * @throws FileStructureException
	 *             if a file already exists at <code>fp</code>.
	 */
	public void createDirectory(VariableAutomaton fp) throws FileStructureException {
		// if fp does not have a trailing separator, then add one
		if (!fp.endsWith(SEPARATOR))
			fp = fp.concatenate(new VariableAutomaton("/"));

		VariableAutomaton a = new VariableAutomaton(absolute(fp));
		if (fileExists(a))
			throw new FileStructureException(String.format("mkdir: cannot create directory '%s': File exists",
					a.getAutomaton().getCommonPrefix()));

		union(fp);
	}

	/**
	 * Determines whether a file exists. It does not matter if it is a directory
	 * or regular file or possibly both.
	 * 
	 * @param fp
	 *            The file path of the file to check if it exists.
	 * @return true if the file exists; false otherwise.
	 */
	public boolean fileExists(VariableAutomaton fp) {
		VariableAutomaton a = new VariableAutomaton(absolute(fp));
		// try as a regular file
		a = a.removeLastSeparator();
		if (a.subsetOf(files))
			return true;

		// try as a directory
		a = a.concatenate(new VariableAutomaton(SEPARATOR));
		return a.subsetOf(files);
	}

	/**
	 * Graphviz DOT representation of this file structure.
	 * 
	 * @return a Graphviz DOT representation of the files automaton.
	 */
	public String toDot() {
		return files.toDot();
	}

	/**
	 * Prepends the current working directory to the file path variable given.
	 * 
	 * @param fp
	 *            The file path to be appended to the current working directory.
	 * @return the absolute file path as an automaton.
	 */
	private Automaton absolute(VariableAutomaton fp) {
		if (fp.startsWith(cwd.getAutomaton())) {
			return fp.getAutomaton();
		}
		return cwd.concatenate(fp).getAutomaton();
	}

	/**
	 * Performs a union operation on <code>files</code>. <code>fp</code> is
	 * converted to an absolute path before the union.
	 * 
	 * @param fp
	 *            The variable automaton to union with <code>files</code>.
	 */
	private void union(VariableAutomaton fp) {
		files = files.union(absolute(fp));
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FileStructure))
			return false;
		FileStructure o = (FileStructure) obj;
		if (!files.equals(o.files))
			return false;
		return true;
	}

	@Override
	public FileStructure clone() {
		FileStructure clone = new FileStructure();
		clone.files = files.clone();
		return clone;
	}

}
