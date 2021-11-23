package com.rob.uiapi.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class JsonRawDeserializer extends StdDeserializer<JsonRaw> {

	private static final long serialVersionUID = 1L;

	public JsonRawDeserializer() {
        super(JsonRaw.class);
    }

    @Override
    public JsonRaw deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        return new JsonRaw(parser.getCodec().readTree(parser).toString());
    }
}
