package sun.springframework.recipemongodbapp.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import sun.springframework.recipemongodbapp.domain.UnitOfMeasure;

import java.util.Optional;

public interface UnitOfMeasureRepository extends MongoRepository<UnitOfMeasure, String> {

    Optional<UnitOfMeasure> findByDescription(String description);
}
