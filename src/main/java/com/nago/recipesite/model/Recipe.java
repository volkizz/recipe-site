package com.nago.recipesite.model;


import com.nago.recipesite.core.BaseEntity;
import com.nago.recipesite.enums.Category;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

@Entity
public class Recipe extends BaseEntity {
  private String name;
  private Category category;
  //private byte[] image;

  @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
  private List<Ingredient> ingredients;

  @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
  private List<Instruction> instructions;

  @ManyToMany
  private List<User> administrators;

  private int preparationTime;
  private int cookTime;

  protected Recipe(){
    super();
    ingredients = new ArrayList<>();
    instructions = new ArrayList<>();
    administrators = new ArrayList<>();
  }

  public Recipe(String name, Category category, int preparationTime, int cookTime) {
    this();
    this.name = name;
    this.category = category;
   // this.image = image;
    this.preparationTime = preparationTime;
    this.cookTime = cookTime;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  /*public byte[] getImage() {
    return image;
  }

  public void setImage(byte[] image) {
    this.image = image;
  }*/

  public List<Ingredient> getIngredients() {
    return ingredients;
  }

  public void addIngredient(Ingredient ingredient)
  {
    ingredient.setRecipe(this);
    ingredients.add(ingredient);
  }

  public List<Instruction> getInstructions() {
    return instructions;
  }

  public void addInstruction(Instruction instruction) {
    instruction.setRecipe(this);
    instructions.add(instruction);
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

  public List<User> getAdministrators() {
    return administrators;
  }

  public void addAdministrator(User administrator) {
    administrators.add(administrator);
  }
}
