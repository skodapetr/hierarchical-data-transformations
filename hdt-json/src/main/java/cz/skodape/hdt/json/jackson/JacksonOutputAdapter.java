package cz.skodape.hdt.json.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import cz.skodape.hdt.model.OutputConfiguration;
import cz.skodape.hdt.model.TransformationFileAdapter;

import java.io.IOException;

public class JacksonOutputAdapter
        implements TransformationFileAdapter.OutputConfigurationAdapter {

    @Override
    public OutputConfiguration readJson(JsonNode root) throws IOException {
        if (!root.has("type")) {
            return null;
        }
        if (!"JacksonOutput".equals(root.get("type").asText())) {
            return null;
        }
        JacksonOutputConfiguration result = new JacksonOutputConfiguration();
        result.datatype = asType(root.get("dataType").asText());
        return result;
    }

    public JacksonOutputConfiguration.Type asType(String string)
            throws IOException {
        switch (string) {
            case "string":
                return JacksonOutputConfiguration.Type.String;
            case "number":
                return JacksonOutputConfiguration.Type.Number;
            case "boolean":
                return JacksonOutputConfiguration.Type.Boolean;
            default:
                throw new IOException("Invalid type '" + string + "'.");
        }
    }

}
