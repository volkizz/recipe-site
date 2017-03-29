package com.nago.recipesite.dao;

import com.nago.recipesite.model.Ingredient;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IngredientRepository extends CrudRepository<Ingredient, Long> {
  List<Ingredient> findByName(String name);
}
