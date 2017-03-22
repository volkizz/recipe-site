package com.nago.recipesite.handler;

import com.nago.recipesite.dao.UserRepository;
import com.nago.recipesite.model.Recipe;
import com.nago.recipesite.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler(Recipe.class)
public class RecipeHandler {
  private final UserRepository users;

  @Autowired
  public RecipeHandler(UserRepository users) {
    this.users=users;
  }

  @HandleBeforeSave
  @HandleBeforeCreate
  public void setOwnerOnCreate(Recipe recipe) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = users.findByUsername(username);
    recipe.setCreatedBy(user);
  }
}
