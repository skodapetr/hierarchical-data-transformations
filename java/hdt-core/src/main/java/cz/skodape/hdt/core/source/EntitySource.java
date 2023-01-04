package cz.skodape.hdt.core.source;

import cz.skodape.hdt.core.OperationFailed;
import cz.skodape.hdt.core.reference.EntityReference;

/**
 * Server as a source for {@link EntityReference}.
 */
public interface EntitySource
        extends BaseSource, ReferenceSource<EntityReference> {

    /**
     * Return an independent copy of the store in the same state.
     */
    EntitySource split() throws OperationFailed;


}
