package com.nago.recipesite.web.controller;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import com.nago.recipesite.enums.Category;
import com.nago.recipesite.model.Recipe;
import com.nago.recipesite.model.User;
import com.nago.recipesite.service.IngredientService;
import com.nago.recipesite.service.RecipeService;
import com.nago.recipesite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class RecipeController {

  @Autowired
  private RecipeService recipeService;

  @Autowired
  private IngredientService ingredientService;

  @Autowired
  private UserService users;

  @RequestMapping(value = {"/", "/recipes"})
  public String listRecipes(Model model) {
    User user = (User) model.asMap().get("currentUser");
    List<Recipe> recipeList = recipeService.findAll();

    if (user != null) {
      model.addAttribute("authenticated", user.isAdmin());
    }
    model.addAttribute("allRecipes", recipeList);
    model.addAttribute("categories", Category.values());
    return "index";
  }

  @RequestMapping(value = "/recipes/{id}")
  public String recipeDetail(Model model, @PathVariable Long id) throws IOException {
    Recipe recipe = recipeService.findOne(id);
    model.addAttribute("recipe", recipe);
    return "detail";
  }

  @RequestMapping(value = "/recipes/{id}/image", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
  public @ResponseBody
  byte[] showImageOnId(@PathVariable("id") Long id) {
    byte[] b = recipeService.findOne(id).getImage();
    return b;
  }

  @RequestMapping("/recipes/add")
  public String formNewRecipe(Model model) {
    Recipe recipe = new Recipe();
    model.addAttribute("recipe", recipe);
    model.addAttribute("redirect", "/");
    model.addAttribute("action", "/recipes/new");
    model.addAttribute("heading", "New Recipe");
    model.addAttribute("submit", "Save");
    model.addAttribute("categories", Category.values());
    return "edit";
  }

  @RequestMapping(value = "/recipes/new", method = RequestMethod.POST)
  public String addRecipe(Recipe recipe, Model model, @RequestParam("uploadedFile") MultipartFile file) {
    User user = (User) model.asMap().get("currentUser");
    try {
      byte[] bytes = file.getBytes();
      recipe.setImage(bytes);
    } catch (IOException e) {
      e.printStackTrace();
    }
    recipe.setCreatedBy(user);
    recipe.getIngredients().forEach(ingredient -> ingredient.setRecipe(recipe));
    recipeService.save(recipe, user);
    users.save(user);

    return "redirect:/recipes/" + recipe.getId();
  }

  @RequestMapping("/recipes/{id}/edit")
  public String editRecipe(Model model, @PathVariable("id") Long id) {
    Recipe recipe = recipeService.findOne(id);
    model.addAttribute("recipe", recipe);
    model.addAttribute("redirect", "/recipes/" + id);
    model.addAttribute("action", "/recipes/" + id + "/edit");
    model.addAttribute("heading", "Edit Recipe");
    model.addAttribute("submit", "Edit");
    model.addAttribute("categories", Category.values());
    return "edit";
  }

  @RequestMapping(value = "/recipes/{id}/edit", method = RequestMethod.POST)
  public String saveEditedRecipe(Recipe recipe, @PathVariable("id") Long id, @RequestParam("uploadedFile") MultipartFile file) {
    User user = recipeService.findOne(id).getCreatedBy();
    try {
      byte[] bytes = file.getBytes();
      recipe.setImage(bytes);
    } catch (IOException e) {
      e.printStackTrace();
    }
    recipe.setCreatedBy(user);
    recipe.getIngredients().forEach(ingredient -> ingredient.setRecipe(recipe));
    recipeService.save(recipe, user);

    return "redirect:/recipes/" + recipe.getId();
  }

  @RequestMapping(path = "/recipes/{id}/delete", method = POST)
  public String deleteRecipe(@PathVariable("id") int id, Model model) {
    User user = (User) model.asMap().get("currentUser");
    Recipe recipe = recipeService.findOne((long) id);
    recipeService.delete(recipe, user);

    return "redirect:/";
  }

  @RequestMapping("/search")
  public String search(Model model,
                       @RequestParam(value = "searchQuery", required = false) String searchQuery,
                       @RequestParam(value = "category", required = false) String category,
                       @RequestParam(value = "method") String method, HttpServletRequest req,
                       HttpServletResponse res) {
    List<Recipe> queriedRecipes = new ArrayList<>();
    if(searchQuery != null) {
      model.addAttribute("search", searchQuery);

      if(method.equals("name")) {
        List<Recipe> namedRecipes = recipeService.findByNameStartsWith(searchQuery);
        if(!category.equals("")) {
          List<Recipe> categorizedRecipes = recipeService.findByCategoryName(category);
          queriedRecipes = namedRecipes.stream().filter(categorizedRecipes::contains).collect(Collectors.toList());
        } else {
          queriedRecipes = namedRecipes;
        }
      }

      else if(method.equals("description")) {
        List<Recipe> namedRecipes = recipeService.findByDescriptionContaining(searchQuery);
        if(!category.equals("")) {
          List<Recipe> categorizedRecipes = recipeService.findByCategoryName(category);
          queriedRecipes = namedRecipes.stream().filter(categorizedRecipes::contains).collect(Collectors.toList());
        } else {
          queriedRecipes = namedRecipes;
        }
      } else if(method.equals("ingredient")) {
        List<Long> recipeIds = recipeService.findByIngredient(searchQuery);
        List<Recipe> searchedRecipes = new ArrayList<>();
        recipeIds.forEach(id -> {
          Recipe recipe = recipeService.findOne(id);
          searchedRecipes.add(recipe);
        });
        if(!category.equals("")) {
          List<Recipe> categorizedRecipes = recipeService.findByCategoryName(category);
          queriedRecipes = searchedRecipes.stream().filter(categorizedRecipes::contains).collect(Collectors.toList());
        } else {
          queriedRecipes = searchedRecipes;
        }
      }

    }
    model.addAttribute("allRecipes", queriedRecipes);
    model.addAttribute("categories", Category.values());
    return "index";
  }

  @RequestMapping(value = "/recipes/{id}/favorite", method = POST)
  public String toggleFavorite(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
    User user = (User) model.asMap().get("currentUser");
    Recipe recipe = recipeService.findOne(id);
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
