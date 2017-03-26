$("#newIngredient").click(function() {
    var ingredientCount = $(".ingredient-row").length;
    var div = '<div class="ingredient-row" id="ingredientNumber' + ingredientCount + '">';

        div = div + '<div class="prefix-20 grid-20">';
        div = div + '<p><input id="ingredients' + ingredientCount + '.name" name="ingredients[';
        div = div + ingredientCount + '].name"></p></div>';

        div = div + '<div class="grid-20">';
        div = div + '<p><input id="ingredients' + ingredientCount + '.condition" name="ingredients[';
        div = div + ingredientCount + '].condition"></p></div>';

        div = div + '<div class="grid-15">';
        div = div + '<p><input class="quantity" style="width: 214%" id="ingredients' + ingredientCount;
        div = div + '.quantity" name="ingredients[' + ingredientCount + '].quantity"></p></div>';

        div = div + '<div class="grid-20"><p><input style="width: 80%" id="ingredients';
        div = div + ingredientCount + '.measurement" name="ingredients[';
        div = div + ingredientCount + '].measurement"></p></div></div>';

    $("#newIngredientButton").on("click", clone);
});
$("#signupButton").click(function(){
    var password1 = $("#firstPassword").val();
    var password2 = $("#secondPassword").val();
    if(password1 == password2) {
        document.getElementById("signupForm").submit();
    } else {
        alert("Passwords don't match! Try re-typing them.");
    }
});

