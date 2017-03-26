package com.nago.recipesite.dao;

import com.nago.recipesite.model.Ingredient;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface IngredientRepository extends PagingAndSortingRepository<Ingredient, Long> {
  List<Ingredient> findByName(String name);
}
