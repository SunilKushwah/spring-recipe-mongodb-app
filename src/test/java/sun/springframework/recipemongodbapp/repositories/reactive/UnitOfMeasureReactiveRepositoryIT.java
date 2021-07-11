package sun.springframework.recipemongodbapp.repositories.reactive;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import sun.springframework.recipemongodbapp.domain.UnitOfMeasure;
import sun.springframework.recipemongodbapp.repositories.UnitOfMeasureRepository;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@DataMongoTest
class UnitOfMeasureReactiveRepositoryIT {

    private final String TEASPOON = "teaspoon";
    private final String EACH = "each";

    @Autowired
    private UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;

    @BeforeEach
    void setUp() {
        unitOfMeasureReactiveRepository.deleteAll().block();
    }

    @Test
    void testSave(){

        UnitOfMeasure uom1 =  new UnitOfMeasure();
        uom1.setDescription(TEASPOON);
        unitOfMeasureReactiveRepository.save(uom1).block();

        UnitOfMeasure uom2  =  new UnitOfMeasure();
        uom2.setDescription(EACH);
        unitOfMeasureReactiveRepository.save(uom2).block();

        Long count = unitOfMeasureReactiveRepository.count().block();

        assertEquals(2, count);
    }

    @Test
    void testFindByDescription(){
        UnitOfMeasure uom =  new UnitOfMeasure();

        uom.setDescription(TEASPOON);
        unitOfMeasureReactiveRepository.save(uom).block();

        UnitOfMeasure unitOfMeasure = unitOfMeasureReactiveRepository.findByDescription(TEASPOON).block();

        assertNotNull(unitOfMeasure.getId());
    }
}