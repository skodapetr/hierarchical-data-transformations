package cz.skodape.hdt.selector.identity;

import cz.skodape.hdt.core.OperationFailed;
import cz.skodape.hdt.core.reference.Reference;
import cz.skodape.hdt.core.source.EntitySource;
import cz.skodape.hdt.core.selector.Selector;
import cz.skodape.hdt.core.SelectorContext;

/**
 * Just pass the input to output.
 */
class IdentitySelector implements Selector {

    private EntitySource input;

    @Override
    public void initialize(SelectorContext context, EntitySource input) {
        this.input = input;
    }

    @Override
    public EntitySource split() throws OperationFailed {
        IdentitySelector result = new IdentitySelector();
        result.input = this.input.split();
        return result;
    }

    @Override
    public Reference next() throws OperationFailed {
        return this.input.next();
    }

}
