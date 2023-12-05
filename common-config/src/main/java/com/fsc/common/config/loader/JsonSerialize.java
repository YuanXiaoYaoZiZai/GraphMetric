package com.fsc.common.config.loader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class JsonSerialize {
    private static final Logger logger = LoggerFactory.getLogger(JsonSerialize.class);

    public JsonSerialize() {
    }

    public static Object loadJson(File file, TypeReference typeReference) {
        if (typeReference == null) {
            logger.info("Can NOT find Type for json,skip[{}]", file.getAbsolutePath());
            return null;
        } else {
            return fromJson(file, typeReference);
        }
    }

    public static Object fromJson(File json, TypeReference typeReference) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            String jsonStr = JsonLoader.readToString(json);
            return objectMapper.readValue(jsonStr, typeReference);
        } catch (IOException var4) {
            logger.error("Load Json[{}] exception:{}", json.getAbsolutePath(), var4);
            return null;
        }
    }
}

