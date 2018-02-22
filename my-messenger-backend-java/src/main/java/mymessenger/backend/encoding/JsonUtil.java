/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymessenger.backend.encoding;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;

/**
 *
 * @author guilherme
 */
public final class JsonUtil {

    private static final ObjectMapper objectMapper;

    private JsonUtil() {
    }

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static String toString(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }

    public static byte[] toBytes(Object o) throws JsonProcessingException {
        return toString(o).getBytes();
    }

    public static <T> T decode(byte[] str, Class<T> cls) throws JsonProcessingException, IOException {
        return objectMapper.readValue(str, cls);
    }

    public static <T> T decode(String str, Class<T> cls) throws JsonProcessingException, IOException {
        return objectMapper.readValue(str, cls);
    }

}
