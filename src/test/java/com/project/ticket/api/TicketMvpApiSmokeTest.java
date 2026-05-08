package com.project.ticket.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class TicketMvpApiSmokeTest {
    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void mvpApiFlowCreatesConcertAndPurchasesTicket() throws Exception {
        signup("artist@example.com", "Artist", "ARTIST");
        signup("fan@example.com", "Fan", "FAN");
        String artistToken = login("artist@example.com");
        String fanToken = login("fan@example.com");

        Long concertId = createConcert(artistToken);
        Long seatId = firstSeatId(concertId);

        JsonNode ticket = purchaseTicket(fanToken, seatId);

        assertThat(ticket.path("ticketId").asLong()).isPositive();
        assertThat(ticket.path("seatId").asLong()).isEqualTo(seatId);
        assertThat(myTickets(fanToken)).hasSize(1);
    }

    @Test
    void concertCreationRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/concerts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(concertCreateJson()))
                .andExpect(status().isForbidden());
    }

    private void signup(String email, String name, String role) throws Exception {
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "email", email,
                                "password", "password123",
                                "name", name,
                                "role", role
                        ))))
                .andExpect(status().isCreated());
    }

    private String login(String email) throws Exception {
        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "email", email,
                                "password", "password123"
                        ))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).path("accessToken").asText();
    }

    private Long createConcert(String artistToken) throws Exception {
        String response = mockMvc.perform(post("/concerts")
                        .header("Authorization", bearer(artistToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(concertCreateJson()))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).path("concertId").asLong();
    }

    private Long firstSeatId(Long concertId) throws Exception {
        String response = mockMvc.perform(get("/concerts/{concertId}/seats", concertId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode seats = objectMapper.readTree(response);
        assertThat(seats).hasSize(2);
        return seats.get(0).path("seatId").asLong();
    }

    private JsonNode purchaseTicket(String fanToken, Long seatId) throws Exception {
        String response = mockMvc.perform(post("/tickets")
                        .header("Authorization", bearer(fanToken))
                        .header("Idempotency-Key", "smoke-ticket-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("seatId", seatId))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response);
    }

    private JsonNode myTickets(String fanToken) throws Exception {
        String response = mockMvc.perform(get("/tickets/me")
                        .header("Authorization", bearer(fanToken)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response);
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    private String concertCreateJson() throws Exception {
        return json(Map.of(
                "title", "Spring Concert",
                "venue", "Olympic Park",
                "concertDate", LocalDateTime.now().plusDays(30).toString(),
                "ticketOpenDate", LocalDateTime.now().minusDays(1).toString(),
                "ticketCloseDate", LocalDateTime.now().plusDays(20).toString(),
                "rowCount", 1,
                "colCount", 2,
                "price", 10000
        ));
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
