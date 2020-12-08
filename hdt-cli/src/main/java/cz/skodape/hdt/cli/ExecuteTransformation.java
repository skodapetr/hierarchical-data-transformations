package cz.skodape.hdt.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import cz.skodape.hdt.core.OperationFailed;
import cz.skodape.hdt.core.Output;
import cz.skodape.hdt.core.PropertySource;
import cz.skodape.hdt.core.SelectorContext;
import cz.skodape.hdt.core.Transform;
import cz.skodape.hdt.json.jackson.JacksonSourceAdapter;
import cz.skodape.hdt.json.java.JsonOutputAdapter;
import cz.skodape.hdt.model.TransformationFile;
import cz.skodape.hdt.model.TransformationFileAdapter;
import cz.skodape.hdt.rdf.rdf4j.Rdf4jSourceAdapter;
import cz.skodape.hdt.selector.filter.FilterSelectorAdapter;
import cz.skodape.hdt.selector.identity.IdentitySelectorAdapter;
import cz.skodape.hdt.selector.once.OnceSelectorAdapter;
import cz.skodape.hdt.selector.path.PathSelectorAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ExecuteTransformation {

    private static final Logger LOG =
            LoggerFactory.getLogger(ExecuteTransformation.class);

    public void transform(URL definitionUrl, Output output)
            throws OperationFailed, IOException {
        TransformationFileAdapter adapter = createAdapter();
        LOG.info("Loading configuration ...");
        TransformationFile definition = adapter.readJson(definitionUrl);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        SelectorContext context = createContext(definition);
        (new Transform(definition, context, output)).apply();
        LOG.info("Transformation finished.");
    }

    protected TransformationFileAdapter createAdapter() {
        TransformationFileAdapter adapter = new TransformationFileAdapter();
        //IdentitySelectorConfiguration
        adapter.addAdapter(new FilterSelectorAdapter());
        adapter.addAdapter(new IdentitySelectorAdapter());
        adapter.addAdapter(new PathSelectorAdapter());
        adapter.addAdapter(new OnceSelectorAdapter());
        //
        adapter.addAdapter(new JsonOutputAdapter());
        adapter.addAdapter(new JacksonSourceAdapter());
        //
        adapter.addAdapter(new Rdf4jSourceAdapter());
        //
        return adapter;
    }

    protected SelectorContext createContext(TransformationFile definition) {
        Map<String, PropertySource> sources = new HashMap<>();
        for (var entry : definition.sources.entrySet()) {
            sources.put(entry.getKey(), entry.getValue().createSource());
        }
        return new SelectorContext(
                sources, sources.get(definition.propertySource));
    }


}
