package com.bmisiek.libraries.mockmvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;

public class MyRequestBuilders extends MockMvcRequestBuilders {
    public static @NotNull MockHttpServletRequestBuilder postJson(String url, Object content) throws JsonProcessingException {
        return MockMvcRequestBuilders.post(url)
                .contentType("application/json")
                .content(content instanceof String ? (String) content : toJson(content));
    }

    public static @NotNull MockHttpServletRequestBuilder postJson(String url, Object content, String bearer) throws JsonProcessingException {
        return MockMvcRequestBuilders.post(url)
                .contentType("application/json")
                .content(content instanceof String ? (String) content : toJson(content))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearer);
    }

    public static MockHttpServletRequestBuilder post(String uriTemplate, String bearer) {
        return MockMvcRequestBuilders.post(URI.create(uriTemplate))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearer);
    }

    public static MockHttpServletRequestBuilder getAuthed(String uriTemplate, String bearer) {
        return MockMvcRequestBuilders.get(URI.create(uriTemplate))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearer);
    }

    private static @NotNull String toJson(Object content) throws JsonProcessingException {
        var objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(content);
    }
}
