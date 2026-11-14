package com.diumi.backend.ai.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "openai")
public record OpenAiProperties(
        String apiKey,
        @NotNull URI baseUrl,
        @NotNull @Valid Realtime realtime
) {
    public record Realtime(
            @NotBlank String model,
            @Min(10) @Max(7200) long clientSecretTtlSeconds
    ) {
    }
}
