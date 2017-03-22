package com.nago.recipesite.core;

import com.nago.recipesite.dao.RecipeRepository;
import com.nago.recipesite.dao.UserRepository;
import com.nago.recipesite.enums.Category;
import com.nago.recipesite.enums.Measurement;
import com.nago.recipesite.model.Ingredient;
import com.nago.recipesite.model.Recipe;
import com.nago.recipesite.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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
        new User("zero", "zero", "password", new String[]{"ROLE_USER"}),
        new User("admin", "Mykola", "12345", new String[]{"ROLE_USER", "ROLE_ADMIN"}),
        new User("yana", "Yana", "password", new String[]{"ROLE_USER"}),
        new User("dino", "Dino", "password", new String[]{"ROLE_USER"}),
        new User("ula", "Ula", "password", new String[]{"ROLE_USER"}),
        new User("tesla", "Tesla", "password", new String[]{"ROLE_USER"})
    );
    users.save(userList);

    String[] recipeTemplates = {"Recipe 1", "Recipe 2", "Recipe 3", "Recipe 4", "Recipe 5"};
    String[] recipeDescriptionTemplates = {"Recipe Description 1", "Recipe Description 2", "Recipe Description 3", "Recipe Description 4", "Recipe Description 5"};
    String [] recipeImagesTemplates = {"bloodyMary", "pasta", "salad", "soup", "steak"};
    String[] ingredientTemplates = {"Ingredient 1", "Ingredient 2", "Ingredient 3", "Ingredient 4", "Ingredient 5"};
    String[] ingredientConditionTemplates = {"Condition 1", "Condition 2", "Condition 3", "Condition 4", "Condition 5"};
    String[] instructionTemplates = {"Step 1", "Step 2", "Step 3", "Step 4", "Step 5"};
    String[] instructionDescriptionTemplates = {"Description 1", "Description 2", "Description 3", "Description 4", "Description 5"};

    List<Recipe> bunchOfRecipes = new ArrayList<>();
    IntStream.range(0, 100)
        .forEach(i -> {
          User user = userList.get(random(userList.size()));
          String recipeName = recipeTemplates[i % random(recipeTemplates.length)];
          String recipeDescription = recipeDescriptionTemplates[i % random(recipeDescriptionTemplates.length)];
          String recipeImage = recipeImagesTemplates[i % random(recipeImagesTemplates.length)];
         // String recipeInstruction = instructionDescriptionTemplates[random(instructionDescriptionTemplates.length)];
          Recipe r = new Recipe(recipeName, recipeDescription, extractBytes(recipeImage), Category.values()[random(Category.values().length)].getName(), random(60), random(120));
          r.addInstruction(0, "Some instruction how to do some shit");
          r.setCreatedBy(user);
          IntStream.rangeClosed(0, random(ingredientTemplates.length))
              .forEach(e -> {
                String ingredientName = ingredientTemplates[e % ingredientTemplates.length];
                String ingredientCondition = ingredientConditionTemplates[random(ingredientConditionTemplates.length)];
                Ingredient ingredient = new Ingredient(ingredientName, ingredientCondition, random(10), Measurement.values()[random(Measurement.values().length)].getName());
                r.addIngredient(ingredient);
              });
          user.addOwnRecipe(r);
          bunchOfRecipes.add(r);
        });

    userList.forEach(user -> IntStream.rangeClosed(0, 5).forEach(r -> {
       Recipe recipe = bunchOfRecipes.get(random(bunchOfRecipes.size()));
       user.addFavoriteRecipe(recipe);
       }));

    recipes.save(bunchOfRecipes);
    users.save(userList);
  }

  private int random(int i) {
    return (int) (Math.random() * (i - 1) + 1);
  }

  private byte[] extractBytes (String imageName)  {
    byte[] image = new byte[]{};
    File file = new File(String.format("src/main/resources/static/mockDbImages/%s.png", imageName));
    String absolutePath = file.getAbsolutePath();
    Path path = Paths.get(absolutePath);

    try {
      image = Files.readAllBytes(path);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return image;
  }
}
