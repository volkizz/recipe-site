package com.nago.recipesite.service;


import com.nago.recipesite.model.Recipe;
import com.nago.recipesite.model.User;

import java.util.List;

public interface RecipeService {
  boolean save(Recipe recipe, User user);

  List<Recipe> findAll();

  Recipe findOne(Long id);

  boolean delete(Long id, User user);

  boolean delete(Recipe recipe, User user);

  List<Recipe> findByNameStartsWith(String searchQuery);

  List<Recipe> findByDescriptionContaining(String searchQuery);

  List<Recipe> findByCategoryName(String category);

  List<Long> findByIngredient(String searchQuery);
}
