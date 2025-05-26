package com.bmisiek.libraries.mockmvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class MyRequestBuilders extends MockMvcRequestBuilders {
    @SneakyThrows
    public static @NotNull MockHttpServletRequestBuilder postJson(String url, Object content) {
        return MockMvcRequestBuilders.post(url)
                .contentType("application/json")
                .content(content instanceof String ? (String) content : toJson(content));
    }

    private static @NotNull String toJson(Object content) throws JsonProcessingException {
        var objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(content);
    }
}
