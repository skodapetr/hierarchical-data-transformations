package cz.skodape.hdt.rdf.rdf4j;

import java.io.File;
import java.net.URL;

public class TestUtils {

    public static File fileFromResource(String fileName) {
        URL result = Thread.currentThread().getContextClassLoader()
                .getResource(fileName);
        if (result == null) {
            throw new RuntimeException(
                    "Required resource '" + fileName + "' is missing.");
        }
        return new File(result.getPath());
    }

}
