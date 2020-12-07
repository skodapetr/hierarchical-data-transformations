package cz.skodape.hdt;

import java.net.URL;

public class TestUtils {

    public static URL urlFromResource(String fileName) {
        URL result = Thread.currentThread().getContextClassLoader()
                .getResource(fileName);
        if (result == null) {
            throw new RuntimeException(
                    "Required resource '" + fileName + "' is missing.");
        }
        return result;
    }

}
