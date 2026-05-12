package com.project.ticket.web;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class ThymeleafBackendIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    void concertCreateValidationFailureRendersFormError() throws Exception {
        MockHttpSession artistSession = new MockHttpSession();
        signup(artistSession, "artist-validation@example.com", "Artist", "ARTIST");
        login(artistSession, "artist-validation@example.com");

        mockMvc.perform(post("/concerts")
                        .session(artistSession)
                        .param("title", "")
                        .param("venue", "Olympic Park")
                        .param("concertDate", "2026-06-01T20:00")
                        .param("ticketOpenDate", "2026-05-01T10:00")
                        .param("ticketCloseDate", "2026-05-31T23:59")
                        .param("price", "10000"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("공연명을 입력해주세요.")));
    }

    private void signup(MockHttpSession session, String email, String name, String role) throws Exception {
        mockMvc.perform(post("/signup")
                        .session(session)
                        .param("email", email)
                        .param("password", "password123")
                        .param("name", name)
                        .param("role", role))
                .andExpect(status().is3xxRedirection());
    }

    private void login(MockHttpSession session, String email) throws Exception {
        mockMvc.perform(post("/login")
                        .session(session)
                        .param("email", email)
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection());
    }
}
