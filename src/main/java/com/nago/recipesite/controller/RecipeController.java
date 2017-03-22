package com.nago.recipesite.controller;

import com.nago.recipesite.core.FlashMessage;
import com.nago.recipesite.dao.IngredientRepository;
import com.nago.recipesite.dao.RecipeRepository;
import com.nago.recipesite.dao.UserRepository;
import com.nago.recipesite.enums.Category;
import com.nago.recipesite.enums.Measurement;
import com.nago.recipesite.model.Recipe;
import com.nago.recipesite.model.User;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

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
    List<Recipe> recipeList = (List<Recipe>) recipes.findAll();

    model.addAttribute("recipes", recipeList);
    model.addAttribute("name", user().getName());
    return "index";
  }

  @RequestMapping(value = "/recipes/{id}")
  public String recipeDetail(Model model, @PathVariable Long id) throws IOException {
    Recipe recipe = recipes.findOne(id);

    model.addAttribute("name", user().getName());
    model.addAttribute("recipe", recipe);

    return "detail";
  }

  @RequestMapping(value = "/recipes/{id}/image", method=RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
  public @ResponseBody byte[] showImageOnId(@PathVariable("id") Long id) {
    byte[] b = recipes.findOne(id).getImage();
    return b;
  }

  @RequestMapping("/recipes/add")
  public String formNewRecipe(Model model) {
    Recipe recipe = new Recipe();

    model.addAttribute("recipe", recipe);
    model.addAttribute("action", "/recipes");
    model.addAttribute("heading", "New Recipe");
    model.addAttribute("submit", "Save");
    model.addAttribute("categories", Category.values());
    model.addAttribute("measurements", Measurement.values());
    return "edit";
  }

  @RequestMapping(value = "/recipes", method = RequestMethod.POST)
  public String addRecipe(Recipe recipe,
                          BindingResult result,
                          RedirectAttributes redirectAttributes) {

    if (result.hasErrors()) {
      redirectAttributes
          .addFlashAttribute("org.springframework.validation.BindingResult.recipe", result);
      redirectAttributes.addFlashAttribute("recipe", recipe);
      return "redirect:/recipes/add";
    }
    recipes.save(recipe);
    recipe.getIngredients().forEach(ingredient -> ingredients.save(ingredient));
    redirectAttributes.addFlashAttribute("flash",
        new FlashMessage("New Recipe Created!!!", FlashMessage.Status.SUCCESS));
    return "redirect:/recipes/" + recipe.getId();
  }
}
