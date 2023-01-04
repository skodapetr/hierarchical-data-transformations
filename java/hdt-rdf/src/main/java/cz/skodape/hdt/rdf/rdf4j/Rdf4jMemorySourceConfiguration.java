package cz.skodape.hdt.rdf.rdf4j;

import cz.skodape.hdt.core.source.PropertySource;
import cz.skodape.hdt.model.TransformationSource;

import java.io.File;

public class Rdf4jMemorySourceConfiguration implements TransformationSource {

    public File file = null;

    public boolean graphAware = false;

    @Override
    public PropertySource createSource() {
        return new Rdf4jMemorySource(this);
    }

}
