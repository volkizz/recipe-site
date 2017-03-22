package com.nago.recipesite.enums;

public enum Measurement {
  teaspoon("teaspoon"),
  tablespoon("tablespoon"),
  cup("cup"),
  can_28_ounces("can 28 ounces"),
  can_16_ounces("can 16 ounces"),
  can_8_ounces("can 8 ounces"),
  fluid_ounces("fluid ounces"),
  jar_28_ounces("jar 28 ounces"),
  jar_16_ounces("jar 16 ounces"),
  jar_8_ounces("jar 8 ounces"),
  pound("pound"),
  ounce("ounce"),
  stalk("stalk"),
  clove("clove"),
  drop("drop");

  private String name;

  Measurement (String name){
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
