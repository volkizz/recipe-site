package com.nago.recipesite.core;

import com.nago.recipesite.dao.RecipeRepository;
import com.nago.recipesite.dao.UserRepository;
import com.nago.recipesite.enums.Category;
import com.nago.recipesite.enums.Measurement;
import com.nago.recipesite.model.Ingredient;
import com.nago.recipesite.model.Instruction;
import com.nago.recipesite.model.Recipe;
import com.nago.recipesite.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@Component
public class DatabaseLoader implements ApplicationRunner {
  private final UserRepository users;
  private final RecipeRepository recipes;

  @Autowired
  public DatabaseLoader(UserRepository users, RecipeRepository recipes) {
    this.users = users;
    this.recipes = recipes;
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    List<User> userList = Arrays.asList(
        new User("admin", "Mykola", "12345", new String[]{"ROLE_USER", "ROLE_ADMIN"}),
        new User("yana", "Yana", "password", new String[]{"ROLE_USER"}),
        new User("dino", "Dino", "password", new String[]{"ROLE_USER"}),
        new User("ula", "Ula", "password", new String[]{"ROLE_USER"}),
        new User("tesla", "Tesla", "password", new String[]{"ROLE_USER"})
    );
    users.save(userList);

    String[] recipeTemplates = {"Recipe 1", "Recipe 2", "Recipe 3", "Recipe 4", "Recipe 5"};
    String[] ingredientTemplates = {"Ingredient 1", "Ingredient 2", "Ingredient 3", "Ingredient 4", "Ingredient 5"};
    String[] ingredientConditionTemplates = {"Condition 1", "Condition 2", "Condition 3", "Condition 4", "Condition 5"};
    String[] instructionTemplates = {"Step 1", "Step 2", "Step 3", "Step 4", "Step 5"};
    String[] instructionDescriptionTemplates = {"Description 1", "Description 2", "Description 3", "Description 4", "Description 5"};

    List<Recipe> bunchOfRecipes = new ArrayList<>();
    IntStream.range(0, 100)
        .forEach(i -> {
          String recipeName = recipeTemplates[i % random(recipeTemplates.length)];
          Recipe r = new Recipe(recipeName, Category.values()[random(Category.values().length)], random(60), random(120));

          for (int j = 0; j < random(userList.size()); j++) {
            r.addAdministrator(userList.get(j));
          }
          Collections.shuffle(userList); // excludes users duplications in the room

          IntStream.rangeClosed(0, random(ingredientTemplates.length))
              .forEach(e -> {
                String ingredientName = ingredientTemplates[e % ingredientTemplates.length];
                String ingredientCondition = ingredientConditionTemplates[e % ingredientConditionTemplates.length];
                Ingredient ingredient = new Ingredient(ingredientName, ingredientCondition, random(10), Measurement.values()[random(Measurement.values().length)]);
                r.addIngredient(ingredient);
              });
          IntStream.rangeClosed(0, random(instructionTemplates.length))
              .forEach(a -> {
                String instructionName = instructionTemplates[a % instructionTemplates.length];
                String instructionDescription = instructionDescriptionTemplates[a % instructionDescriptionTemplates.length];
                Instruction c = new Instruction(instructionName, instructionDescription);
                r.addInstruction(c);
              });
          bunchOfRecipes.add(r);
        });
    recipes.save(bunchOfRecipes);
  }

  private int random(int i) {
    return (int) (Math.random() * (i - 1) + 1);
  }

}
