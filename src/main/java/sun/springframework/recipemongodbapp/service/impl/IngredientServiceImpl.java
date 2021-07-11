package sun.springframework.recipemongodbapp.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import sun.springframework.recipemongodbapp.commands.IngredientCommand;
import sun.springframework.recipemongodbapp.converters.IngredientCommandToIngredient;
import sun.springframework.recipemongodbapp.converters.IngredientToIngredientCommand;
import sun.springframework.recipemongodbapp.domain.Ingredient;
import sun.springframework.recipemongodbapp.domain.Recipe;
import sun.springframework.recipemongodbapp.repositories.RecipeRepository;
import sun.springframework.recipemongodbapp.repositories.reactive.RecipeReactiveRepository;
import sun.springframework.recipemongodbapp.repositories.reactive.UnitOfMeasureReactiveRepository;
import sun.springframework.recipemongodbapp.service.IngredientService;


import java.util.Optional;

@Slf4j
@Service
public class IngredientServiceImpl implements IngredientService {

    private final IngredientToIngredientCommand ingredientToIngredientCommand;
    private final IngredientCommandToIngredient ingredientCommandToIngredient;
    private final RecipeReactiveRepository recipeReactiveRepository;
    private final UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;
    private final RecipeRepository recipeRepository;

    public IngredientServiceImpl(IngredientToIngredientCommand ingredientToIngredientCommand,
                                 IngredientCommandToIngredient ingredientCommandToIngredient,
                                 RecipeReactiveRepository recipeReactiveRepository, UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository,
                                 RecipeRepository recipeRepository) {
        this.ingredientToIngredientCommand = ingredientToIngredientCommand;
        this.ingredientCommandToIngredient = ingredientCommandToIngredient;
        this.recipeReactiveRepository = recipeReactiveRepository;
        this.unitOfMeasureReactiveRepository = unitOfMeasureReactiveRepository;
        this.recipeRepository = recipeRepository;
    }

    @Override
    public Mono<IngredientCommand> findByRecipeIdAndIngredientId(String recipeId, String ingredientId) {
        log.info("findByRecipeIdAndIngredientId RecipeId:"+recipeId);
        return recipeReactiveRepository.findById(recipeId).flatMapIterable(Recipe::getIngredients)
                .filter(ingredient -> ingredient.getId().equalsIgnoreCase(ingredientId))
                .single()
                .map(ingredient ->{
                    IngredientCommand command = ingredientToIngredientCommand.convert(ingredient);
                    command.setRecipeId(recipeId);
                    return command;
                });


       /* return recipeReactiveRepository.findById(recipeId)
                .map(recipe->recipe.getIngredients().stream().filter(ingredient -> ingredient.getId().equals(ingredientId)).findFirst())
                .filter(Optional::isPresent).map(ingredient ->{
                    IngredientCommand command = ingredientToIngredientCommand.convert(ingredient.get());
                    command.setRecipeId(recipeId);
                    return command;
        });*/

        /*Optional<Recipe> recipeOptional = recipeReactiveRepository.findById(recipeId);
        if(!recipeOptional.isPresent())
            throw new RuntimeException("recipe id not found :"+recipeId);
        Recipe recipe = recipeOptional.get();
        Optional<IngredientCommand> ingredientCommandOptional = recipe.getIngredients().stream()
                .filter(ingredient -> ingredient.getId().equals(ingredientId))
                .map(ingredient -> ingredientToIngredientCommand.convert(ingredient))
                .findFirst();
        if(!ingredientCommandOptional.isPresent())
            throw new RuntimeException("ingredient id not found :"+ingredientId);

        IngredientCommand ingredientCommand = ingredientCommandOptional.get();
        ingredientCommand.setRecipeId(recipeId);

        return ingredientCommand;*/
    }

    @Override
    @Transactional
    public Mono<IngredientCommand> saveIngredientCommand(IngredientCommand command) {
        Optional<Recipe> recipeOptional = recipeRepository.findById(command.getRecipeId());

        if(!recipeOptional.isPresent()){
            //todo toss error if not found!
            log.error("Recipe not found for id: " + command.getRecipeId());
            return Mono.just(new IngredientCommand());
        } else {
            Recipe recipe = recipeOptional.get();

            Optional<Ingredient> ingredientOptional = recipe
                    .getIngredients()
                    .stream()
                    .filter(ingredient -> ingredient.getId().equals(command.getId()))
                    .findFirst();

            if(ingredientOptional.isPresent()){
                Ingredient ingredientFound = ingredientOptional.get();
                ingredientFound.setDescription(command.getDescription());
                ingredientFound.setAmount(command.getAmount());
                ingredientFound.setUom(unitOfMeasureReactiveRepository
                        .findById(command.getUnitOfMeasure().getId()).block());
                       // .orElseThrow(() -> new RuntimeException("UOM NOT FOUND"))); //todo address this
                if(ingredientFound.getUom()==null){
                    new RuntimeException("UOM not found");
                }
            } else {
                //add new Ingredient
                Ingredient ingredient = ingredientCommandToIngredient.convert(command);
                //ingredient.setRecipe(recipe);
                recipe.addIngredient(ingredient);
            }

            Recipe savedRecipe = recipeReactiveRepository.save(recipe).block();

            Optional<Ingredient> savedRecipeIngredientOptional = savedRecipe.getIngredients().stream()
                    .filter(ingredient -> ingredient.getId().equals(command.getId())).findFirst();

            //check by description
            if(!savedRecipeIngredientOptional.isPresent()){
                savedRecipeIngredientOptional = savedRecipe.getIngredients().stream()
                        .filter(ingredient -> ingredient.getDescription().equals(command.getDescription()))
                        .filter(ingredient -> ingredient.getAmount().equals(command.getAmount()))
                        .filter(ingredient -> ingredient.getUom().getId().equals(command.getUnitOfMeasure().getId())).findFirst();
            }

            IngredientCommand ingredientCommandSaved = ingredientToIngredientCommand.convert(savedRecipeIngredientOptional.get());
            ingredientCommandSaved.setRecipeId(recipe.getId());

            //to do check for fail
            return Mono.just(ingredientCommandSaved);
        }

    }

    @Override
    public Mono<Void> deleteById(String recipeId, String ingredientId) {
        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);
        if(recipeOptional.isPresent()){
            log.debug("recipe found");
            Recipe recipe = recipeOptional.get();
            Optional<Ingredient> ingredientOptional = recipe.getIngredients().stream()
                    .filter(ingredient -> ingredient.getId().equals(ingredientId)).findFirst();
            if(ingredientOptional.isPresent()){
                log.debug("ingredient to delete found");
                Ingredient ingredient = ingredientOptional.get();
                //ingredient.setRecipe(null);
                recipe.getIngredients().remove(ingredient);
                recipeReactiveRepository.save(recipe);
            }
        }else {
            log.debug("recipe id not found id:"+recipeId);
        }
        return Mono.empty();
    }
}
