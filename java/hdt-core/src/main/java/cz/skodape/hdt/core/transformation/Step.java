package cz.skodape.hdt.core.transformation;

import cz.skodape.hdt.core.reference.Reference;
import cz.skodape.hdt.model.transformation.KindArray;
import cz.skodape.hdt.model.transformation.BaseTransformation;
import cz.skodape.hdt.model.transformation.KindObject;
import cz.skodape.hdt.model.transformation.KindPrimitive;

class Step {

    public final Reference reference;

    public final BaseTransformation definition;

    public final StepType type;

    public Step(Reference reference) {
        this.reference = reference;
        this.definition = null;
        this.type = StepType.Reference;
    }

    public Step(KindObject definition) {
        this.reference = null;
        this.definition = definition;
        this.type = StepType.Object;
    }

    public Step(KindArray definition) {
        this.reference = null;
        this.definition = definition;
        this.type = StepType.Array;
    }

    public Step(KindPrimitive definition) {
        this.reference = null;
        this.definition = definition;
        this.type = StepType.Primitive;
    }

}
