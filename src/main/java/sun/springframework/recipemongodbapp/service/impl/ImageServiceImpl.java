package sun.springframework.recipemongodbapp.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import sun.springframework.recipemongodbapp.domain.Recipe;
import sun.springframework.recipemongodbapp.repositories.reactive.RecipeReactiveRepository;
import sun.springframework.recipemongodbapp.service.ImageService;

import java.io.IOException;


@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    private RecipeReactiveRepository recipeReactiveRepository;

    public ImageServiceImpl(RecipeReactiveRepository recipeReactiveRepository) {
        this.recipeReactiveRepository = recipeReactiveRepository;
    }

    @Override
    public Mono<Void> saveImageFile(String recipeId, MultipartFile file) {
        Mono<Recipe> recipeMono = recipeReactiveRepository.findById(recipeId).map(recipe -> {
            Byte[] byteObject = new Byte[0];
            try {
                byteObject = new Byte[file.getBytes().length];
                int i=0;
                for (byte b : file.getBytes()) {
                    byteObject[i++] = b;
                }
                recipe.setImage(byteObject);
                return recipe;
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
        recipeReactiveRepository.save(recipeMono.block()).block();
        return Mono.empty();
    }
}

