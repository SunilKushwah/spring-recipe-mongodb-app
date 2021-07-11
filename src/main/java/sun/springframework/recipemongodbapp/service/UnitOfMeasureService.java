package sun.springframework.recipemongodbapp.service;


import reactor.core.publisher.Flux;
import sun.springframework.recipemongodbapp.commands.UnitOfMeasureCommand;

import java.util.Set;

public interface UnitOfMeasureService {

    Flux<UnitOfMeasureCommand> listAllUoms();
}
