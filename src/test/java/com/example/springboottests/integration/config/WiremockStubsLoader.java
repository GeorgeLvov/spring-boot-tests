package com.example.springboottests.integration.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.springframework.util.Assert;
import wiremock.com.fasterxml.jackson.core.type.TypeReference;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Utility class for loading WireMock stubs.
 * <p>
 * This class provides methods to load stub mappings into a WireMock server instance.
 */
public class WiremockStubsLoader {

    private final WireMockServer wireMockServer;

    /**
     * Constructs a new WiremockStubsLoader with the specified WireMockServer instance.
     *
     * @param wireMockServer the WireMockServer instance
     */
    public WiremockStubsLoader(WireMockServer wireMockServer) {
        this.wireMockServer = wireMockServer;
    }

    /**
     * Add stub mappings in JSON format to the WireMock server.
     *
     * @param mappingsJson Array of jsons with stub mappings
     * @throws IllegalArgumentException if mappingsJson is empty
     */
    public void loadStubs(String... mappingsJson) {
        Assert.notEmpty(mappingsJson, "mappingsJson must not be empty");

        Arrays.stream(mappingsJson)
                .map(WiremockStubsLoader::addSquareBrackets)
                .map(json -> Json.read(json, new TypeReference<List<StubMapping>>() { }))
                .flatMap(Collection::stream)
                .forEach(wireMockServer::addStubMapping);
    }

    private static String addSquareBrackets(String json) {
        if (!json.startsWith("[")) {
            json = "[" + json;
        }

        if (!json.endsWith("]")) {
            json += "]";
        }

        return json;
    }
}
