package cz.skodape.hdt.selector.path;

import cz.skodape.hdt.core.OperationFailed;
import cz.skodape.hdt.core.reference.Reference;
import cz.skodape.hdt.core.source.EntitySource;
import cz.skodape.hdt.core.selector.Selector;
import cz.skodape.hdt.core.SelectorContext;

class PathSelector implements Selector {

    private final PathSelectorConfiguration configuration;

    private EntitySource input;

    public PathSelector(PathSelectorConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void initialize(
            SelectorContext context, EntitySource input)
            throws OperationFailed {
        this.input = input;
        for (PathSelectorConfiguration.Path step : this.configuration.path) {
            this.input = new PathStepSelector(
                    context.defaultSource, step, this.input);
        }
    }

    @Override
    public EntitySource split() throws OperationFailed {
        PathSelector result = new PathSelector(this.configuration);
        result.input = this.input.split();
        return result;
    }

    @Override
    public Reference next() throws OperationFailed {
        return this.input.next();
    }

}
