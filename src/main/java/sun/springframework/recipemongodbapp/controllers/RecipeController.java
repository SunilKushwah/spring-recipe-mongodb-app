package sun.springframework.recipemongodbapp.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.exceptions.TemplateInputException;
import reactor.core.publisher.Mono;
import sun.springframework.recipemongodbapp.commands.RecipeCommand;
import sun.springframework.recipemongodbapp.exceptions.NotFoundException;
import sun.springframework.recipemongodbapp.service.RecipeService;

import javax.validation.Valid;

@Slf4j
@Controller
public class RecipeController {

    private RecipeService recipeService;

    private String RECIPE_RECIPEFORM_URL = "recipe/recipeform";

    private WebDataBinder webDataBinder;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder){
        this.webDataBinder = webDataBinder;
    }

    @GetMapping("/recipe/{id}/show")
    public String showById(@PathVariable String id, Model model){
        model.addAttribute("recipe", recipeService.findById(id));
        return "recipe/show";
    }

    @GetMapping("/recipe/new")
    public String newRecipe(Model model){
        model.addAttribute("recipe", new RecipeCommand());

        return RECIPE_RECIPEFORM_URL;
    }

    @PostMapping("recipe")
    public String saveOrUpdate(@Valid @ModelAttribute("recipe") RecipeCommand command){//binding result is result of validation
        webDataBinder.validate();
        BindingResult bindingResult = webDataBinder.getBindingResult();
        if(bindingResult.hasErrors()){
            bindingResult.getAllErrors().forEach(objectError -> {
                log.debug(objectError.toString());
            });
            return RECIPE_RECIPEFORM_URL;
        }
        RecipeCommand savedCommand = recipeService.saveRecipeCommand(command).block();
        return "redirect:/recipe/" + savedCommand.getId() + "/show";
    }

    @GetMapping("recipe/{id}/update")
    public String updateRecipe(@PathVariable String id, Model model){
        model.addAttribute("recipe", recipeService.findCommandById(id));
        return "recipe/recipeform";
    }
    @GetMapping("recipe/{id}/delete")
    public String deleteRecipe(@PathVariable String id){
        log.debug("Deleting Recipe Id:"+id);
        recipeService.deleteById(id);
        return "redirect:/";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NotFoundException.class, TemplateInputException.class})
    public String handleNotFound(Exception exception, Model model){
        log.error("Handling not found exception");
        log.error(exception.getMessage());
        model.addAttribute("exception", exception);
        return "404error";
    }


}
