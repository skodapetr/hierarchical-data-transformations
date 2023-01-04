package cz.skodape.hdt.core.output;

import cz.skodape.hdt.core.OperationFailed;
import cz.skodape.hdt.model.TransformationTarget;

import java.io.IOException;

/**
 * Interface for writing out the data.
 */
public interface Output {

    /**
     * Open output for writing.
     */
    void open() throws OperationFailed;

    void openNewArray() throws IOException;

    void closeLastArray() throws IOException;

    void openNewObject() throws IOException;

    void closeLastObject() throws IOException;

    /**
     * Set value for writing next values.
     */
    void setNextKey(String key) throws IOException;

    /**
     * Write primitive value.
     *
     * @param configuration Optional configuration object, can be null.
     * @param value         Value to output.
     */
    void writeValue(TransformationTarget configuration, String value)
            throws IOException;

    /**
     * Called when transformation is finished can be used to flush the
     * content.
     */
    void onTransformationFinished() throws OperationFailed;

    /**
     * Close the output.
     */
    void close();

}
