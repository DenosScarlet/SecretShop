package com.secretshop.keycloak.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * KeycloakController handles web requests related to the home and work_env pages of the secretshop application.
 */
@Controller
public class KeycloakController {

    /**
     * Maps the root URL ("/") to the home page.
     *
     * @return the name of the view to render for the home page
     */
    @GetMapping("/")
    public String home() {
        return "home";
    }

    /**
     * Maps the "/work_env" URL to the menu page and sets the authenticated user's username in the model.
     *
     * @param user  the authenticated OIDC (OpenID Connect) user
     * @param model Model object for passing data to the view
     * @return the name of the view to render for the menu page, or redirects to home if user is not authenticated
     */
    @GetMapping("/work_env")
    public String workEnv(@AuthenticationPrincipal OidcUser user, Model model) {
        if (user != null) {
            model.addAttribute("username", user.getPreferredUsername());
        } else {
            return "redirect:/oauth2/authorization/keycloak";  // Redirect to login page if not authenticated
        }
        return "work_env";
    }
    @GetMapping("/api/work_env")
    public String workEnv(Model model) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof Jwt) {
            Jwt jwt = (Jwt) principal;
            String username = jwt.getClaim("preferred_username");
            model.addAttribute("username", username);
            return "work_env";
        } else {
            return "redirect:/oauth2/authorization/keycloak"; // Redirect to login page if not authenticated
        }
    }
}