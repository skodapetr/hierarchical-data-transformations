package cz.skodape.hdt.core.selector;

import cz.skodape.hdt.core.OperationFailed;
import cz.skodape.hdt.core.reference.EntityReference;
import cz.skodape.hdt.core.source.EntitySource;
import cz.skodape.hdt.core.source.ReferenceSource;

/**
 * Selectors act as filters for {@link EntitySource}.
 */
public interface Selector extends ReferenceSource<EntityReference> {

    /**
     * Set source, must be called before any method.
     */
    void bind(EntitySource input) throws OperationFailed;

}
