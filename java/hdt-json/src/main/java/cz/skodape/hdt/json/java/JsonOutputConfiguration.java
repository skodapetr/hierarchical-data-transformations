package cz.skodape.hdt.json.java;

import cz.skodape.hdt.model.TransformationTarget;

public class JsonOutputConfiguration implements TransformationTarget {

    public enum Type {
        String,
        Number,
        Boolean;
    }

    /**
     * Define output JSON value type.
     */
    public Type datatype;

    public JsonOutputConfiguration() {
    }

    public JsonOutputConfiguration(Type datatype) {
        this.datatype = datatype;
    }

}
