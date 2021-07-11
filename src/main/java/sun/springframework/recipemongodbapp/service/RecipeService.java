package sun.springframework.recipemongodbapp.service;


import sun.springframework.recipemongodbapp.commands.RecipeCommand;
import sun.springframework.recipemongodbapp.domain.Recipe;

import java.util.List;
import java.util.Set;

public interface RecipeService {

    List<Recipe> getRecipes();
    Recipe findById(String id);
    RecipeCommand saveRecipeCommand(RecipeCommand recipeCommand);
    RecipeCommand findCommandById(String id);
    void deleteById(String id);

}
