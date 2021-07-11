package sun.springframework.recipemongodbapp.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import sun.springframework.recipemongodbapp.domain.Recipe;

public interface RecipeRepository extends MongoRepository<Recipe,String> {
}
