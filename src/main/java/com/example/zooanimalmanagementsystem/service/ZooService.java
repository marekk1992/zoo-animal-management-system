package com.example.zooanimalmanagementsystem.service;

import com.example.zooanimalmanagementsystem.repository.AnimalRepository;
import com.example.zooanimalmanagementsystem.repository.EnclosureRepository;
import com.example.zooanimalmanagementsystem.repository.model.Animal;
import com.example.zooanimalmanagementsystem.repository.model.Enclosure;
import com.example.zooanimalmanagementsystem.service.exception.AnimalNotFoundException;
import com.example.zooanimalmanagementsystem.service.exception.DataAlreadyStoredException;
import com.example.zooanimalmanagementsystem.service.exception.EnclosureNotFoundException;
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

    public List<Animal> findAllAnimals() {
        return animalRepository.findAll();
    }

    public Animal findAnimalById(UUID id) {
        Optional<Animal> animal = animalRepository.findById(id);
        return animal.orElseThrow(() -> new AnimalNotFoundException("Could not find animal with id - " + id));
    }

    public Animal saveAnimal(Animal animal) {
        UUID enclosureId = assignEnclosure(animal.getFood(), animal.getAmount());
        animal.setEnclosureId(assignEnclosure(animal.getFood(), animal.getAmount()));
        updateEnclosureDataWithAnimalInfo(enclosureId, animal.getAmount(), animal.getFood());

        return animalRepository.save(animal);
    }

    public Animal updateAnimal(UUID id, Animal animal) {
        try {
            Animal tempAnimal = findAnimalById(id);
            animal.setId(id);
            animal.setFood(tempAnimal.getFood());
            animal.setEnclosureId(tempAnimal.getEnclosureId());
            updateEnclosureDataWithNewAmount(animal.getEnclosureId(), animal.getAmount() - tempAnimal.getAmount());

            return animalRepository.save(animal);
        } catch (AnimalNotFoundException e) {
            throw new AnimalNotFoundException("Update failed. Could not find animal with id - " + id);
        }
    }

    public void deleteAnimalById(UUID id) {
        try {
            Animal animal = findAnimalById(id);
            updateEnclosureDataWithAnimalInfo(animal.getEnclosureId(), -animal.getAmount(), animal.getFood());
            animalRepository.deleteById(id);
        } catch (AnimalNotFoundException e) {
            throw new AnimalNotFoundException("Deletion failed. Could not find animal with id - " + id);
        }
    }

    public List<Enclosure> findAllEnclosures() {
        return enclosureRepository.findAll();
    }

    public Enclosure findEnclosureById(UUID id) {
        Optional<Enclosure> enclosure = enclosureRepository.findById(id);
        return enclosure.orElseThrow(() -> new EnclosureNotFoundException("Could not find enclosure with id - " + id));
    }

    public List<Enclosure> storeEnclosures(MultipartFile file) {
        evaluateIfEnclosuresAreNotStoredAlready();
        EnclosuresList enclosuresList = inputReadingService.retrieveEnclosuresData(file);

        return enclosureRepository.saveAll(enclosuresList.toEntity());
    }

    public List<Animal> storeAnimals(MultipartFile file) {
        evaluateIfEnclosuresAreStored();
        evaluateIfAnimalsAreNotStoredAlready();
        AnimalsList animalsList = inputReadingService.retrieveAnimalsData(file);
        List<Animal> givenAnimals = animalsList.toEntity();
        for (Animal animal : givenAnimals) {
            saveAnimal(animal);
        }

        return animalRepository.findAll();
    }

    private UUID assignEnclosure(String food, int amount) {
        List<Enclosure> enclosures = enclosureRepository.findAll();

        return food.equals("Carnivore")
                ? findSuitableEnclosureForCarnivore(food, amount, enclosures)
                : findSuitableEnclosureForHerbivore(food, amount, enclosures);
    }

    private void updateEnclosureDataWithAnimalInfo(UUID id, int amount, String food) {
        Optional<Enclosure> optionalEnclosure = enclosureRepository.findById(id);
        if (optionalEnclosure.isPresent()) {
            Enclosure enclosure = optionalEnclosure.get();
            enclosure.setId(id);
            enclosure.setFreeSpace(enclosure.getFreeSpace() - amount);
            if (amount > 0) {
                enclosure.setAnimals(food);
            } else {
                enclosure.removeAnimals(food);
            }
            enclosureRepository.save(enclosure);
        }
    }

    private void updateEnclosureDataWithNewAmount(UUID id, int amount) {
        Optional<Enclosure> optionalEnclosure = enclosureRepository.findById(id);
        if (optionalEnclosure.isPresent()) {
            Enclosure enclosure = optionalEnclosure.get();
            enclosure.setId(id);
            enclosure.setFreeSpace(enclosure.getFreeSpace() - amount);
            enclosureRepository.save(enclosure);
        }
    }

    private void evaluateIfEnclosuresAreNotStoredAlready() {
        if (enclosureRepository.count() > 0) {
            throw new DataAlreadyStoredException("File reading cancelled. Given enclosures are already stored in database.");
        }
    }

    private void evaluateIfEnclosuresAreStored() {
        if (enclosureRepository.count() == 0) {
            throw new EnclosureNotFoundException("File reading cancelled. Please store enclosures before proceeding with animals.");
        }
    }

    private void evaluateIfAnimalsAreNotStoredAlready() {
        if (animalRepository.count() > 0) {
            throw new DataAlreadyStoredException("File reading cancelled. Given animals are already stored in database.");
        }
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

        if (!foundSuitableEnclosure) {
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

        if (!foundSuitableEnclosure) {
            throw new NoAvailableEnclosureException("There is no enough space in enclosures. Animal can`t be placed.");
        }

        return suitableEnclosureId;
    }
}
