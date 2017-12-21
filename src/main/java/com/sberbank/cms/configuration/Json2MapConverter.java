package com.sberbank.cms.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.postgresql.util.PGobject;
import org.springframework.core.convert.converter.Converter;

import java.util.HashMap;
import java.util.Map;

public class Json2MapConverter implements Converter<PGobject, Map> {
    private final Gson gson = new GsonBuilder().serializeNulls().create();

    @Override
    public Map convert(PGobject source) {
        if (source.getValue() != null) {
            return gson.fromJson(source.getValue(), Map.class);
        }
        return new HashMap();
    }
}