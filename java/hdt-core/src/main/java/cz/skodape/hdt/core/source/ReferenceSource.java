package cz.skodape.hdt.core.source;

import cz.skodape.hdt.core.OperationFailed;
import cz.skodape.hdt.core.reference.Reference;

/**
 * Basically a custom iterator over {@link Reference}.
 */
public interface ReferenceSource<T extends Reference> {

    /**
     * Return null where there are no more references.
     */
    T next() throws OperationFailed;

}
