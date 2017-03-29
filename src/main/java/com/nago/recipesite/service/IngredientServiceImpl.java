package com.nago.recipesite.service;

import com.nago.recipesite.dao.IngredientRepository;
import com.nago.recipesite.model.Ingredient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngredientServiceImpl implements IngredientService {
    @Autowired
    private IngredientRepository ingredientRepository;

    @Override
    public void save(Ingredient ingredient) {
        ingredientRepository.save(ingredient);
    }

    @Override
    public List<Ingredient> findByName(String name) {
        return ingredientRepository.findByName(name);
    }
}
