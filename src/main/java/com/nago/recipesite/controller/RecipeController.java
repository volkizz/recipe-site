package com.nago.recipesite.controller;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import com.nago.recipesite.core.FlashMessage;
import com.nago.recipesite.dao.IngredientRepository;
import com.nago.recipesite.dao.RecipeRepository;
import com.nago.recipesite.dao.UserRepository;
import com.nago.recipesite.enums.Category;
import com.nago.recipesite.enums.Measurement;
import com.nago.recipesite.model.Ingredient;
import com.nago.recipesite.model.Recipe;
import com.nago.recipesite.model.User;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class RecipeController {

  @Autowired
  private RecipeRepository recipes;

  @Autowired
  private IngredientRepository ingredients;

  @Autowired
  private UserRepository users;

  protected User user() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return users.findByUsername(auth.getName());
  }

  @RequestMapping(value = {"/", "/recipes"})
  public String listRecipes(Model model) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User user = users.findByUsername(auth.getName());

    List<Recipe> recipeList = (List<Recipe>) recipes.findAll();

    if (user != null) {
      model.addAttribute("authenticated", user.isAdmin());
    }
    model.addAttribute("user", user);
    model.addAttribute("allRecipes", recipeList);
    model.addAttribute("name", user().getName());
    model.addAttribute("categories", Category.values());
    return "index";
  }

  @RequestMapping(value = "/recipes/{id}")
  public String recipeDetail(Model model, @PathVariable Long id) throws IOException {
    Recipe recipe = recipes.findOne(id);

    model.addAttribute("user", user());
    model.addAttribute("name", user().getName());
    model.addAttribute("recipe", recipe);

    return "detail";
  }

  @RequestMapping(value = "/recipes/{id}/image", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
  public @ResponseBody
  byte[] showImageOnId(@PathVariable("id") Long id) {
    byte[] b = recipes.findOne(id).getImage();
    return b;
  }

  @RequestMapping("/recipes/add")
  public String formNewRecipe(Model model) {
    Recipe recipe = new Recipe();

    model.addAttribute("recipe", recipe);
    model.addAttribute("action", "/recipes/new");
    model.addAttribute("heading", "New Recipe");
    model.addAttribute("submit", "Save");
    model.addAttribute("categories", Category.values());
    model.addAttribute("measurements", Measurement.values());
    return "edit";
  }

  @RequestMapping(value = "/recipes/new", method = RequestMethod.POST)
  public String addRecipe(Recipe recipe, @RequestParam("uploadedFile") MultipartFile file) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User user = users.findByUsername(auth.getName());
    try {
      byte[] bytes = file.getBytes();
      recipe.setImage(bytes);
    } catch (IOException e) {
      e.printStackTrace();
    }
    recipe.setCreatedBy(user);
    recipe.getIngredients().forEach(ingredient -> ingredient.setRecipe(recipe));
    recipes.save(recipe);

    return "redirect:/recipes/" + recipe.getId();
  }

  @RequestMapping("/recipes/{id}/edit")
  public String editRecipe(Model model, @PathVariable("id") Long id) {
    Recipe recipe = recipes.findOne(id);
    String name = user().getName();
    model.addAttribute("recipe", recipe);
    model.addAttribute("name", name);
    model.addAttribute("action", "/recipes/" + id + "/edit");
    model.addAttribute("heading", "Edit Recipe");
    model.addAttribute("submit", "Edit");
    model.addAttribute("categories", Category.values());
    model.addAttribute("measurements", Measurement.values());
    return "edit";
  }

  @RequestMapping(value = "/recipes/{id}/edit", method = RequestMethod.POST)
  public String saveEditedRecipe(Recipe recipe, @PathVariable("id") Long id,
                                 @RequestParam("uploadedFile") MultipartFile file) {
    User user = recipes.findOne(id).getCreatedBy();

    try {
      byte[] bytes = file.getBytes();
      recipe.setImage(bytes);
    } catch (IOException e) {
      e.printStackTrace();
    }
    recipe.setCreatedBy(user);
    recipe.getIngredients().forEach(ingredient -> ingredient.setRecipe(recipe));
    recipes.save(recipe);

    return "redirect:/recipes/" + recipe.getId();
  }

  @RequestMapping(path = "/recipes/{id}/delete", method = POST)
  public String deleteRecipe(@PathVariable("id") int id) {
    List<User> allUsers = users.findAll();
    Recipe recipe = recipes.findOne((long) id);

    for (User user : allUsers){
      if (user.getFavoriteRecipes().contains(recipe)){
        user.removeFavoriteRecipe(recipe);
      }
    }
    recipe.getCreatedBy().removeOwnRecipe(recipe);
    users.save(allUsers);
    recipe.setCreatedBy(null);
    recipes.save(recipe);
    recipes.delete(recipe.getId());
    return "redirect:/";
  }

  @RequestMapping("/search")
  public String search(Model model,
                       @RequestParam(value = "searchQuery", required = false) String searchQuery,
                       @RequestParam(value = "category", required = false) String category,
                       @RequestParam(value = "method") String method, HttpServletRequest request,
                       HttpServletResponse response) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User user = users.findByUsername(auth.getName());
    List<Recipe> queriedRecipes = new ArrayList<>();
    if (searchQuery != null) {
      model.addAttribute("search", searchQuery);
      if (method.equals("description")) {
        List<Recipe> namedRecipes = recipes.findByDescriptionContaining(searchQuery);
        if (!category.equals("")) {
          List<Recipe> categorizedRecipes = recipes.findByCategory(category);
          queriedRecipes = namedRecipes.stream().filter(categorizedRecipes::contains).collect(
              Collectors.toList());
        } else {
          queriedRecipes = namedRecipes;
        }
      } else if (method.equals("ingredient")) {
        List<Ingredient> ingredientsList = ingredients.findByName(searchQuery);
        List<BigInteger> integers = new ArrayList<>();
        ingredientsList
            .forEach(ingredient -> integers.addAll(recipes.findByIngredient(ingredient.getId())));
        List<Long> recipeIds = new ArrayList<>();
        integers.forEach(integer -> {
          recipeIds.add(integer.longValue());
        });

        List<Recipe> searchedRecipes = new ArrayList<>();
        recipeIds.forEach(id -> {
          Recipe recipe = recipes.findOne(id);
          searchedRecipes.add(recipe);
        });
        if (!category.equals("")) {
          List<Recipe> categorizedRecipes = recipes.findByCategory(category);
          queriedRecipes =
              searchedRecipes.stream().filter(categorizedRecipes::contains)
                  .collect(Collectors.toList());
        } else {
          queriedRecipes = searchedRecipes;
        }
      }

    }
    model.addAttribute("name", user().getName());
    model.addAttribute("allRecipes", queriedRecipes);
    model.addAttribute("categories", Category.values());
    return "index";
  }

  @RequestMapping(value = "/recipes/{id}/favorite", method = POST)
  public String toggleFavorite(@PathVariable("id") Long id, HttpServletRequest request) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User user = users.findByUsername(auth.getName());
    Recipe recipe = recipes.findOne(id);
    String referer = request.getHeader("Referer");
    if (user.getFavoriteRecipes().contains(recipe)) {
      user.removeFavoriteRecipe(recipe);
    } else {
      user.addFavoriteRecipe(recipe);
    }
    users.save(user);
    return "redirect:" + referer;
  }
}
