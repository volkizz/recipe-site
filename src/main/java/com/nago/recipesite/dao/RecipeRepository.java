package com.nago.recipesite.dao;

import com.nago.recipesite.model.Recipe;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RecipeRepository extends PagingAndSortingRepository<Recipe, Long> {
}
