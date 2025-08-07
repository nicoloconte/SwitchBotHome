package it.myhouse.switchbot.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class JsonBodyHandler<T> implements HttpResponse.BodyHandler<T> {
    private final Class<T> clazz;
    private final TypeReference<T> typeReference;

    public JsonBodyHandler(Class<T> clazz) {
        this.clazz = clazz;
        this.typeReference = null;
    }

    public JsonBodyHandler(TypeReference<T> typeReference) {
        this.clazz = null;
        this.typeReference = typeReference;
    }

    @Override
    public HttpResponse.BodySubscriber<T> apply(HttpResponse.ResponseInfo responseInfo) {
        return HttpResponse.BodySubscribers.mapping(
                HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8),
                body -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        if (clazz != null) {
                            return mapper.readValue(body, clazz);
                        } else if (typeReference != null) {
                            return mapper.readValue(body, typeReference);
                        } else {
                            throw new IllegalStateException("No type information provided");
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to parse JSON", e);
                    }
                }
        );
    }
}