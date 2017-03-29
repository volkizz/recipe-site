package com.nago.recipesite.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nago.recipesite.dao.RecipeRepository;
import com.nago.recipesite.enums.Category;
import com.nago.recipesite.model.Recipe;
import com.nago.recipesite.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
public class RecipeServiceImplTest {
    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UserService users;

    @InjectMocks
    private RecipeService recipeService = new RecipeServiceImpl();

    @Test
    public void adminSaveAddsRecipe() throws Exception {
        List<Recipe> recipes = new ArrayList<>(testRecipes());
        doAnswer(answer -> {
            recipes.add(new Recipe());
            return true;
        }).when(recipeRepository).save(any(Recipe.class));

        recipeService.save(new Recipe(), admin);

        assertThat("recipes has added a recipe", recipes.size() == 3);
        verify(recipeRepository).save(any(Recipe.class));
    }

    @Test
    public void nonAdminDoesNotSaveRecipe() throws Exception {
        List<Recipe> recipes = new ArrayList<>(testRecipes());

        Boolean saved = recipeService.save(new Recipe(), nonAdmin);

        assertFalse(saved);
        assertThat("recipes has not added a recipe", recipes.size() == 2);
    }

    @Test
    public void findAllReturnsAll() throws Exception {
        when(recipeRepository.findAll()).thenReturn(testRecipes());

        List<Recipe> recipes = recipeService.findAll();

        assertThat("recipes have been retrieved", recipes.size() == 2);
        verify(recipeRepository).findAll();
    }

    @Test
    public void findOneReturnsOne() throws Exception {
        when(recipeRepository.findOne(1L)).thenReturn(testRecipe());

        Recipe recipe = recipeService.findOne(1L);

        assertThat("recipe has been retrieved", recipe.equals(testRecipe()));
        verify(recipeRepository).findOne(1L);
    }

    @Test
    public void adminCanDelete() throws Exception {
        List<Recipe> recipes = new ArrayList<>(testRecipes());
        doAnswer(answer -> {
            recipes.remove(0);
            return true;
        }).when(recipeRepository).delete(any(Long.class));
        when(recipeRepository.findOne(1L)).thenReturn(testRecipe());
        doAnswer(answer -> null).when(users).save(any(User.class));

        recipeService.delete(1L, admin);

        assertThat("recipe has been deleted", recipes.size() == 1);
        verify(recipeRepository).delete(any(Long.class));
    }

    @Test
    public void nonAdminCannotDelete() throws Exception {
        doAnswer(answer -> {
            return true;
        }).when(recipeRepository).delete(any(Long.class));
        when(recipeRepository.findOne(1L)).thenReturn(testRecipe());
        doAnswer(answer -> null).when(users).save(any(User.class));

        Boolean deleted = recipeService.delete(1L, nonAdmin);

        assertFalse(deleted);
    }

    @Test
    public void findByDescriptionContaining() throws Exception {
        when(recipeRepository.findByDescriptionContaining("testing")).thenReturn(testRecipes());

        List<Recipe> recipes = recipeService.findByDescriptionContaining("testing");

        assertThat("recipes have been retrieved", recipes.size() == 2);
    }

    @Test
    public void findByCategoryName() throws Exception {
        when(recipeRepository.findByCategory("category")).thenReturn(Collections.singletonList(testRecipe()));

        List<Recipe> recipes = recipeService.findByCategoryName("category");

        assertThat("One recipe retrieved", recipes.size() == 1);
    }

    private Recipe testRecipe() {
        Recipe recipe = new Recipe(
            "test1",
            "description",
            extractBytes("bloodyMary"),
            Category.BEVERAGE.getName(),
            1,
            2);
        recipe.setCreatedBy(admin);
        recipe.setId(1L);
        return recipe;
    }

    private List<Recipe> testRecipes() {
        Recipe recipe = new Recipe(
            "test2",
            "description",
            extractBytes("bloodyMary"),
            Category.BEVERAGE.getName(),
            1,
            2);
        return Arrays.asList(recipe, testRecipe());
    }

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

    private User admin = new User("admin", "Tester1", "password", new String[] {"ROLE_ADMIN"});
    private User nonAdmin = new User("admin", "Tester2", "password", new String[] {"ROLE_USER"});
}
