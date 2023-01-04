package cz.skodape.hdt.core.transformation;

import cz.skodape.hdt.core.OperationFailed;
import cz.skodape.hdt.core.reference.PrimitiveReference;
import cz.skodape.hdt.core.reference.Reference;
import cz.skodape.hdt.core.selector.Selector;
import cz.skodape.hdt.core.source.PropertySource;
import cz.skodape.hdt.core.source.EntitySource;
import cz.skodape.hdt.model.transformation.KindArray;
import cz.skodape.hdt.model.transformation.BaseTransformation;
import cz.skodape.hdt.model.transformation.KindObject;
import cz.skodape.hdt.model.transformation.KindPrimitive;
import cz.skodape.hdt.model.SelectorConfiguration;
import cz.skodape.hdt.model.TransformationFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Stack;

/**
 * Core logic for executing the transformation.
 */
public class Transformation {

    private static final Logger LOG =
            LoggerFactory.getLogger(Transformation.class);

    protected final TransformationFile transformation;

    /**
     * Store path to currently node the is currently being processed.
     */
    protected final Stack<Step> path = new Stack<>();

    public Transformation(TransformationFile transformation) {
        this.transformation = transformation;
    }

    /**
     * Execute transformation.
     */
    public void execute() throws OperationFailed, IOException {
        openSources();
        PropertySource rootSource = getRootSource();
        BaseTransformation rootTransformation = transformation.transformation;
        transformRoot(rootTransformation, rootSource.roots());
        closeSources();
        output.onTransformationFinished();
    }

    protected void openSources() throws OperationFailed {
        for (var entry : transformation.sources.entrySet()) {
            LOG.info("Opening source: {}", entry.getKey());
            entry.getValue().open();
        }
        LOG.info("Opening sources ... done");
    }

    protected PropertySource getRootSource() {
        return context.sources.get(transformation.rootSource);
    }

    /**
     * We can not use split on root source, as some sources may not support
     * it. As a result we have special limitations and functions to deal with
     * roots.
     */
    protected void transformRoot(
            BaseTransformation definition, EntitySource source)
            throws OperationFailed, IOException {
        EntitySource filteredSource = applySelectors(definition, source);
        if (definition instanceof KindArray) {
            transformRootArray(
                    (KindArray) definition, filteredSource);
        } else if (definition instanceof KindObject) {
            transformRootObject(
                    (KindObject) definition, filteredSource);
        } else {
            throw new OperationFailed("Unsupported root definition.");
        }
    }

    protected void transformRootArray(
            KindArray arrayDefinition, EntitySource source)
            throws OperationFailed, IOException {
        output.openNewArray();
        Reference next;
        while ((next = source.next()) != null) {
            for (BaseTransformation itemDefinition : arrayDefinition.items) {
                transform(itemDefinition, asSource(next));
            }
        }
        output.closeLastArray();
    }

    protected EntitySource asSource(Reference reference) {
        return new MemoryReferenceSource<>(reference);
    }

    /**
     * We can utilize only one Resource for object root. We allow the root
     * object to consists of more object.
     */
    protected void transformRootObject(
            KindObject objectDefinition, EntitySource source)
            throws OperationFailed, IOException {
        output.openNewObject();
        Reference next;
        while ((next = source.next()) != null) {
            for (var entry : objectDefinition.properties.entrySet()) {
                output.setNextKey(entry.getKey());
                transform(entry.getValue(), asSource(next));
            }
        }
        output.closeLastObject();
    }

    protected void transform(
            BaseTransformation definition, EntitySource source)
            throws OperationFailed, IOException {
        if (definition instanceof KindArray) {
            KindArray arrayDefinition =
                    (KindArray) definition;
            path.add(new Step(arrayDefinition));
            transformArray(arrayDefinition, source);
            path.pop();
        } else if (definition instanceof KindObject) {
            KindObject objectDefinition =
                    (KindObject) definition;
            path.add(new Step(objectDefinition));
            transformObject(objectDefinition, source);
            path.pop();
        } else if (definition instanceof KindPrimitive) {
            KindPrimitive primitiveDefinition =
                    (KindPrimitive) definition;
            path.add(new Step(primitiveDefinition));
            transformPrimitive(primitiveDefinition, source);
            path.pop();
        } else {
            throw createError("Unknown transformation definition.");
        }
    }

    protected OperationFailed createError(String messages, Object... args) {
        return new OperationFailed(messages, args);
    }

    protected void transformArray(
            KindArray definition, EntitySource source)
            throws OperationFailed, IOException {
        EntitySource filteredSource = applySelectors(definition, source);
        output.openNewArray();
        Reference next;
        while ((next = filteredSource.next()) != null) {
            path.push(new Step(next));
            for (BaseTransformation itemDefinition : definition.items) {
                transform(itemDefinition, asSource(next));
            }
            path.pop();
        }
        output.closeLastArray();
    }

    private EntitySource applySelectors(
            BaseTransformation definition, EntitySource source)
            throws OperationFailed {
        EntitySource result = source;
        for (SelectorConfiguration configuration : definition.selectors) {
            Selector selector = configuration.createSelector();
            selector.initialize(context, result);
            result = selector;
        }
        return result;
    }

    protected void transformObject(
            KindObject definition, EntitySource source)
            throws OperationFailed, IOException {
        EntitySource filteredSource = applySelectors(definition, source);
        output.openNewObject();
        for (var entry : definition.properties.entrySet()) {
            output.setNextKey(entry.getKey());
            transform(entry.getValue(), filteredSource.split());
        }
        output.closeLastObject();
    }

    protected void transformPrimitive(
            KindPrimitive definition, EntitySource source)
            throws OperationFailed, IOException {
        String value = getValueForPrimitive(definition, source);
        if (value == null) {
            return;
        }
        output.writeValue(definition.outputConfiguration, value);
    }

    protected String getValueForPrimitive(
            KindPrimitive definition, EntitySource source)
            throws OperationFailed {
        if (definition.constantValue != null) {
            return definition.constantValue;
        }
        EntitySource filteredSource = applySelectors(definition, source);
        Reference reference = filteredSource.next();
        if (reference == null) {
            return definition.defaultValue;
        }
        if (!(reference instanceof PrimitiveReference)) {
            throw createError("Reference must be PrimitiveReference.");
        }
        String result = ((PrimitiveReference) reference).getValue();
        // Check there is no next value.
        Reference next = filteredSource.next();
        if (next != null) {
            onMultiplePrimitiveValues(reference, next, filteredSource);
        }
        return result;
    }

    protected void onMultiplePrimitiveValues(
            Reference head, Reference next, EntitySource source)
            throws OperationFailed {
        StringBuilder content = new StringBuilder();
        content.append("\n  ");
        content.append(head.asDebugString());
        content.append("\n  ");
        content.append(next.asDebugString());
        Reference rest;
        while ((rest = source.next()) != null) {
            content.append("\n  ");
            content.append(rest.asDebugString());
        }
        throw createError(
                "Multiple values detected for primitive: {}", content);
    }

    protected void closeSources() {
        LOG.info("Closing sources ...");
        for (PropertySource source : context.sources.values()) {
            source.close();
        }
        LOG.info("Closing sources ... done");
    }

}
