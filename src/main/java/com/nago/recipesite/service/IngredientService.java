package com.nago.recipesite.service;

import com.nago.recipesite.model.Ingredient;

import java.util.List;

public interface IngredientService {
    void save(Ingredient ingredient);
    List<Ingredient> findByName(String name);
}
