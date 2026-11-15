package com.diumi.backend.common.config;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class OpenApiDocumentationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void exposesAllTwelveApiOperations() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("디우미(Diumi) API"))
                .andExpect(jsonPath("$.paths['/visitors'].post").exists())
                .andExpect(jsonPath("$.paths['/scenarios'].get").exists())
                .andExpect(jsonPath("$.paths['/scenarios/{scenarioId}'].get").exists())
                .andExpect(jsonPath("$.paths['/scenarios/{scenarioId}/missions'].get").exists())
                .andExpect(jsonPath("$.paths['/sessions'].post").exists())
                .andExpect(jsonPath("$.paths['/sessions/{sessionId}/actions'].post").exists())
                .andExpect(jsonPath("$.paths['/sessions/{sessionId}/steps/{stepId}/complete'].post").exists())
                .andExpect(jsonPath("$.paths['/sessions/{sessionId}/ai/connections'].post").exists())
                .andExpect(jsonPath("$.paths['/sessions/{sessionId}/ai/messages'].post").exists())
                .andExpect(jsonPath("$.paths['/sessions/{sessionId}/end'].post").exists())
                .andExpect(jsonPath("$.paths['/sessions/{sessionId}/result'].get").exists())
                .andExpect(jsonPath("$.paths['/sessions/{sessionId}'].get").exists());
    }

    @Test
    void documentsVisitorHeaderAndErrorCodes() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("X-Visitor-Id")))
                .andExpect(content().string(containsString("VISITOR_REQUIRED")))
                .andExpect(content().string(containsString("AI_CONNECTION_FAILED")))
                .andExpect(content().string(containsString("SESSION_NOT_READY_TO_COMPLETE")));
    }
}
