package com.project.ticket.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {

    @GetMapping("/")
    public String home() {
        return "redirect:/concerts";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("pageTitle", "로그인");
        return "pages/login";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("pageTitle", "회원가입");
        return "pages/signup";
    }

    @GetMapping("/concerts")
    public String concertList(Model model) {
        model.addAttribute("pageTitle", "공연 목록");
        return "pages/concert-list";
    }

    @GetMapping("/concerts/new")
    public String concertCreate(Model model) {
        model.addAttribute("pageTitle", "공연 생성");
        return "pages/concert-create";
    }

    @GetMapping("/concerts/{concertId}")
    public String concertDetail(@PathVariable Long concertId, Model model) {
        model.addAttribute("pageTitle", "공연 상세");
        model.addAttribute("concertId", concertId);
        return "pages/concert-detail";
    }

    @GetMapping("/tickets/me")
    public String myTickets(Model model) {
        model.addAttribute("pageTitle", "내 티켓");
        return "pages/my-tickets";
    }
}
