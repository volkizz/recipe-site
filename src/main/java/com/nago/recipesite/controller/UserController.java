package com.nago.recipesite.controller;

import com.nago.recipesite.dao.RecipeRepository;
import com.nago.recipesite.dao.UserRepository;
import com.nago.recipesite.model.Recipe;
import com.nago.recipesite.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class UserController {

  @Autowired
  private RecipeRepository recipes;

  @Autowired
  private UserRepository users;

  @RequestMapping(value = {"/profile"})
  public String profile(Model model) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User user = users.findByUsername(auth.getName());

    List<Recipe> ownRecipes = user.getOwnRecipes();
    List<Recipe> favoriteRecipes = user.getFavoriteRecipes();

    model.addAttribute("authenticated", true);

    model.addAttribute("ownRecipes", ownRecipes);
    model.addAttribute("favoriteRecipes", favoriteRecipes);
    model.addAttribute("name", user.getName());
    return "profile";
  }

  @RequestMapping("/users/{id}")
  public String userProfile(@PathVariable("id") Long id, Model model) {
    User user = users.findOne(id);
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User currentUser = users.findByUsername(auth.getName());
    List<Recipe> ownRecipes = user.getOwnRecipes();
    List<Recipe> favoriteRecipes = user.getFavoriteRecipes();
    if(currentUser != null) {
      if (currentUser.isAdmin()) {
        model.addAttribute("authenticated", true);
      }
    }
    model.addAttribute("user", user);
    model.addAttribute("ownRecipes", ownRecipes);
    model.addAttribute("favoriteRecipes", favoriteRecipes);
    model.addAttribute("name", user.getName());
    return "profile";
  }
}
