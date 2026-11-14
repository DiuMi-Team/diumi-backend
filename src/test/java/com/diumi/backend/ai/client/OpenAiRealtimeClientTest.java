package com.diumi.backend.ai.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.diumi.backend.ai.config.OpenAiProperties;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class OpenAiRealtimeClientTest {

    @Test
    void requestsOfficialClientSecretEndpoint() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        OpenAiProperties properties = new OpenAiProperties("sk-test", URI.create("https://api.openai.com"),
                new OpenAiProperties.Realtime("gpt-realtime", 600));
        OpenAiRealtimeClient client = new OpenAiRealtimeClient(builder, properties);

        server.expect(requestTo("https://api.openai.com/v1/realtime/client_secrets"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer sk-test"))
                .andExpect(header("OpenAI-Safety-Identifier", "visitor-123"))
                .andExpect(content().json("""
                        {
                          "expires_after":{"anchor":"created_at","seconds":600},
                          "session":{
                            "type":"realtime",
                            "model":"gpt-realtime",
                            "output_modalities":["audio"],
                            "instructions":"짧게 안내하세요.",
                            "tool_choice":"none"
                          }
                        }
                        """))
                .andRespond(withSuccess("""
                        {
                          "value":"ek_test",
                          "expires_at":1792990800,
                          "session":{"id":"sess_7f3a","model":"gpt-realtime"}
                        }
                        """, MediaType.APPLICATION_JSON));

        OpenAiRealtimeClient.ClientSecret result =
                client.createClientSecret("visitor-123", "짧게 안내하세요.");

        assertThat(result.value()).isEqualTo("ek_test");
        assertThat(result.expiresAt()).isEqualTo(1792990800L);
        assertThat(result.conversationId()).isEqualTo("sess_7f3a");
        server.verify();
    }
}
