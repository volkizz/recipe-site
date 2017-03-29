package com.nago.recipesite.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nago.recipesite.core.BaseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Recipe extends BaseEntity {
  private String name;
  private String description;

  @Lob
  private byte[] image;

  private String category;

  @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
  private List<Ingredient> ingredients;

  @ElementCollection
  private List<String> instructions;

  private int preparationTime;
  private int cookTime;

  @ManyToOne
  @JoinColumn(name = "created_by_id")
  @JsonIgnore
  private User createdBy;

  public Recipe(){
    super();
    ingredients = new ArrayList<>();
    instructions = new ArrayList<>();
  }

  public Recipe(String name, String description, byte[] image, String category, int preparationTime, int cookTime) {
    this();
    this.name = name;
    this.description = description;
    this.category = category;
    this.image = image;
    this.preparationTime = preparationTime;
    this.cookTime = cookTime;
    instructions = new ArrayList<>();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public byte[] getImage() {
    return image;
  }
  public void setImage(byte[] image) {
    this.image = image;
  }

  public List<Ingredient> getIngredients() {
    return ingredients;
  }

  public void addIngredient(Ingredient ingredient)
  {
    ingredient.setRecipe(this);
    ingredients.add(ingredient);
  }

  public List<String> getInstructions() {
    return instructions;
  }

  public void removeInstruction(String instruction) {
    instructions.remove(instruction);
  }

  public void addInstruction(int step, String instruction) {
    instructions.add(step, instruction);
  }

  public int getPreparationTime() {
    return preparationTime;
  }

  public void setPreparationTime(int preparationTime) {
    this.preparationTime = preparationTime;
  }

  public int getCookTime() {
    return cookTime;
  }

  public void setCookTime(int cookTime) {
    this.cookTime = cookTime;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(User createdBy) {
    if(createdBy != null) {
      createdBy.addOwnRecipe(this);
    }
    this.createdBy = createdBy;
  }

  public boolean isFavorited(User user) {
    return user.getFavoriteRecipes().contains(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Recipe recipe = (Recipe) o;

    if (preparationTime != recipe.preparationTime) {return false;}
    if (cookTime != recipe.cookTime) {
      return false;
    }
    if (name != null ? !name.equals(recipe.name) : recipe.name != null) {
      return false;
    }
    if (description != null ? !description.equals(recipe.description)
        : recipe.description != null) {
      return false;
    }
    if (!Arrays.equals(image, recipe.image)) {
      return false;
    }
    if (category != null ? !category.equals(recipe.category) : recipe.category != null) {
      return false;
    }
    if (ingredients != null ? !ingredients.equals(recipe.ingredients)
        : recipe.ingredients != null) {
      return false;
    }
    if (instructions != null ? !instructions.equals(recipe.instructions)
        : recipe.instructions != null) {
      return false;
    }
    return createdBy != null ? createdBy.equals(recipe.createdBy) : recipe.createdBy == null;
  }

  @Override
  public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (description != null ? description.hashCode() : 0);
    result = 31 * result + Arrays.hashCode(image);
    result = 31 * result + (category != null ? category.hashCode() : 0);
    result = 31 * result + (ingredients != null ? ingredients.hashCode() : 0);
    result = 31 * result + (instructions != null ? instructions.hashCode() : 0);
    result = 31 * result + preparationTime;
    result = 31 * result + cookTime;
    result = 31 * result + (createdBy != null ? createdBy.hashCode() : 0);
    return result;
  }
}