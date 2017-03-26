package com.nago.recipesite.controller;

import static com.nago.recipesite.core.FlashMessage.Status.FAILURE;
import static com.nago.recipesite.core.FlashMessage.Status.SUCCESS;

import com.nago.recipesite.core.FlashMessage;
import com.nago.recipesite.dao.UserRepository;
import com.nago.recipesite.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LoginController {

  @Autowired
  private UserRepository users;

  @RequestMapping(path = "/login", method = RequestMethod.GET)
  public String loginForm(Model model, HttpServletRequest request) {
    model.addAttribute("user", new User());
    try {
      Object flash = request.getSession().getAttribute("flash");
      model.addAttribute("flash", flash);

      request.getSession().removeAttribute("flash");
    } catch (Exception ex) {
      // "flash" session attribute must not exist...do nothing and proceed normally
    }
    return "login";
  }

  @RequestMapping("/signup")
  public String signup(Model model) {
    model.addAttribute("user", new User());
    return "signup";
  }

  @RequestMapping(value = "/newUser", method = RequestMethod.POST)
  public String newUser(User user, RedirectAttributes attributes) {
    if (users.findByUsername(user.getUsername()) == null) {
      user.setRoles(new String[]{"ROLE_USER"});
      users.save(user);
      attributes.addFlashAttribute("flash", new FlashMessage(String.format("User %s successfully created! Please log in", user.getUsername()), SUCCESS));
      return "redirect:/login";
    } else {
      attributes.addFlashAttribute("flash", new FlashMessage(String.format("Account with username '%s' already exists. " +
          "Please enter a different username.", user.getUsername()), FAILURE));
      return "redirect:/signup";
    }
  }

}
