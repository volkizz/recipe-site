package recipes.web.controller;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


import com.nago.recipesite.dao.IngredientRepository;
import com.nago.recipesite.dao.RecipeRepository;
import com.nago.recipesite.dao.UserRepository;
import com.nago.recipesite.enums.Category;
import com.nago.recipesite.model.Ingredient;
import com.nago.recipesite.model.Recipe;
import com.nago.recipesite.model.User;
import com.nago.recipesite.web.controller.RecipeController;
import com.nago.recipesite.web.controller.UserHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class RecipeControllerTest {
  private MockMvc mockMvc;

  @InjectMocks
  private RecipeController recipeController;

  @Mock
  private RecipeRepository recipeService;
  @Mock
  private IngredientRepository ingredientService;

  @Mock
  private UserHandler userHandler;
  @Mock
  private UserRepository users;

  @Before
  public void setUp() throws Exception {
    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    viewResolver.setPrefix("classpath:/templates/");
    viewResolver.setSuffix(".html");
    mockMvc = MockMvcBuilders.standaloneSetup(recipeController)
        .setViewResolvers(viewResolver).build();
  }

  @Test
  public void recipesPageHasAllRecipesShown() throws Exception {
    when(recipeService.findAll()).thenReturn(testRecipes());

    mockMvc.perform(get("/recipes"))
        .andExpect(model().attribute("allRecipes", iterableWithSize(2)));
    verify(recipeService).findAll();
  }

  @Test
  public void recipeDetailPageReturnsCorrectRecipe() throws Exception {
    when(recipeService.findOne(1L)).thenReturn(testRecipe());

    mockMvc.perform(get("/recipes/1"))
        .andExpect(model().attribute("recipe", testRecipe()));
  }

  @Test
  public void editPageLoadsCorrectItems() throws Exception {
    when(recipeService.findOne(1L)).thenReturn(testRecipe());

    mockMvc.perform(get("/recipes/1/edit"))

        .andExpect(model().attribute("recipe", testRecipe()))
        .andExpect(model().attribute("action", "/recipes/1/edit"))
        .andExpect(model().attribute("redirect", "/recipes/1"));
  }

  @Test
  public void searchWithOnlyDescriptionWorks() throws Exception {
    when(recipeService.findByDescriptionContaining("testing")).thenReturn(testRecipes());
    when(recipeService.findByCategory("category"))
        .thenReturn(Collections.singletonList(testRecipe()));

    mockMvc.perform(get("/search?searchQuery=testing&category=&method=description"))

        .andExpect(model().attribute("allRecipes", hasSize(2)));
  }

  @Test
  public void searchWithDescriptionAndCategoryWorks() throws Exception {
    when(recipeService.findByDescriptionContaining("testing")).thenReturn(testRecipes());
    when(recipeService.findByCategory("category"))
        .thenReturn(Collections.singletonList(testRecipe()));

    mockMvc.perform(get("/search?searchQuery=testing&category=category&method=description"))

        .andExpect(model().attribute("allRecipes", hasSize(1)));
  }

  @Test
  public void newRecipePageCreatesNewRecipe() throws Exception {
    mockMvc.perform(get("/recipes/add"))

        .andExpect(view().name("edit"))
        .andExpect(model().attribute("recipe", hasProperty("id", nullValue())));
  }

  @Test
  public void addingNewRecipeSavesCorrectly() throws Exception {
    List<Recipe> recipes = new ArrayList<>(testRecipes());

    doAnswer(answer -> null).when(ingredientService).save(any(ingredient.getClass()));
    doAnswer(answer -> {
      recipes.add(new Recipe());
      return true;
    }).when(recipeService).save(org.mockito.Matchers.any(Recipe.class));
    doAnswer(answer -> null).when(users).save(org.mockito.Matchers.any(User.class));

    mockMvc.perform(post("/recipes/add").param("category.name", "category"));

    assertThat(recipes, hasSize(2));
  }

  private Recipe testRecipe() {
    Recipe
        recipe =
        new Recipe("test1", "description", extractBytes("bloodyMary"), Category.BEVERAGE.getName(),
            1, 2);
    recipe.setId(1L);
    return recipe;
  }

  private List<Recipe> testRecipes() {
    Recipe
        recipe =
        new Recipe("test2", "description", extractBytes("bloodyMary"), Category.BEVERAGE.getName(),
            1, 2);
    return Arrays.asList(recipe, testRecipe());
  }

  private User user = new User("user", "Tester", "password", new String[]{"ROLE_ADMIN"});

  private Ingredient ingredient = new Ingredient("ingredient", "condition", 7, "measurement");

  private byte[] extractBytes(String imageName) {
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
