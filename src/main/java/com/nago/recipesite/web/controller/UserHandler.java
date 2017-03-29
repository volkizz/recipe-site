package com.nago.recipesite.web.controller;

import com.nago.recipesite.model.User;
import com.nago.recipesite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice(basePackages = "com.nago.recipesite.web.controller")
public class UserHandler {
    @Autowired
    private UserService users;

    @ExceptionHandler(AccessDeniedException.class)
    public String redirectNonUser(RedirectAttributes attributes) {
        attributes.addAttribute("errorMessage", "Please login before accessing website");
        return "redirect:/login";
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public String redirectNotFound(RedirectAttributes attributes) {
        attributes.addAttribute("errorMessage", "Username not found");
        return "redirect:/login";
    }

    @ModelAttribute("currentUser")
    public User addUser() {
        if(SecurityContextHolder.getContext().getAuthentication() != null) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = users.findByUsername(username);
            if(user != null) {
                return user;
            } else {
                throw new UsernameNotFoundException("Username not found");
            }
        } else {
            throw new AccessDeniedException("Not logged in");
        }
    }
}
