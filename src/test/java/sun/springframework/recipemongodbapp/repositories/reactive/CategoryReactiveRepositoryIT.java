package sun.springframework.recipemongodbapp.repositories.reactive;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import sun.springframework.recipemongodbapp.domain.Category;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@DataMongoTest
class CategoryReactiveRepositoryIT {

    @Autowired
    CategoryReactiveRepository categoryReactiveRepository;

    @BeforeEach
    public void setUp() throws Exception {
        categoryReactiveRepository.deleteAll().block();
    }

    @Test
    void testSave(){
        Category cat1 = new Category();
        cat1.setDescription("Foo");
        categoryReactiveRepository.save(cat1).block();

        Category cat2 = new Category();
        cat2.setDescription("Soo");
        categoryReactiveRepository.save(cat2).block();

        Long count = categoryReactiveRepository.count().block();

        assertEquals(2,count);
    }

    @Test
    void testFindByDescription(){
        Category cat1 = new Category();
        cat1.setDescription("Foo");
        categoryReactiveRepository.save(cat1).block();

        Category cat2 = new Category();
        cat2.setDescription("Soo");
        categoryReactiveRepository.save(cat2).block();

        Category soo = categoryReactiveRepository.findByDescription("Soo").block();

        assertNotNull(soo.getId());
    }
}