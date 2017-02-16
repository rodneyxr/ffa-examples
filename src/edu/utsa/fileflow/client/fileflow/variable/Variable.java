package edu.utsa.fileflow.client.fileflow.variable;

/**
 * Created by Rodney on 2/11/2017.
 * <p>
 * This class contains information about a variable such as the variable name
 * and ID that is obtained from a flowpoint's unique ID.
 */
public class Variable {

    final String name; // ex: "$x0" or "$x0[1]"
    final int id; // id corresponding to flowpoint's unique ID

    private final int hashCode;

    public Variable(String name, int id) {
        this.name = name;
        this.id = id;
        hashCode = (name + id).hashCode();
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
        return name.equals(other.name) && id == other.id;
    }

    @Override
    public String toString() {
        return String.format("%s{%d}", name, id);
    }
}
