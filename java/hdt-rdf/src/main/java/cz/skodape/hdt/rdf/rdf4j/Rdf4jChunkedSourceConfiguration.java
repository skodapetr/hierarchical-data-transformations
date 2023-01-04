package cz.skodape.hdt.rdf.rdf4j;

import cz.skodape.hdt.core.source.PropertySource;
import cz.skodape.hdt.model.TransformationSource;

import java.io.File;

public class Rdf4jChunkedSourceConfiguration implements TransformationSource {

    public File file = null;

    @Override
    public PropertySource createSource() {
        return new Rdf4jChunkedSource(this);
    }


}
