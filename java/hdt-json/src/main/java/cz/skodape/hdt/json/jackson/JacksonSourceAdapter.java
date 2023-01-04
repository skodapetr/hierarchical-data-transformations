package cz.skodape.hdt.json.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import cz.skodape.hdt.model.TransformationSource;
import cz.skodape.hdt.model.adapter.TransformationFileAdapter;

public class JacksonSourceAdapter
        implements TransformationFileAdapter.SourceConfigurationAdapter {

    @Override
    public TransformationSource readJson(JsonNode root) {
        return null;
    }

}
