package com.nago.recipesite.controller;


import static org.hamcrest.MatcherAssert.assertThat;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.nago.recipesite.dao.IngredientRepository;
import com.nago.recipesite.dao.RecipeRepository;
import com.nago.recipesite.dao.UserRepository;
import com.nago.recipesite.enums.Category;
import com.nago.recipesite.enums.Measurement;
import com.nago.recipesite.model.Ingredient;
import com.nago.recipesite.model.Recipe;
import com.nago.recipesite.model.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
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
    /*@Mock
    private Category categoryService;*/
   /* @Mock
    private UserHandler userHandler;*/
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
    public void backslashRedirectsToRecipesPage() throws Exception {
        mockMvc.perform(get("/"))

        .andExpect(redirectedUrl("/recipes"));
    }

    @Test
    public void recipesPageHasAllRecipesShown() throws Exception {
        when(recipeService.findAll()).thenReturn(testRecipes());
        when(categoryService.findAll()).thenReturn(testCategories());

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
    public void deletingRecipeIsPossible() throws Exception {
        List<Recipe> recipes = new ArrayList<>(testRecipes());
        when(recipeService.findOne(1L)).thenReturn(testRecipe());
        //This is set up this way because it's how the Controller calls the method, unfortunately ->
        when(recipeService.delete(testRecipe(), null)).then(answer -> {
            recipes.remove(testRecipe());
            return true;
        });

        mockMvc.perform(post("/recipes/1/delete"));

        assertThat(recipes.size(), is(1));
        verify(recipeService).delete(org.mockito.Matchers.any(Recipe.class), org.mockito.Matchers.any(User.class));
    }

    @Test
    public void deniedUserCannotDeleteRecipe() throws Exception {
        List<Recipe> recipes = new ArrayList<>(testRecipes());
        when(recipeService.findOne(1L)).thenReturn(testRecipe());
        //This is set up this way because it's how the Controller calls the method, unfortunately ->
        when(recipeService.delete(testRecipe(), null)).then(answer -> {
            return false;
        });

        mockMvc.perform(post("/recipes/1/delete"))

        .andExpect(flash().attribute("flash", hasProperty("status", equalTo(FAILURE))));
        assertThat(recipes.size(), is(2));
    }

    @Test
    public void editPageLoadsCorrectItems() throws Exception {
        when(recipeService.findOne(1L)).thenReturn(testRecipe());
        when(categoryService.findAll()).thenReturn(testCategories());

        mockMvc.perform(get("/recipes/1/edit"))

        .andExpect(model().attribute("recipe", testRecipe()))
        .andExpect(model().attribute("action", "/recipes/1/edit"))
        .andExpect(model().attribute("redirect", "/recipes/1"))
        .andExpect(model().attribute("categories", testCategories()));
    }

    @Test
    public void searchWithOnlyDescriptionWorks() throws Exception {
        when(recipeService.findByDescriptionContaining("testing")).thenReturn(testRecipes());
        when(recipeService.findByCategoryName("category")).thenReturn(Collections.singletonList(testRecipe()));
        when(categoryService.findAll()).thenReturn(testCategories());

        mockMvc.perform(get("/search?searchQuery=testing&category=&method=description"))

        .andExpect(model().attribute("allRecipes", hasSize(2)));
    }

    @Test
    public void searchWithDescriptionAndCategoryWorks() throws Exception {
        when(recipeService.findByDescriptionContaining("testing")).thenReturn(testRecipes());
        when(recipeService.findByCategoryName("category")).thenReturn(Collections.singletonList(testRecipe()));
        when(categoryService.findAll()).thenReturn(testCategories());

        mockMvc.perform(get("/search?searchQuery=testing&category=category&method=description"))

        .andExpect(model().attribute("allRecipes", hasSize(1)));
    }

    @Test
    public void searchByOnlyIngredientWorks() throws Exception {
        when(recipeService.findByIngredient("ingredient")).thenReturn(Collections.singletonList(1L));
        when(recipeService.findOne(1L)).thenReturn(testRecipe());
        when(recipeService.findByCategoryName("category")).thenReturn(Collections.singletonList(testRecipe()));
        when(categoryService.findAll()).thenReturn(testCategories());

        mockMvc.perform(get("/search?searchQuery=ingredient&category=&method=ingredient"))

        .andExpect(model().attribute("allRecipes", hasSize(1)));
    }

    @Test
    public void searchByIngredientAndCategoryWorks() throws Exception {
        when(recipeService.findByIngredient("ingredient")).thenReturn(Collections.singletonList(1L));
        when(recipeService.findOne(1L)).thenReturn(testRecipe());
        when(recipeService.findByCategoryName("notCategory")).thenReturn(new ArrayList<>());
        when(categoryService.findAll()).thenReturn(testCategories());

        mockMvc.perform(get("/search?searchQuery=ingredient&category=notCategory&method=ingredient"))

        .andExpect(model().attribute("allRecipes", hasSize(0)));
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
        when(categoryService.findByName("category")).thenReturn(testCategories().get(0));
        doAnswer(answer -> null).when(ingredientService).save(any());
        doAnswer(answer -> {
            recipes.add(new Recipe());
            return true;
        }).when(recipeService).save(org.mockito.Matchers.any(Recipe.class), org.mockito.Matchers.any(User.class));
        doAnswer(answer -> null).when(users).save(org.mockito.Matchers.any(User.class));

        mockMvc.perform(post("/recipes/add").param("category.name", "category"));

        assertThat(recipes, hasSize(3));
    }

    @Test
    public void editingOldRecipeSavesCorrectly() throws Exception {
        List<Recipe> recipes = new ArrayList<>(testRecipes());
        Recipe recipe = testRecipe();
        when(categoryService.findByName("category")).thenReturn(testCategories().get(0));
        doAnswer(answer -> null).when(ingredientService).save(any());
        doAnswer(answer -> {
            recipe.setName("newName");
            return true;
        }).when(recipeService).save(org.mockito.Matchers.any(Recipe.class), org.mockito.Matchers.any(User.class));
        doAnswer(answer -> null).when(users).save(org.mockito.Matchers.any(User.class));

        mockMvc.perform(post("/recipes/1/edit").param("category.name", "category"));

        assertThat(recipe, hasProperty("name", is("newName")));
    }

    private Recipe testRecipe() {
        Recipe recipe = new Recipe("test", "description", extractBytes("bloodyMary"), Category.BEVERAGE.toString(), 2, 1);
        recipe.setId(1L);
        return recipe;
    }

    private List<Recipe> testRecipes() {
        Recipe recipe = new Recipe("test2", "description", extractBytes("bloodyMary"), Category.BEVERAGE.toString(), 2, 1);
        return Arrays.asList(recipe, testRecipe());
    }

    private User user = new User("username", "Tester", "password", new String[] {"ROLE_ADMIN"});


    private Ingredient ingredient = new Ingredient("ingredient1", "condition1", 7, Measurement.cup.toString());

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
