package com.project.ticket.web;

import com.project.ticket.domain.auth.dto.LoginResponse;
import com.project.ticket.domain.auth.service.AuthService;
import com.project.ticket.domain.concert.dto.ConcertResponse;
import com.project.ticket.domain.concert.service.ConcertService;
import com.project.ticket.domain.seat.service.SeatService;
import com.project.ticket.domain.ticket.dto.TicketPurchaseRequest;
import com.project.ticket.domain.ticket.service.TicketService;
import com.project.ticket.domain.user.entity.UserRole;
import com.project.ticket.global.auth.AuthenticatedUser;
import com.project.ticket.global.auth.JwtProvider;
import com.project.ticket.global.exception.BusinessException;
import com.project.ticket.web.form.ConcertCreateForm;
import com.project.ticket.web.form.LoginForm;
import com.project.ticket.web.form.SignupForm;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PageController {
    private static final String SESSION_USER_ID = "userId";
    private static final String SESSION_USER_ROLE = "userRole";

    private final AuthService authService;
    private final JwtProvider jwtProvider;
    private final ConcertService concertService;
    private final SeatService seatService;
    private final TicketService ticketService;

    public PageController(
            AuthService authService,
            JwtProvider jwtProvider,
            ConcertService concertService,
            SeatService seatService,
            TicketService ticketService
    ) {
        this.authService = authService;
        this.jwtProvider = jwtProvider;
        this.concertService = concertService;
        this.seatService = seatService;
        this.ticketService = ticketService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/concerts";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("pageTitle", "로그인");
        model.addAttribute("form", new LoginForm("", ""));
        return "pages/login";
    }

    @PostMapping("/login")
    public String login(
            @ModelAttribute LoginForm form,
            HttpSession session,
            Model model
    ) {
        try {
            LoginResponse response = authService.login(form.toRequest());
            AuthenticatedUser user = jwtProvider.parse(response.accessToken());
            session.setAttribute(SESSION_USER_ID, user.userId());
            session.setAttribute(SESSION_USER_ROLE, user.role());
            return "redirect:/concerts";
        } catch (BusinessException exception) {
            model.addAttribute("pageTitle", "로그인");
            model.addAttribute("form", form);
            model.addAttribute("errorMessage", exception.errorCode().message());
            return "pages/login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("pageTitle", "회원가입");
        model.addAttribute("form", new SignupForm("", "", "", UserRole.FAN));
        return "pages/signup";
    }

    @PostMapping("/signup")
    public String signup(
            @ModelAttribute SignupForm form,
            Model model
    ) {
        try {
            authService.signup(form.toRequest());
            return "redirect:/login";
        } catch (BusinessException exception) {
            model.addAttribute("pageTitle", "회원가입");
            model.addAttribute("form", form);
            model.addAttribute("errorMessage", exception.errorCode().message());
            return "pages/signup";
        }
    }

    @GetMapping("/concerts")
    public String concertList(Model model, HttpSession session) {
        model.addAttribute("pageTitle", "공연 목록");
        model.addAttribute("concerts", concertService.findConcerts());
        addSession(model, session);
        return "pages/concert-list";
    }

    @GetMapping("/concerts/new")
    public String concertCreate(Model model, HttpSession session) {
        if (!isArtist(session)) {
            return "redirect:/login";
        }
        model.addAttribute("pageTitle", "공연 생성");
        addSession(model, session);
        return "pages/concert-create";
    }

    @PostMapping("/concerts")
    public String createConcert(
            @ModelAttribute ConcertCreateForm form,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        Long userId = userId(session);
        if (userId == null || !isArtist(session)) {
            return "redirect:/login";
        }
        try {
            ConcertResponse response = concertService.createConcert(userId, form.toRequest());
            return "redirect:/concerts/" + response.concertId();
        } catch (BusinessException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.errorCode().message());
            return "redirect:/concerts/new";
        }
    }

    @GetMapping("/concerts/{concertId}")
    public String concertDetail(
            @PathVariable Long concertId,
            Model model,
            HttpSession session
    ) {
        model.addAttribute("pageTitle", "공연 상세");
        model.addAttribute("concert", concertService.findConcert(concertId));
        model.addAttribute("seats", seatService.findSeats(concertId));
        addSession(model, session);
        return "pages/concert-detail";
    }

    @PostMapping("/tickets")
    public String purchaseTicket(
            @RequestParam Long seatId,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        Long userId = userId(session);
        if (userId == null) {
            return "redirect:/login";
        }
        try {
            ticketService.purchase(userId, new TicketPurchaseRequest(seatId), UUID.randomUUID().toString());
            return "redirect:/tickets/me";
        } catch (BusinessException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.errorCode().message());
            return "redirect:/tickets/me";
        }
    }

    @GetMapping("/tickets/me")
    public String myTickets(Model model, HttpSession session) {
        Long userId = userId(session);
        if (userId == null) {
            return "redirect:/login";
        }
        model.addAttribute("pageTitle", "내 티켓");
        model.addAttribute("tickets", ticketService.findMyTicketViews(userId));
        addSession(model, session);
        return "pages/my-tickets";
    }

    private void addSession(Model model, HttpSession session) {
        model.addAttribute("loggedIn", userId(session) != null);
        model.addAttribute("userRole", session.getAttribute(SESSION_USER_ROLE));
    }

    private Long userId(HttpSession session) {
        return (Long) session.getAttribute(SESSION_USER_ID);
    }

    private boolean isArtist(HttpSession session) {
        return session.getAttribute(SESSION_USER_ROLE) == UserRole.ARTIST;
    }
}
