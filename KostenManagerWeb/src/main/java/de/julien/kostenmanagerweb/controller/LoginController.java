package de.julien.kostenmanagerweb.controller;

import de.julien.kostenmanagerweb.entity.BenutzerEntity;
import de.julien.kostenmanagerweb.repository.BenutzerRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
public class LoginController {

    private final BenutzerRepository benutzerRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginController(BenutzerRepository benutzerRepository,
                           PasswordEncoder passwordEncoder) {
        this.benutzerRepository = benutzerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginSeite() {
        return "login";
    }

    @PostMapping("/login")
    public String login(String username, String password, HttpSession session, Model model) {

        BenutzerEntity benutzer = benutzerRepository.findByUsername(username);

        if (benutzer == null) {
            model.addAttribute("fehler", "Benutzer wurde nicht gefunden.");
            return "login";
        }

        if (!passwordEncoder.matches(
                password,
                benutzer.getPassword_hash()
        )) {
            model.addAttribute("fehler", "Passwort ist falsch.");
            return "login";
        }

        session.setAttribute("benutzerId", benutzer.getId());
        session.setAttribute("username", benutzer.getUsername());

        return "redirect:/startmenue";
    }

    @GetMapping("/registrieren")
    public String registrierenSeite() {
        return "registrieren";
    }

    @PostMapping("/registrieren")
    public String registrieren(String username, String password, Model model) {

        if (username == null || username.isBlank()) {
            model.addAttribute("fehler", "Benutzername darf nicht leer sein.");
            return "registrieren";
        }

        if (password == null || password.isBlank()) {
            model.addAttribute("fehler", "Passwort darf nicht leer sein.");
            return "registrieren";
        }

        BenutzerEntity existiertSchon = benutzerRepository.findByUsername(username.trim());

        if (existiertSchon != null) {
            model.addAttribute("fehler", "Benutzername ist bereits vergeben.");
            return "registrieren";
        }

        benutzerRepository.save(
                new BenutzerEntity(
                        username.trim(),
                        passwordEncoder.encode(password)
                )
        );




        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}