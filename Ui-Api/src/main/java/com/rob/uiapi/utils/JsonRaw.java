package com.rob.uiapi.utils;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = JsonRawSerializer.class)
@JsonDeserialize(using = JsonRawDeserializer.class)
public class JsonRaw {
    public final String value;

    public JsonRaw(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
