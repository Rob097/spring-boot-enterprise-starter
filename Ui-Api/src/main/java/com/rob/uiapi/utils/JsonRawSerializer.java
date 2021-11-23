package com.rob.uiapi.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class JsonRawSerializer extends StdSerializer<JsonRaw> {

    private static final long serialVersionUID = 1L;

	public JsonRawSerializer() {
        super(JsonRaw.class);
    }

    @Override
    public void serialize(JsonRaw value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        if (value.getValue() == null) {
            generator.writeNull();
        } else {
            generator.writeRawValue(value.getValue());
        }
    }
}
