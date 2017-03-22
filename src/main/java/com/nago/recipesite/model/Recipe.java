package com.nago.recipesite.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nago.recipesite.core.BaseEntity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
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
}