package cz.skodape.hdt.rdf.rdf4j;

import cz.skodape.hdt.core.OperationFailed;
import cz.skodape.hdt.core.PrimitiveReference;
import cz.skodape.hdt.core.Reference;
import cz.skodape.hdt.core.ReferenceSource;
import cz.skodape.hdt.core.Selector;
import cz.skodape.hdt.core.SelectorContext;
import cz.skodape.hdt.selector.filter.FilterSelectorConfiguration;
import cz.skodape.hdt.selector.path.PathSelectorConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Rdf4jMemoryTripleSourceTest {

    @Test
    public void loadNkodSingleDataset() throws Exception {
        File file = TestUtils.fileFromResource("nkod-single-dataset.trig");
        Rdf4jMemorySourceConfiguration configuration =
                new Rdf4jMemorySourceConfiguration();
        configuration.file = file;
        configuration.graphAware = false;
        //
        Rdf4jMemorySource source = new Rdf4jMemorySource(configuration);
        source.open();
        List<Reference> roots = collect(source.roots());
        Assertions.assertEquals(8, roots.size());
        SelectorContext context = new SelectorContext(null, source);
        //
        Selector pathSelector = createPathSelector();
        pathSelector.initialize(context, source.roots());
        List<String> types = new ArrayList<>();
        Reference next;
        while ((next = pathSelector.next()) != null) {
            if (next instanceof PrimitiveReference) {
                types.add(((PrimitiveReference)next).getValue());
            }
        }
        Assertions.assertEquals(8, types.size());
        //
        Selector filterSelector = createFilterSelector();
        filterSelector.initialize(context, source.roots());
        List<Reference> filtered = collect(filterSelector);
        Assertions.assertEquals(1, filtered.size());
    }

    protected List<Reference> collect(ReferenceSource source)
            throws OperationFailed {
        ArrayList<Reference> result = new ArrayList<>();
        Reference next;
        while ((next = source.next()) != null) {
            result.add(next);
        }
        return result;
    }

    protected Selector createPathSelector() {
        PathSelectorConfiguration configuration =
                new PathSelectorConfiguration();
        PathSelectorConfiguration.Path stepType =
                new PathSelectorConfiguration.Path();
        stepType.predicate = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
        configuration.path.add(stepType);
        PathSelectorConfiguration.Path stepString =
                new PathSelectorConfiguration.Path();
        stepString.predicate = "@id";
        configuration.path.add(stepString);
        return configuration.createSelector();
    }

    protected Selector createFilterSelector() {
        FilterSelectorConfiguration configuration =
                new FilterSelectorConfiguration();
        configuration.condition =
                FilterSelectorConfiguration.ConditionType.Contain;
        configuration.value = "http://www.w3.org/ns/dcat#Dataset";

        configuration.path = new PathSelectorConfiguration();
        PathSelectorConfiguration.Path stepType =
                new PathSelectorConfiguration.Path();
        stepType.predicate = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
        configuration.path.path.add(stepType);
        PathSelectorConfiguration.Path stepString =
                new PathSelectorConfiguration.Path();
        stepString.predicate = "@id";
        configuration.path.path.add(stepString);

        return configuration.createSelector();
    }

}
