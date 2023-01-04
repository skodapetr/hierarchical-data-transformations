package cz.skodape.hdt.model.transformation;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration of output object.
 */
public class KindObject extends BaseTransformation {

    public Map<String, BaseTransformation> properties =
            new HashMap<>();

    public Map<KindPrimitive, BaseTransformation> dynamicProperties =
            new HashMap<>();

}
