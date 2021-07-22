package sun.springframework.recipemongodbapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import sun.springframework.recipemongodbapp.domain.Recipe;
import sun.springframework.recipemongodbapp.domain.UnitOfMeasure;
import sun.springframework.recipemongodbapp.service.RecipeService;
import sun.springframework.recipemongodbapp.service.UnitOfMeasureService;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
public class WebConfig {

    @Bean
    public RouterFunction<?> routes(RecipeService recipeService){
        return RouterFunctions.route(GET("/api/recipes"),
                serverRequest-> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(recipeService.getRecipes(), Recipe.class));
    }

    @Bean
    RouterFunction<?> routesUom(UnitOfMeasureService unitOfMeasureService){
        return RouterFunctions.route(GET("/api/uoms"),
                serverRequest->ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(unitOfMeasureService.listAllUoms(), UnitOfMeasure.class));
    }
}
