package sun.springframework.recipemongodbapp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import sun.springframework.recipemongodbapp.commands.RecipeCommand;
import sun.springframework.recipemongodbapp.converters.RecipeCommandToRecipe;
import sun.springframework.recipemongodbapp.converters.RecipeToRecipeCommand;
import sun.springframework.recipemongodbapp.domain.Recipe;
import sun.springframework.recipemongodbapp.repositories.RecipeRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
class RecipeServiceIT {

    public static final String DESCRIPTION = "new description";
    @Autowired
    RecipeService recipeService;
    @Autowired
    RecipeRepository recipeRepository;
    @Autowired
    RecipeCommandToRecipe recipeCommandToRecipe;
    @Autowired
    RecipeToRecipeCommand recipeToRecipeCommand;

    @Test
    void testOfSaveDescription() {
        //given
        Iterable<Recipe> all = recipeRepository.findAll();
        Recipe next = all.iterator().next();
        RecipeCommand recipeCommand = recipeToRecipeCommand.convert(next);

        //when
        recipeCommand.setDescription(DESCRIPTION);
        RecipeCommand savedRecipeCommand = recipeService.saveRecipeCommand(recipeCommand);

        //then
        assertNotNull(savedRecipeCommand);
        assertEquals(DESCRIPTION,savedRecipeCommand.getDescription());
        assertEquals(next.getId(),savedRecipeCommand.getId());
        assertEquals(next.getCategories().size(),savedRecipeCommand.getCategories().size());
        assertEquals(next.getIngredients().size(),savedRecipeCommand.getIngredients().size());

    }
}