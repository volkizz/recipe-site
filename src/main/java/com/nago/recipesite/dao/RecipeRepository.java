package com.nago.recipesite.dao;

import com.nago.recipesite.model.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import java.math.BigInteger;
import java.util.List;

public interface RecipeRepository extends CrudRepository<Recipe, Long> {
  @RestResource(rel = "nameStartsWith", path = "nameStartsWith")
  List<Recipe> findByNameStartsWith(@Param("name") String name);

  @RestResource(rel = "description-contains", path = "containsDescription")
  List<Recipe> findByDescriptionContaining(@Param("description") String description);

  @RestResource(rel = "category", path = "category")
  List<Recipe> findByCategory(@Param("category") String category);

  @Query(value = "SELECT DISTINCT recipe_id FROM ingredient WHERE id = :ingredientId", nativeQuery = true)
  @RestResource(rel = "ingredientName", path = "ingredientName")
  List<BigInteger> findByIngredient(@Param("ingredientId") Long ingredientId);

}
