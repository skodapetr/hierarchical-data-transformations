package cz.skodape.hdt.json.jackson;

import cz.skodape.hdt.model.OutputConfiguration;

public class JacksonOutputConfiguration implements OutputConfiguration {

    public enum Type {
        String,
        Number,
        Boolean;
    }

    /**
     * Define output JSON value type.
     */
    public Type datatype;

    public JacksonOutputConfiguration() {
    }

    public JacksonOutputConfiguration(Type datatype) {
        this.datatype = datatype;
    }

}
