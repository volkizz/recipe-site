package com.nago.recipesite.enums;

public enum Category {
  SOUP("Soup"),
  APPETIZER("Appetizer"),
  SALADS("Salads"),
  HEALTHY_FOOD("Healthy Food"),
  VEGETARIAN("Vegetarian"),
  BREAD("Bread"),
  BEVERAGE("Beverage"),
  DESSERT("Dessert"),
  MAIN_DISH("Main Dish"),
  SAUCE("Sauce"),
  SIDE_DISH("Side Dish");

  private String name;

  Category (String name){
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
