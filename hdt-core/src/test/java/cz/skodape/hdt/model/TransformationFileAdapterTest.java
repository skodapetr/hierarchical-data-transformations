package cz.skodape.hdt.model;

import cz.skodape.hdt.TestUtils;
import cz.skodape.hdt.selector.filter.FilterSelectorAdapter;
import cz.skodape.hdt.selector.filter.FilterSelectorConfiguration;
import cz.skodape.hdt.selector.identity.IdentitySelectorAdapter;
import cz.skodape.hdt.selector.identity.IdentitySelectorConfiguration;
import cz.skodape.hdt.selector.path.PathSelectorAdapter;
import cz.skodape.hdt.selector.path.PathSelectorConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class TransformationFileAdapterTest {

    /**
     * Minimal example.
     */
    @Test
    public void test00() throws Exception {
        TransformationFileAdapter adapter = new TransformationFileAdapter();
        //
        TransformationFile actual = adapter.readJson(
                TestUtils.urlFromResource("model/test-00.json"));
        Assertions.assertEquals("root", actual.rootSource);
        Assertions.assertEquals("root", actual.propertySource);
        Assertions.assertEquals(0, actual.sources.size());
        BaseTransformation transformation = actual.transformation;
        Assertions.assertEquals(0, transformation.selectors.size());
        Assertions.assertTrue(
                transformation instanceof PrimitiveTransformation);
        PrimitiveTransformation primitive =
                (PrimitiveTransformation) transformation;
        Assertions.assertEquals("42", primitive.constantValue);
    }

    /**
     * Load configuration for core selectors with templates.
     */
    @Test
    public void test01() throws Exception {
        TransformationFileAdapter adapter = new TransformationFileAdapter();
        adapter.addAdapter(new IdentitySelectorAdapter());
        adapter.addAdapter(new PathSelectorAdapter());
        adapter.addAdapter(new FilterSelectorAdapter());
        //
        TransformationFile actual = adapter.readJson(
                TestUtils.urlFromResource("model/test-01.json"));
        BaseTransformation transformation = actual.transformation;
        Assertions.assertEquals(3, transformation.selectors.size());
        Assertions.assertTrue(
                transformation.selectors.get(0)
                        instanceof IdentitySelectorConfiguration);
        Assertions.assertTrue(
                transformation.selectors.get(1)
                        instanceof PathSelectorConfiguration);
        Assertions.assertTrue(
                transformation.selectors.get(2)
                        instanceof FilterSelectorConfiguration);
        PathSelectorConfiguration path =
                (PathSelectorConfiguration) transformation.selectors.get(1);
        Assertions.assertEquals(2, path.path.size());
        Assertions.assertEquals("isPartOf", path.path.get(0).predicate);
        Assertions.assertTrue(path.path.get(0).reverse);
        Assertions.assertEquals("name", path.path.get(1).predicate);
        Assertions.assertFalse(path.path.get(1).reverse);
        FilterSelectorConfiguration filter =
                (FilterSelectorConfiguration) transformation.selectors.get(2);
        Assertions.assertEquals("cs", filter.value);
        Assertions.assertEquals(
                FilterSelectorConfiguration.ConditionType.Contain,
                filter.condition);
        Assertions.assertEquals(1, filter.path.path.size());
    }

}
