package sun.springframework.recipemongodbapp.service;


import reactor.core.publisher.Mono;
import sun.springframework.recipemongodbapp.commands.IngredientCommand;

public interface IngredientService {
    Mono<IngredientCommand> findByRecipeIdAndIngredientId(String recipeId, String ingredientId);
    Mono<IngredientCommand> saveIngredientCommand(IngredientCommand ingredientCommand);
    Mono<Void> deleteById(String recipeId, String ingredientId);
}
