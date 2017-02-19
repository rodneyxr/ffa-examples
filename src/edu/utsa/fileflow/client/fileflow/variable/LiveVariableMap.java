package edu.utsa.fileflow.client.fileflow.variable;

import edu.utsa.fileflow.analysis.Mergeable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Rodney on 2/18/2017.
 * <p>
 * This class is a data structure for keeping track of live variables during variable analysis.
 * A variable is live if it may be used in the future. Only live variables should be added to this
 * data structure.
 */
public class LiveVariableMap implements Cloneable, Mergeable<LiveVariableMap> {
	private HashMap<String, HashSet<Variable>> m;

	public LiveVariableMap() {
		m = new HashMap<>();
	}

	public void addVariable(Variable variable) {
		HashSet<Variable> s = getOrCreate(variable.name);
		s.clear();
		s.add(variable);
	}

	/**
	 * Gets the HashSet of variables given the key (usually the variable name)
	 * if it exists in the HashMap <code>m</code>. If it does not exist then a
	 * new HashSet will be created and put into the map with the key entered.
	 *
	 * @param k The key to get or create in the HashMap.
	 * @return the HashSet obtained by <code>k</code> or a new HashSet if one does not exist.
	 */
	private HashSet<Variable> getOrCreate(String k) {
		return m.computeIfAbsent(k, set -> new HashSet<>());
	}

	@Override
	public LiveVariableMap merge(LiveVariableMap other) {
		// if is bottom just return
		if (other.m.isEmpty())
			return this;

		// add (merge) everything in other map to this map
		other.m.forEach((k, set2) -> {
			HashSet<Variable> set1 = this.m.get(k);

			// if item is only in other map
			if (set1 == null) {
				// just add it to this map
				this.m.put(k, set2);
			} else {
				// item exists in both, merge the two sets
				set1.addAll(set2);
			}
		});
		return this;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof LiveVariableMap))
			return false;
		LiveVariableMap other = (LiveVariableMap) object;
		return m.equals(other.m);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		for (Map.Entry<String, HashSet<Variable>> entry : m.entrySet()) {
			sb.append("\t");
			sb.append(entry.getKey());
			sb.append(": ");
			sb.append(entry.getValue());
			sb.append("\n");
		}
		sb.append("}");
		return sb.toString();
	}

	@Override
	public LiveVariableMap clone() {
		LiveVariableMap clone = new LiveVariableMap();
		for (Map.Entry<String, HashSet<Variable>> entry : m.entrySet()) {
			clone.m.put(entry.getKey(), (HashSet<Variable>) entry.getValue().clone());
		}
		return clone;
	}

}
