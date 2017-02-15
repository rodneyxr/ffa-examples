package edu.utsa.fileflow.client.fileflow.variable;

/**
 * Created by Rodney on 2/11/2017.
 * <p>
 * This class contains information about a variable such as the variable name
 * an ID that is obtained from a flowpoint's unique ID.
 */
public class Variable {

    private String variableName;
    private int id;

    public String getVariableName() {
        return variableName;
    }

    public int getId() {
        return id;
    }

}
