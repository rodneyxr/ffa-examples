package edu.utsa.fileflow.client.fileflow.variable;

/**
 * Created by Rodney on 2/11/2017.
 * <p>
 * This class contains information about a variable such as an alias, name
 * and ID that is obtained from a {@link edu.utsa.fileflow.cfg.FlowPoint FlowPoint's} unique ID.
 */
public class Variable implements Comparable<Variable> {

	/* ID corresponding to the FlowPoint's unique ID */
	public final int id;

	/* ex: "$x0" or "$x0[1]" */
	final String name;

	/* String representation of this variable object */
	private final String alias;
	private final int hashCode;

	public Variable(String name, int id) {
		this.name = name;
		this.id = id;
		this.alias = String.format("%s{%d}", name, id);
		hashCode = alias.hashCode();
	}

	@Override
	public int compareTo(Variable other) {
		return alias.compareTo(other.alias);
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Variable))
			return false;
		Variable other = (Variable) obj;
		return alias.equals(other.alias);
	}

	@Override
	public String toString() {
		return alias;
	}
}
