package com.nago.recipesite.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import com.nago.recipesite.dao.IngredientRepository;
import com.nago.recipesite.model.Ingredient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;


@RunWith(MockitoJUnitRunner.class)
public class IngredientServiceImplTest {
    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private IngredientService ingredientService = new IngredientServiceImpl();

    @Test
    public void saveAddsIngredient() throws Exception {
        List<Ingredient> ingredients = new ArrayList<>();
        when(ingredientRepository.save(any(Ingredient.class))).then(answer -> {
            ingredients.add(new Ingredient());
            return true;
        });

        ingredientService.save(new Ingredient());

        assertThat("ingredient was added", ingredients.size() == 1);
    }
}
