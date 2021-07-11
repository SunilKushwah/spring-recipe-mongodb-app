package sun.springframework.recipemongodbapp.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import sun.springframework.recipemongodbapp.domain.Category;

import java.util.Optional;

public interface CategoryRepository extends MongoRepository<Category, String> {

    Optional<Category> findByDescription(String description);
}
