package cz.skodape.hdt.json.jackson;

import cz.skodape.hdt.json.java.JsonOutput;
import cz.skodape.hdt.json.java.JsonOutputConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class JacksonOutputTest {

    private static final JsonOutputConfiguration STRING =
            new JsonOutputConfiguration(
                    JsonOutputConfiguration.Type.String);

    private static final JsonOutputConfiguration BOOLEAN =
            new JsonOutputConfiguration(
                    JsonOutputConfiguration.Type.Boolean);

    private static final JsonOutputConfiguration NUMBER =
            new JsonOutputConfiguration(
                    JsonOutputConfiguration.Type.Number);

    @Test
    public void writePrimitives() throws IOException {
        Writer writer = new StringWriter();
        JsonOutput output = new JsonOutput(writer, false);
        output.openNewObject();
        output.setNextKey("key");
        output.writeValue(STRING, "value");
        output.setNextKey("1");
        output.writeValue(NUMBER, "1");
        output.setNextKey("is");
        output.writeValue(BOOLEAN, "true");
        output.closeLastObject();
        String expected = "{\"key\":\"value\",\"1\":1,\"is\":true}";
        Assertions.assertEquals(expected, writer.toString());
    }

    @Test
    public void booleanConversion() throws IOException {
        Writer writer = new StringWriter();
        JsonOutput output = new JsonOutput(writer, false);
        output.openNewArray();
        output.writeValue(BOOLEAN, "1");
        output.writeValue(BOOLEAN, "true");
        output.writeValue(BOOLEAN, "True");
        output.writeValue(BOOLEAN, "0");
        output.writeValue(BOOLEAN, "false");
        output.writeValue(BOOLEAN, "False");
        output.closeLastArray();
        String expected = "[true,true,true,false,false,false]";
        Assertions.assertEquals(expected, writer.toString());
    }


    @Test
    public void sanitizeString() throws IOException {
        Writer writer = new StringWriter();
        JsonOutput output = new JsonOutput(writer, false);
        output.openNewArray();
        output.writeValue(STRING, "start\r\nend");
        output.writeValue(STRING, "start\t\"end");
        output.closeLastArray();
        String expected = "[\"start\\r\\nend\",\"start\\t\\\"end\"]";
        Assertions.assertEquals(expected, writer.toString());
    }

    @Test
    public void numberConversion() throws IOException {
        Writer writer = new StringWriter();
        JsonOutput output = new JsonOutput(writer, false);
        output.openNewArray();
        output.writeValue(NUMBER, "1");
        output.writeValue(NUMBER, "1.1");
        output.writeValue(NUMBER, "1,1");
        output.writeValue(NUMBER, "-1.0");
        output.writeValue(NUMBER, "1 2");
        output.closeLastArray();
        String expected = "[1,1.1,1.1,-1.0,12]";
        Assertions.assertEquals(expected, writer.toString());
    }


}
