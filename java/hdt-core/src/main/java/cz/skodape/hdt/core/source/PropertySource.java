package cz.skodape.hdt.core.source;

import cz.skodape.hdt.core.OperationFailed;
import cz.skodape.hdt.core.reference.ArrayReference;
import cz.skodape.hdt.core.reference.EntityReference;
import cz.skodape.hdt.core.reference.Reference;

public interface PropertySource extends BaseSource {

    /**
     * Create a source for reading an {@link ArrayReference} items.
     */
    ReferenceSource<Reference> source(ArrayReference reference)
            throws OperationFailed;

    /**
     * Return values for given object and property.
     */
    ArrayReference property(EntityReference reference, String property)
            throws OperationFailed;

    /**
     * Return array of all references that have given property with
     * given reference as a value.
     */
    ArrayReference reverseProperty(Reference reference, String property)
            throws OperationFailed;

}
