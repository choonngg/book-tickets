package com.project.ticket.web;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class PageControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    void loginPageRenders() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("로그인")));
    }

    @Test
    void signupPageRenders() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("회원가입")));
    }

    @Test
    void concertListPageRenders() throws Exception {
        mockMvc.perform(get("/concerts"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("공연 목록")));
    }

    @Test
    void concertCreatePageRenders() throws Exception {
        mockMvc.perform(get("/concerts/new"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("공연 생성")));
    }

    @Test
    void concertDetailPageRenders() throws Exception {
        mockMvc.perform(get("/concerts/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("공연 상세")));
    }

    @Test
    void myTicketsPageRenders() throws Exception {
        mockMvc.perform(get("/tickets/me"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("내 티켓")));
    }
}
