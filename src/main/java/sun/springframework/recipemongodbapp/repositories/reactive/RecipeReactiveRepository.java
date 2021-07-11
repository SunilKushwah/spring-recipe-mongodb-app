package sun.springframework.recipemongodbapp.repositories.reactive;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import sun.springframework.recipemongodbapp.domain.Recipe;

public interface RecipeReactiveRepository extends ReactiveMongoRepository<Recipe,String> {
}
