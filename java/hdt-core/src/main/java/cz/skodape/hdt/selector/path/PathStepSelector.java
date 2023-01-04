package cz.skodape.hdt.selector.path;

import cz.skodape.hdt.core.reference.ArrayReference;
import cz.skodape.hdt.core.reference.EntityReference;
import cz.skodape.hdt.core.OperationFailed;
import cz.skodape.hdt.core.source.PropertySource;
import cz.skodape.hdt.core.reference.Reference;
import cz.skodape.hdt.core.source.EntitySource;

class PathStepSelector implements EntitySource {

    private final PropertySource propertySource;

    private final PathSelectorConfiguration.Path step;

    private final EntitySource inputSource;

    private EntitySource nextSource = null;

    public PathStepSelector(
            PropertySource propertySource,
            PathSelectorConfiguration.Path step,
            EntitySource inputSource) {
        this.propertySource = propertySource;
        this.step = step;
        this.inputSource = inputSource;
    }

    @Override
    public EntitySource split() throws OperationFailed {
        PathStepSelector result = new PathStepSelector(
                propertySource, step, inputSource.split());
        if (nextSource != null) {
            result.nextSource = nextSource.split();
        }
        return result;
    }

    @Override
    public Reference next() throws OperationFailed {
        if (nextSource == null) {
            return prepareNextSource();
        }
        Reference next = nextSource.next();
        if (next == null) {
            return prepareNextSource();
        }
        return next;
    }

    private Reference prepareNextSource() throws OperationFailed {
        Reference next;
        while ((next = inputSource.next()) != null) {
            if (!next.isObjectReference()) {
                continue;
            }
            EntityReference objectReference = (EntityReference) next;
            ArrayReference arrayReference;
            if (step.reverse) {
                arrayReference = propertySource.reverseProperty(
                        objectReference, step.predicate);
            } else {
                arrayReference = propertySource.property(
                        objectReference, step.predicate);
            }
            nextSource = propertySource.source(arrayReference);
            Reference result = nextSource.next();
            if (result == null) {
                continue;
            }
            return result;
        }
        return null;
    }

}
