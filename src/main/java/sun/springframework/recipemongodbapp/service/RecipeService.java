package sun.springframework.recipemongodbapp.service;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sun.springframework.recipemongodbapp.commands.RecipeCommand;
import sun.springframework.recipemongodbapp.domain.Recipe;

public interface RecipeService {

    Flux<Recipe> getRecipes();
    Mono<Recipe> findById(String id);
    Mono<RecipeCommand> saveRecipeCommand(RecipeCommand recipeCommand);
    Mono<RecipeCommand> findCommandById(String id);
    Mono<Void> deleteById(String id);

}
