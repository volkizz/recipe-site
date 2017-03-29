package com.nago.recipesite.web.controller;

import com.nago.recipesite.dao.RecipeRepository;
import com.nago.recipesite.dao.UserRepository;
import com.nago.recipesite.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserController {

  @Autowired
  private RecipeRepository recipes;

  @Autowired
  private UserRepository users;

  @RequestMapping(value = {"/profile"})
  public String profile(Model model) {
    User user = (User) model.asMap().get("currentUser");
    model.addAttribute("user", user);
    model.addAttribute("authenticated", true);

    return "profile";
  }

  @RequestMapping("/users/{id}")
  public String userProfile(@PathVariable("id") Long id, Model model) {
    User user = users.findOne(id);
    User currentUser = (User) model.asMap().get("currentUser");
    if(currentUser != null) {
      if (currentUser.isAdmin()) {
        model.addAttribute("authenticated", true);
      }
    }
    model.addAttribute("user", user);

    return "profile";
  }
}
