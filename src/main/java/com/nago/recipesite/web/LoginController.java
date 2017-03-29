package com.nago.recipesite.web;

import com.nago.recipesite.dao.UserRepository;
import com.nago.recipesite.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {

  @Autowired
  private UserRepository users;

  @RequestMapping("/login")
  public String login(Model model) {
    model.addAttribute("user", new User());
    return "login";
  }

  @RequestMapping("/signup")
  public String signup(Model model) {
    model.addAttribute("user", new User());
    return "signup";
  }

  @RequestMapping(value = "/newUser", method = RequestMethod.POST)
  public String newUser(User user) {
    if (users.findByUsername(user.getUsername()) == null) {
      user.setRoles(new String[]{"ROLE_USER"});
      users.save(user);
      return "redirect:/login";
    } else {
      return "redirect:/signup";
    }
  }

}
