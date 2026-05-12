package com.project.ticket.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.project.ticket.domain.seat.repository.SeatRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class ThymeleafMvpFlowTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    SeatRepository seatRepository;

    @Test
    void browserFlowCreatesConcertAndPurchasesTicket() throws Exception {
        MockHttpSession artistSession = new MockHttpSession();
        signup(artistSession, "artist@example.com", "Artist", "ARTIST");
        login(artistSession, "artist@example.com");

        MvcResult createConcert = mockMvc.perform(post("/concerts")
                        .session(artistSession)
                        .param("title", "Spring Concert")
                        .param("venue", "Olympic Park")
                        .param("concertDate", "2026-06-01T20:00")
                        .param("ticketOpenDate", "2026-05-01T10:00")
                        .param("ticketCloseDate", "2026-05-31T23:59")
                        .param("price", "10000"))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        String concertLocation = createConcert.getResponse().getRedirectedUrl();
        assertThat(concertLocation).startsWith("/concerts/");
        Long concertId = Long.valueOf(concertLocation.substring("/concerts/".length()));

        mockMvc.perform(get("/concerts").session(artistSession))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Spring Concert")));

        mockMvc.perform(get("/concerts/{concertId}", concertId).session(artistSession))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("A-1")));

        MockHttpSession fanSession = new MockHttpSession();
        signup(fanSession, "fan@example.com", "Fan", "FAN");
        login(fanSession, "fan@example.com");
        Long seatId = seatRepository.findByConcertId(concertId).getFirst().getId();

        mockMvc.perform(post("/tickets")
                        .session(fanSession)
                        .param("seatId", seatId.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tickets/me"));

        mockMvc.perform(get("/tickets/me").session(fanSession))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Spring Concert")))
                .andExpect(content().string(containsString("COMPLETED")));
    }

    private void signup(MockHttpSession session, String email, String name, String role) throws Exception {
        mockMvc.perform(post("/signup")
                        .session(session)
                        .param("email", email)
                        .param("password", "password123")
                        .param("name", name)
                        .param("role", role))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    private void login(MockHttpSession session, String email) throws Exception {
        mockMvc.perform(post("/login")
                        .session(session)
                        .param("email", email)
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/concerts"));
    }
}
