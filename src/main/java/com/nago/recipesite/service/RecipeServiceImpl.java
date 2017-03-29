package com.nago.recipesite.service;

import com.nago.recipesite.dao.IngredientRepository;
import com.nago.recipesite.dao.RecipeRepository;
import com.nago.recipesite.model.Ingredient;
import com.nago.recipesite.model.Recipe;
import com.nago.recipesite.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecipeServiceImpl implements RecipeService {
    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private UserService users;

    @Override
    public boolean save(Recipe recipe, User user) {
        if(recipe.getCreatedBy() == user || user.isAdmin()) {
            recipeRepository.save(recipe);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Recipe> findAll() {
        return (List<Recipe>) recipeRepository.findAll();
    }

    @Override
    public Recipe findOne(Long id) {
        return recipeRepository.findOne(id);
    }

    @Override
    public boolean delete(Recipe recipe, User user) {
        List<User> affectedUsers = new ArrayList<>();
        if(recipe.getCreatedBy() == user || user.isAdmin()) {
            for (User u : users.findAll()){
                if (u.getFavoriteRecipes().contains(recipe)) {
                    u.removeFavoriteRecipe(recipe);
                    affectedUsers.add(u);
                }
            }
            recipe.getCreatedBy().removeOwnRecipe(recipe);
            users.save(affectedUsers);
            recipe.setCreatedBy(null);
            recipeRepository.save(recipe);
            recipeRepository.delete(recipe.getId());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Recipe> findByNameStartsWith(String searchQuery) {
        return recipeRepository.findByNameStartsWith(searchQuery);
    }

    @Override
    public List<Recipe> findByDescriptionContaining(String searchQuery) {
        return recipeRepository.findByDescriptionContaining(searchQuery);
    }

    @Override
    public boolean delete(Long id, User user) {
        return delete(recipeRepository.findOne(id), user);
    }


    @Override
    public List<Recipe> findByCategoryName(String category) {
        return recipeRepository.findByCategory(category);
    }

    @Override
    public List<Long> findByIngredient(String searchQuery) {
        List<Ingredient> ingredients = ingredientRepository.findByName(searchQuery);
        List<BigInteger> integers = new ArrayList<>();
        ingredients.forEach(ingredient -> integers.addAll(recipeRepository.findByIngredient(ingredient.getId())));
        List<Long> longs = new ArrayList<>();
        integers.forEach(integer -> {
            longs.add(integer.longValue());
        });
        return longs;
    }
}
