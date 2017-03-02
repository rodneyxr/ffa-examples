package edu.utsa.fileflow.client.fileflow.variable;

/**
 * Created by Rodney on 2/11/2017.
 * <p>
 * This class contains information about a alias such as the alias name
 * and ID that is obtained from a flowpoint's unique ID.
 */
public class Variable implements Comparable<Variable> {

	final String name; // ex: "$x0" or "$x0[1]"
	final int id; // id corresponding to flowpoint's unique ID

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
