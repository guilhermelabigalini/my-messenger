package my.messenger.androidclient.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

class JSON {
    private static ObjectMapper serializer;

    static {
        serializer = new ObjectMapper();
        serializer.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static String stringify(Object obj) throws JsonProcessingException {
        return serializer.writeValueAsString(obj);
    }

    public static <T> T parse(String message, Class<T> clazz) throws IOException {
        return serializer.readValue(message, clazz);
    }
}
