package com.nago.recipesite.model;

import com.nago.recipesite.core.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Instruction extends BaseEntity {
  private String name;
  private String description;

  @ManyToOne
  private Recipe recipe;

  protected Instruction(){
    super();
  }

  public Instruction(String name, String description) {
    this();
    this.name = name;
    this.description = description;
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

  public Recipe getRecipe() {
    return recipe;
  }

  public void setRecipe(Recipe recipe) {
    this.recipe = recipe;
  }
}
