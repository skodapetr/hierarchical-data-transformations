package cz.skodape.hdt.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransformationFileAdapter {

    public interface SourceConfigurationAdapter {

        /**
         * Return null if the adapter can not read given content.
         */
        SourceConfiguration readJson(JsonNode root) throws IOException;

    }

    public interface SelectorConfigurationAdapter {

        /**
         * Return null if the adapter can not read given content.
         */
        SelectorConfiguration readJson(JsonNode root) throws IOException;

    }

    public interface OutputConfigurationAdapter {

        OutputConfiguration readJson(JsonNode root) throws IOException;

    }

    private final List<SourceConfigurationAdapter> sourceAdapters =
            new ArrayList<>();

    private final List<SelectorConfigurationAdapter> selectorAdapters =
            new ArrayList<>();

    private final List<OutputConfigurationAdapter> outputAdapters =
            new ArrayList<>();

    public void addAdapter(SourceConfigurationAdapter adapter) {
        this.sourceAdapters.add(adapter);
    }

    public void addAdapter(SelectorConfigurationAdapter adapter) {
        this.selectorAdapters.add(adapter);
    }

    public void addAdapter(OutputConfigurationAdapter adapter) {
        this.outputAdapters.add(adapter);
    }

    public TransformationFile readJson(URL url) throws IOException {
        JsonNode root = loadJson(url);
        TransformationFile result = new TransformationFile();
        result.rootSource = root.get("rootSource").asText();
        result.propertySource = root.get("propertySource").asText();
        result.sources = readSources(root.get("sources"));
        result.transformation = readTransformation(root.get("transformation"));
        return result;
    }

    protected JsonNode loadJson(URL url) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(url);
        return (new ResolveJsonTemplates()).resolveTemplates(root);
    }

    protected Map<String, SourceConfiguration> readSources(JsonNode node)
            throws IOException {
        var iterator = node.fields();
        Map<String, SourceConfiguration> result = new HashMap<>();
        while (iterator.hasNext()) {
            var next = iterator.next();
            String key = next.getKey();
            SourceConfiguration value = readSource(next.getValue());
            result.put(key, value);
        }
        return result;
    }

    protected SourceConfiguration readSource(JsonNode node) throws IOException {
        for (SourceConfigurationAdapter sourceAdapter : sourceAdapters) {
            var configuration = sourceAdapter.readJson(node);
            if (configuration == null) {
                continue;
            }
            return configuration;
        }
        throw new IOException("Can't recognize source definition.");
    }

    protected BaseTransformation readTransformation(JsonNode node)
            throws IOException {
        if (!node.has("type")) {
            throw new IOException("Missing type for: " + node.asText());
        }
        String type = node.get("type").asText();
        switch (type) {
            case "object":
                return readObjectTransformation(node);
            case "array":
                return readArrayTransformation(node);
            case "primitive":
                return readPrimitiveTransformation(node);
            default:
                throw new IOException(
                        "Invalid transformation type:'" + type + '.');
        }
    }

    protected BaseTransformation readObjectTransformation(JsonNode node)
            throws IOException {
        ObjectTransformation result = new ObjectTransformation();
        result.selectors = readSelectors(node.get("selectors"));
        var iterator = node.get("properties").fields();
        while (iterator.hasNext()) {
            var next = iterator.next();
            String key = next.getKey();
            BaseTransformation value = readTransformation(next.getValue());
            result.properties.put(key, value);
        }
        return result;
    }

    protected List<SelectorConfiguration> readSelectors(JsonNode node)
            throws IOException {
        var iterator = node.iterator();
        List<SelectorConfiguration> result = new ArrayList<>();
        while (iterator.hasNext()) {
            JsonNode selectorNode = iterator.next();
            SelectorConfiguration value = readSelector(selectorNode);
            if (value == null) {
                throw new IOException(
                        "Can't read selector: " + selectorNode.asText());
            }
            result.add(value);
        }
        return result;
    }

    protected SelectorConfiguration readSelector(JsonNode node)
            throws IOException {
        for (SelectorConfigurationAdapter sourceAdapter : selectorAdapters) {
            var configuration = sourceAdapter.readJson(node);
            if (configuration == null) {
                continue;
            }
            return configuration;
        }
        return null;
    }

    protected BaseTransformation readArrayTransformation(JsonNode node)
            throws IOException {
        ArrayTransformation result = new ArrayTransformation();
        result.selectors = readSelectors(node.get("selectors"));
        for (JsonNode jsonNode : node.get("items")) {
            result.items.add(readTransformation(jsonNode));
        }
        return result;
    }

    protected BaseTransformation readPrimitiveTransformation(JsonNode node)
            throws IOException {
        PrimitiveTransformation result = new PrimitiveTransformation();
        result.selectors = readSelectors(node.get("selectors"));
        if (node.has("constant")) {
            result.constantValue = node.get("constant").textValue();
        }
        if (node.has("default")) {
            result.defaultValue = node.get("default").textValue();
        }
        if (node.has("output")) {
            result.outputConfiguration = readOutput(node.get("output"));
            if (result.outputConfiguration == null) {
                throw new IOException("Can't recognize output definition.");
            }
        }
        return result;
    }

    protected OutputConfiguration readOutput(JsonNode node) throws IOException {
        for (OutputConfigurationAdapter sourceAdapter : outputAdapters) {
            var configuration = sourceAdapter.readJson(node);
            if (configuration == null) {
                continue;
            }
            return configuration;
        }
        return null;
    }

}
