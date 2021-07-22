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

    public IngredientServiceImpl(IngredientToIngredientCommand ingredientToIngredientCommand,
                                 IngredientCommandToIngredient ingredientCommandToIngredient,
                                 RecipeReactiveRepository recipeReactiveRepository, UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository) {
        this.ingredientToIngredientCommand = ingredientToIngredientCommand;
        this.ingredientCommandToIngredient = ingredientCommandToIngredient;
        this.recipeReactiveRepository = recipeReactiveRepository;
        this.unitOfMeasureReactiveRepository = unitOfMeasureReactiveRepository;
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
    }

    @Override
    @Transactional
    public Mono<IngredientCommand> saveIngredientCommand(IngredientCommand command) {
        Recipe recipe = recipeReactiveRepository.findById(command.getRecipeId()).block();

        if(recipe==null){
            //todo toss error if not found!
            log.error("Recipe not found for id: " + command.getRecipeId());
            return Mono.just(new IngredientCommand());
        } else {
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
        Recipe recipe = recipeReactiveRepository.findById(recipeId).block();
        if(recipe!=null){
            log.debug("recipe found");
            Optional<Ingredient> ingredientOptional = recipe.getIngredients().stream()
                    .filter(ingredient -> ingredient.getId().equals(ingredientId)).findFirst();
            if(ingredientOptional.isPresent()){
                log.debug("ingredient to delete found");
                Ingredient ingredient = ingredientOptional.get();
                //ingredient.setRecipe(null);
                recipe.getIngredients().remove(ingredient);
                recipeReactiveRepository.save(recipe).block();
            }
        }else {
            log.debug("recipe id not found id:"+recipeId);
        }
        return Mono.empty();
    }

}
