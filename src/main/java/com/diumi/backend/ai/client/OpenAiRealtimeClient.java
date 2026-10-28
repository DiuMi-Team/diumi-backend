package com.diumi.backend.ai.client;

import com.diumi.backend.ai.config.OpenAiProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class OpenAiRealtimeClient {
    private static final String CLIENT_SECRET_PATH = "/v1/realtime/client_secrets";
    private static final String SAFETY_IDENTIFIER_HEADER = "OpenAI-Safety-Identifier";

    private final RestClient restClient;
    private final OpenAiProperties properties;

    public OpenAiRealtimeClient(RestClient.Builder builder, OpenAiProperties properties) {
        this.properties = properties;
        this.restClient = builder
                .baseUrl(properties.baseUrl().toString())
                .build();
    }

    public ClientSecret createClientSecret(String safetyIdentifier, String instructions) {
        if (properties.apiKey() == null || properties.apiKey().isBlank()) {
            throw new OpenAiRealtimeClientException("OpenAI API key is not configured");
        }

        ClientSecretRequest request = new ClientSecretRequest(
                new ExpiresAfter("created_at", properties.realtime().clientSecretTtlSeconds()),
                new RealtimeSession("realtime", properties.realtime().model(), List.of("audio"),
                        instructions, "none"));
        try {
            ClientSecretResponse response = restClient.post()
                    .uri(CLIENT_SECRET_PATH)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.apiKey())
                    .header(SAFETY_IDENTIFIER_HEADER, safetyIdentifier)
                    .body(request)
                    .retrieve()
                    .body(ClientSecretResponse.class);
            return validate(response);
        } catch (RestClientException exception) {
            throw new OpenAiRealtimeClientException("Failed to create OpenAI Realtime client secret", exception);
        }
    }

    private ClientSecret validate(ClientSecretResponse response) {
        if (response == null || response.value() == null || response.value().isBlank()
                || response.expiresAt() <= 0 || response.session() == null
                || response.session().id() == null || response.session().id().isBlank()
                || response.session().model() == null || response.session().model().isBlank()) {
            throw new OpenAiRealtimeClientException("Invalid OpenAI Realtime client secret response");
        }
        return new ClientSecret(response.value(), response.expiresAt(),
                response.session().id(), response.session().model());
    }

    private record ClientSecretRequest(
            @JsonProperty("expires_after") ExpiresAfter expiresAfter,
            RealtimeSession session
    ) {
    }

    private record ExpiresAfter(String anchor, long seconds) {
    }

    private record RealtimeSession(
            String type,
            String model,
            @JsonProperty("output_modalities") List<String> outputModalities,
            String instructions,
            @JsonProperty("tool_choice") String toolChoice
    ) {
    }

    private record ClientSecretResponse(
            String value,
            @JsonProperty("expires_at") long expiresAt,
            SessionResponse session
    ) {
    }

    private record SessionResponse(String id, String model) {
    }

    public record ClientSecret(String value, long expiresAt, String conversationId, String model) {
    }
}
