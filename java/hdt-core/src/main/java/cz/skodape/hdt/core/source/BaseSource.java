package cz.skodape.hdt.core.source;

import cz.skodape.hdt.core.OperationFailed;

public interface BaseSource {

    /**
     * Open the source and prepare it for reading.
     */
    void open() throws OperationFailed;

    /**
     * Close the source.
     */
    void close();

}
