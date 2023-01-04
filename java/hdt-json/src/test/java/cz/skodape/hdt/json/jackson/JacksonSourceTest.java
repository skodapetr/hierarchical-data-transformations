package cz.skodape.hdt.json.jackson;

import cz.skodape.hdt.core.reference.EntityReference;
import cz.skodape.hdt.core.reference.PrimitiveReference;
import cz.skodape.hdt.core.reference.Reference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JacksonSourceTest {

    @Test
    public void loadPrimitives() throws Exception {
        JacksonSource source = JacksonSource.create(
                "[{\"key\":[1, \"string\", true]}]");
        int rootCount = 0;
        Reference root = null;
        var roots = source.roots();
        Reference next;
        while ((next = roots.next()) != null) {
            ++rootCount;
            root = next;
        }
        Assertions.assertEquals(1, rootCount);
        // Navigate to item.
        var rootSource = source.source(root);
        var objectRef = rootSource.next();
        Assertions.assertNotNull(objectRef);
        Assertions.assertNull(rootSource.next());
        // Select property in the item.
        Assertions.assertTrue(objectRef.isObjectReference());
        var propertyArrayRef = source.property(
                (EntityReference) objectRef, "key");
        var propertyArraySource = source.source(propertyArrayRef);
        var propRef = propertyArraySource.next();
        Assertions.assertNotNull(propRef);
        Assertions.assertNull(propertyArraySource.next());
        var valueSource = source.source(propRef);
        // Select first value.
        var firstRef = (EntityReference)
                source.source(valueSource.next()).next();
        var firstValue = ((PrimitiveReference) source.source(
                source.property(firstRef, "@value")).next()).getValue();
        Assertions.assertEquals("1", firstValue);
        var firstType = ((PrimitiveReference) source.source(
                source.property(firstRef, "@type")).next()).getValue();
        Assertions.assertEquals("number", firstType);
        // Select second value.
        var secondRef = (EntityReference)
                source.source(valueSource.next()).next();
        var secondValue = ((PrimitiveReference)
                source.source(source.property(secondRef, "@value"))
                        .next()).getValue();
        Assertions.assertEquals("string", secondValue);
        var secondType = ((PrimitiveReference)
                source.source(source.property(secondRef, "@type"))
                        .next()).getValue();
        Assertions.assertEquals("string", secondType);
        // Select third value.
        var thirdRef = (EntityReference)
                source.source(valueSource.next()).next();
        var thirdValue = ((PrimitiveReference) source.source(
                source.property(thirdRef, "@value")).next()).getValue();
        Assertions.assertEquals("true", thirdValue);
        var thirdType = ((PrimitiveReference) source.source(
                source.property(thirdRef, "@type")).next()).getValue();
        Assertions.assertEquals("boolean", thirdType);
    }

}
