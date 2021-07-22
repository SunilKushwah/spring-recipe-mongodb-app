package sun.springframework.recipemongodbapp.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import reactor.core.publisher.Flux;
import sun.springframework.recipemongodbapp.config.WebConfig;
import sun.springframework.recipemongodbapp.domain.Recipe;
import sun.springframework.recipemongodbapp.service.RecipeService;

import static org.mockito.Mockito.when;

public class RouterFunctionTest {

    private WebTestClient webTestClient;
    @Mock
    private RecipeService recipeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        WebConfig webConfig = new WebConfig();
        RouterFunction<?> routerFunction = webConfig.routes(recipeService);
        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
    }

    @Test
    void testGetRecipes() {
        when(recipeService.getRecipes()).thenReturn(Flux.empty());
        webTestClient.get().uri("/api/recipes")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void testGetRecipesWithData() {
        when(recipeService.getRecipes()).thenReturn(Flux.just(new Recipe(),new Recipe()));
        webTestClient.get().uri("/api/recipes")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Recipe.class);
    }
}
