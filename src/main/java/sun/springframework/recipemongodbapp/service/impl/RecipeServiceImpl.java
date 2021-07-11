package sun.springframework.recipemongodbapp.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.springframework.recipemongodbapp.commands.RecipeCommand;
import sun.springframework.recipemongodbapp.converters.RecipeCommandToRecipe;
import sun.springframework.recipemongodbapp.converters.RecipeToRecipeCommand;
import sun.springframework.recipemongodbapp.domain.Recipe;
import sun.springframework.recipemongodbapp.exceptions.NotFoundException;
import sun.springframework.recipemongodbapp.repositories.RecipeRepository;
import sun.springframework.recipemongodbapp.service.RecipeService;

import java.lang.reflect.Array;
import java.util.*;

@Slf4j
@Service
public class RecipeServiceImpl implements RecipeService {

    private RecipeRepository recipeRepository;
    private RecipeCommandToRecipe recipeCommandToRecipe;
    private RecipeToRecipeCommand recipeToRecipeCommand;

    public RecipeServiceImpl(RecipeRepository recipeRepository, RecipeCommandToRecipe recipeCommandToRecipe, RecipeToRecipeCommand recipeToRecipeCommand) {
        this.recipeRepository = recipeRepository;
        this.recipeCommandToRecipe = recipeCommandToRecipe;
        this.recipeToRecipeCommand = recipeToRecipeCommand;
    }

    @Override
    public List<Recipe> getRecipes() {
        log.debug("I'm in  the service");
        List<Recipe> recipeSet = new ArrayList<>();
        Iterable<Recipe> all = recipeRepository.findAll();
        all.iterator().forEachRemaining(recipeSet::add);
        return recipeSet;
    }

    @Override
    public Recipe findById(String id) {
        Optional<Recipe> recipeOptional = recipeRepository.findById(id);
        if(!recipeOptional.isPresent()){
            //throw new RuntimeException("recipe not found");
            throw new NotFoundException("Recipe not found. For ID value:"+id.toString());
        }
        return recipeOptional.get();
    }

    @Override
    public RecipeCommand saveRecipeCommand(RecipeCommand recipeCommand) {
        Recipe recipe = recipeCommandToRecipe.convert(recipeCommand);
        Recipe savedRecipe = recipeRepository.save(recipe);
        log.debug("saved recipeId:"+savedRecipe.getId());
        return recipeToRecipeCommand.convert(savedRecipe);
    }

    @Override
    public RecipeCommand findCommandById(String id) {
        return recipeToRecipeCommand.convert(findById(id));
    }

    @Override
    public void deleteById(String id) {
        recipeRepository.deleteById(id);
    }

}
