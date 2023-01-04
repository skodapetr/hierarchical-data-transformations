package cz.skodape.hdt.model.transformation;

/**
 * Configuration of output primitive value.
 */
public class KindPrimitive extends BaseTransformation {

    /**
     * Interface for primitive value configuration.
     */
    public interface Value {

    }

    public String type = null;

    public Value value = null;

}
