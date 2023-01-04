package cz.skodape.hdt.core.reference;

/**
 * Reference to a primitive value. Unlike other references this one
 * provide direct access to reading the value. Meaning the value must
 * be read at time of creation of this reference.
 */
public interface PrimitiveReference extends Reference {

    /**
     * Return string value.
     */
    String getValue();

    /**
     * Return string identification of the value's type.
     */
    String getType();

}
