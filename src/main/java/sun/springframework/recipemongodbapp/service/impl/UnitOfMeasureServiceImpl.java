package sun.springframework.recipemongodbapp.service.impl;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import sun.springframework.recipemongodbapp.commands.UnitOfMeasureCommand;
import sun.springframework.recipemongodbapp.converters.UnitOfMeasureToUnitOfMeasureCommand;
import sun.springframework.recipemongodbapp.domain.UnitOfMeasure;
import sun.springframework.recipemongodbapp.repositories.UnitOfMeasureRepository;
import sun.springframework.recipemongodbapp.repositories.reactive.UnitOfMeasureReactiveRepository;
import sun.springframework.recipemongodbapp.service.UnitOfMeasureService;


import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UnitOfMeasureServiceImpl implements UnitOfMeasureService {

    private UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;
    private UnitOfMeasureToUnitOfMeasureCommand unitOfMeasureToUnitOfMeasureCommand;

    public UnitOfMeasureServiceImpl(UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository,
                                    UnitOfMeasureToUnitOfMeasureCommand unitOfMeasureToUnitOfMeasureCommand) {
        this.unitOfMeasureReactiveRepository = unitOfMeasureReactiveRepository;
        this.unitOfMeasureToUnitOfMeasureCommand = unitOfMeasureToUnitOfMeasureCommand;
    }

    @Override
    public Flux<UnitOfMeasureCommand> listAllUoms() {
        Flux<UnitOfMeasureCommand> all = unitOfMeasureReactiveRepository.findAll().map(unitOfMeasureToUnitOfMeasureCommand::convert);
        return all;
       /*return StreamSupport.stream(unitOfMeasureRepository.findAll().spliterator(), false)
               .map(unitOfMeasureToUnitOfMeasureCommand::convert).collect(Collectors.toSet());*/

    }
}
