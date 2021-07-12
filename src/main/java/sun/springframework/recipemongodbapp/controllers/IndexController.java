package sun.springframework.recipemongodbapp.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import sun.springframework.recipemongodbapp.domain.Recipe;
import sun.springframework.recipemongodbapp.service.RecipeService;

import java.util.List;

@Slf4j
@Controller
public class IndexController {

private final RecipeService recipeService;

    public IndexController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @RequestMapping({"","/","/index"})
    public String getIndexPage(Model model){
        log.debug("Getting index page.");
        List<Recipe> recipes = recipeService.getRecipes().collectList().block();
        model.addAttribute("recipes", recipes);
        return "index";
    }

}
