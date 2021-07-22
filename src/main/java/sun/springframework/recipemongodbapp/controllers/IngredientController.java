package sun.springframework.recipemongodbapp.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Flux;
import sun.springframework.recipemongodbapp.commands.IngredientCommand;
import sun.springframework.recipemongodbapp.commands.RecipeCommand;
import sun.springframework.recipemongodbapp.commands.UnitOfMeasureCommand;
import sun.springframework.recipemongodbapp.domain.UnitOfMeasure;
import sun.springframework.recipemongodbapp.service.IngredientService;
import sun.springframework.recipemongodbapp.service.RecipeService;
import sun.springframework.recipemongodbapp.service.UnitOfMeasureService;


@Slf4j
@Controller
public class IngredientController {

    private RecipeService recipeService;
    private IngredientService ingredientService;
    private UnitOfMeasureService unitOfMeasureService;


    public IngredientController(RecipeService recipeService, IngredientService ingredientService,
                                UnitOfMeasureService unitOfMeasureService) {
        this.recipeService = recipeService;
        this.ingredientService = ingredientService;
        this.unitOfMeasureService = unitOfMeasureService;
    }

    @GetMapping("/recipe/{id}/ingredients")
    public String listIngredients(@PathVariable String id, Model model){
        log.info("recipeId:"+id);
        model.addAttribute("recipe", recipeService.findCommandById(id));
        return "recipe/ingredient/list";
    }

    @GetMapping("/recipe/{recipeId}/ingredient/{id}/show")
    public String showIngredient(@PathVariable String recipeId, @PathVariable String id,Model model){
        log.info("recipeId:"+recipeId+" ingredientId:"+id);
        model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(recipeId,id));
        return "recipe/ingredient/show";
    }

    @GetMapping("/recipe/{recipeId}/ingredient/{id}/update")
    public String updateIngredient(@PathVariable String recipeId, @PathVariable String id,Model model){
        log.info("recipeId:"+recipeId+" ingredientId:"+id);
        model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(recipeId,id));
        return "recipe/ingredient/ingredientform";
    }

    @PostMapping("/recipe/{recipeId}/ingredient")
    public String saveOrUpdateIngredient(@ModelAttribute IngredientCommand ingredientCommand){
        IngredientCommand  savedIngredientCommand = ingredientService.saveIngredientCommand(ingredientCommand).block();
        log.debug("recipe id:+"+ savedIngredientCommand.getRecipeId());
        log.debug("ingredient id:+"+ savedIngredientCommand.getId());
        return "redirect:/recipe/"+savedIngredientCommand.getRecipeId()+"/ingredient/"+savedIngredientCommand.getId()+"/show";
    }

    @GetMapping("/recipe/{recipeId}/ingredient/new")
    public String saveNewIngredient(@PathVariable String recipeId, Model model){
        //make sure we have a good id value.
        RecipeCommand recipeCommand = recipeService.findCommandById(recipeId).block();
        //todo raise exception if null
        IngredientCommand ingredientCommand = new IngredientCommand();
        ingredientCommand.setRecipeId(recipeId);
        model.addAttribute("ingredient",ingredientCommand);
        ingredientCommand.setUnitOfMeasure(new UnitOfMeasureCommand());
        return "recipe/ingredient/ingredientform";
    }

    @GetMapping("recipe/{recipeId}/ingredient/{id}/delete")
    public String deleteIngredient(@PathVariable String recipeId, @PathVariable String id){
        log.debug("deleting ingredient id:"+id);
        ingredientService.deleteById(recipeId,id).block();
        return "redirect:/recipe/"+recipeId+"/ingredients";
    }

    @ModelAttribute("uomList")
    public Flux<UnitOfMeasureCommand> populateUOM(){
        return unitOfMeasureService.listAllUoms();
    }
}
