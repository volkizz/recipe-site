package com.nago.recipesite.model;

import com.nago.recipesite.core.BaseEntity;
import com.nago.recipesite.enums.Measurement;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Ingredient extends BaseEntity {
  private String name;
  private String condition;
  private double quantity;
  private Measurement measurement;

  @ManyToOne
  private Recipe recipe;

  protected Ingredient(){
    super();
  }

  public Ingredient(String name, String condition, double quantity, Measurement measurement) {
    this();
    this.name = name;
    this.condition = condition;
    this.quantity = quantity;
    this.measurement = measurement;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCondition() {
    return condition;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }

  public double getQuantity() {
    return quantity;
  }

  public void setQuantity(double quantity) {
    this.quantity = quantity;
  }

  public Measurement getMeasurement() {
    return measurement;
  }

  public void setMeasurement(Measurement measurement) {
    this.measurement = measurement;
  }

  public Recipe getRecipe() {
    return recipe;
  }

  public void setRecipe(Recipe recipe) {
    this.recipe = recipe;
  }
}

