package com.nago.recipesite.dao;

import com.nago.recipesite.model.Ingredient;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface IngredientRepository extends PagingAndSortingRepository<Ingredient, Long> {
}
