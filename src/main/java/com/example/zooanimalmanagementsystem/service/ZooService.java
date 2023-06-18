package com.example.zooanimalmanagementsystem.service;

import com.example.zooanimalmanagementsystem.repository.AnimalRepository;
import com.example.zooanimalmanagementsystem.repository.EnclosureRepository;
import com.example.zooanimalmanagementsystem.repository.model.Animal;
import com.example.zooanimalmanagementsystem.repository.model.Enclosure;
import com.example.zooanimalmanagementsystem.service.exception.DataAlreadyStoredException;
import com.example.zooanimalmanagementsystem.service.exception.EnclosuresDataNotFound;
import com.example.zooanimalmanagementsystem.service.exception.NoAvailableEnclosureException;
import com.example.zooanimalmanagementsystem.service.model.AnimalsList;
import com.example.zooanimalmanagementsystem.service.model.EnclosuresList;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ZooService {

    private final EnclosureRepository enclosureRepository;
    private final AnimalRepository animalRepository;
    private final InputReadingService inputReadingService;

    public ZooService(
            EnclosureRepository enclosureRepository,
            AnimalRepository animalRepository,
            InputReadingService inputReadingService
    ) {
        this.enclosureRepository = enclosureRepository;
        this.animalRepository = animalRepository;
        this.inputReadingService = inputReadingService;
    }

    public List<Enclosure> saveEnclosures(MultipartFile file) {
        evaluateIfEnclosuresAreNotStoredAlready();
        EnclosuresList enclosuresList = inputReadingService.retrieveEnclosuresData(file);
        return enclosureRepository.saveAll(enclosuresList.toEntity());
    }

    public List<Animal> saveAnimals(MultipartFile file) {
        evaluateIfEnclosuresAreStored();
        evaluateIfAnimalsAreNotStoredAlready();
        AnimalsList animalsList = inputReadingService.retrieveAnimalsData(file);
        List<Animal> givenAnimals = animalsList.toEntity();
        for (Animal animal : givenAnimals) {
            animal.setEnclosureId(assignEnclosure(animal.getFood(), animal.getAmount()));
        }

        return animalRepository.saveAll(givenAnimals);
    }

    private void evaluateIfEnclosuresAreNotStoredAlready() {
        if (enclosureRepository.count() > 0) {
            throw new DataAlreadyStoredException("File reading cancelled. Given enclosures are already stored in database.");
        }
    }

    private void evaluateIfEnclosuresAreStored() {
        if (enclosureRepository.count() == 0) {
            throw new EnclosuresDataNotFound("File reading cancelled. Please store enclosures before proceeding with animals.");
        }
    }

    private void evaluateIfAnimalsAreNotStoredAlready() {
        if (animalRepository.count() > 0) {
            throw new DataAlreadyStoredException("File reading cancelled. Given animals are already stored in database.");
        }
    }

    private UUID assignEnclosure(String food, int amount) {
        List<Enclosure> enclosures = enclosureRepository.findAll();

        return food.equals("Carnivore")
                ? findSuitableEnclosureForCarnivore(food, amount, enclosures)
                : findSuitableEnclosureForHerbivore(food, amount, enclosures);
    }

    private UUID findSuitableEnclosureForCarnivore(String food, int amount, List<Enclosure> enclosures) {
        boolean foundSuitableEnclosure = false;
        UUID suitableEnclosureId = null;
        for (Enclosure enclosure : enclosures) {
            if (Collections.frequency(enclosure.getAnimals(), "Carnivore") <= 1 && enclosure.getFreeSpace() - amount >= 0) {
                suitableEnclosureId = enclosure.getId();
                foundSuitableEnclosure = true;
                break;
            }
        }

        if (foundSuitableEnclosure) {
            updateEnclosureWithAnimalData(suitableEnclosureId, amount, food);
        } else {
            throw new NoAvailableEnclosureException("There is no enough space in enclosures. Animal can`t be placed.");
        }

        return suitableEnclosureId;
    }

    private UUID findSuitableEnclosureForHerbivore(String food, int amount, List<Enclosure> enclosures) {
        boolean foundSuitableEnclosure = false;
        UUID suitableEnclosureId = null;
        for (Enclosure enclosure : enclosures) {
            if (enclosure.getFreeSpace() - amount >= 0) {
                suitableEnclosureId = enclosure.getId();
                foundSuitableEnclosure = true;
                break;
            }
        }

        if (foundSuitableEnclosure) {
            updateEnclosureWithAnimalData(suitableEnclosureId, amount, food);
        } else {
            throw new NoAvailableEnclosureException("There is no enough space in enclosures. Animal can`t be placed.");
        }

        return suitableEnclosureId;
    }

    private void updateEnclosureWithAnimalData(UUID id, int amount, String food) {
        Optional<Enclosure> optionalEnclosure = enclosureRepository.findById(id);
        if (optionalEnclosure.isPresent()) {
            Enclosure enclosure = optionalEnclosure.get();
            enclosure.setId(id);
            enclosure.setFreeSpace(enclosure.getFreeSpace() - amount);
            enclosure.setAnimals(food);
            enclosureRepository.save(enclosure);
        }
    }
}
