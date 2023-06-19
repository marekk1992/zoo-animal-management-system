package com.example.zooanimalmanagementsystem.service;

import com.example.zooanimalmanagementsystem.repository.AnimalRepository;
import com.example.zooanimalmanagementsystem.repository.EnclosureRepository;
import com.example.zooanimalmanagementsystem.repository.model.Animal;
import com.example.zooanimalmanagementsystem.repository.model.Enclosure;
import com.example.zooanimalmanagementsystem.service.exception.AnimalNotFoundException;
import com.example.zooanimalmanagementsystem.service.exception.DataAlreadyStoredException;
import com.example.zooanimalmanagementsystem.service.exception.EnclosureNotFoundException;
import com.example.zooanimalmanagementsystem.service.exception.IncorrectAnimalTypeException;
import com.example.zooanimalmanagementsystem.service.exception.NotEnoughFreeSpaceInEnclosure;
import com.example.zooanimalmanagementsystem.service.model.AnimalDetails;
import com.example.zooanimalmanagementsystem.service.model.AnimalsList;
import com.example.zooanimalmanagementsystem.service.model.EnclosureDetails;
import com.example.zooanimalmanagementsystem.service.model.EnclosuresList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ZooServiceTest {

    private static final UUID ID_1 = UUID.fromString("6879e088-6a43-4f13-bd57-6bddb54fbd1b");
    private static final UUID ID_2 = UUID.fromString("2159cfe1-a549-4aa5-8ff6-b8257366c94d");
    private static final UUID ENCLOSURE_ID = UUID.fromString("7c0e1530-3232-4547-854c-68876f4d6fd7");

    @Mock
    private EnclosureRepository enclosureRepository;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private InputReadingService inputReadingService;

    @InjectMocks
    private ZooService zooService;

    @Test
    void returns_collection_of_animals() {
        // given
        List<Animal> expectedAnimals = List.of(
                        new Animal(ID_1, "Lion", "Carnivore", 3, ENCLOSURE_ID),
                        new Animal(ID_2, "Giraffe", "Herbivore", 2, ENCLOSURE_ID)
        );
        when(animalRepository.findAll()).thenReturn(expectedAnimals);

        // when
        List<Animal> actualAnimals = zooService.findAllAnimals();

        // then
        assertThat(actualAnimals)
                .isEqualTo(expectedAnimals);
    }

    @Test
    void finds_animal_by_id() {
        // given
        Animal expectedAnimal = new Animal(ID_1, "Lion", "Carnivore", 3, ENCLOSURE_ID);
        when(animalRepository.findById(ID_1)).thenReturn(Optional.of(expectedAnimal));

        // when
        Animal actualAnimal = zooService.findAnimalById(ID_1);

        // then
        assertThat(actualAnimal)
                .isEqualTo(expectedAnimal);
    }

    @Test
    void throws_exception_when_trying_to_find_non_existing_animal() {
        // given
        when(animalRepository.findById(ID_1)).thenReturn(Optional.empty());

        // then
        assertThatExceptionOfType(AnimalNotFoundException.class)
                .isThrownBy(() -> zooService.findAnimalById(ID_1))
                .withMessage("Could not find animal with id - " + ID_1);
    }

    @Test
    void saves_animal() {
        // given
        Animal givenAnimal = new Animal("Lion", "Carnivore", 3);
        givenAnimal.setEnclosureId(ENCLOSURE_ID);
        Animal expectedAnimal = new Animal(ID_1, "Lion", "Carnivore", 3, ENCLOSURE_ID);
        Enclosure givenEnclosure = new Enclosure(
                ENCLOSURE_ID,
                "Test Enclosure 1",
                "Large",
                "Inside",
                List.of("Pool", "Rocks", "Trees"),
                11,
                new ArrayList<>(List.of("Empty"))
        );
        when(enclosureRepository.findAll()).thenReturn(List.of(givenEnclosure));
        when(animalRepository.save(givenAnimal)).thenReturn(expectedAnimal);

        // when
        Animal actualAnimal = zooService.saveAnimal(givenAnimal);

        // then
        assertThat(actualAnimal)
                .isEqualTo(expectedAnimal);
    }

    @Test
    void throws_exception_when_provided_incorrect_animal_food_type() {
        // given
        Animal givenAnimal = new Animal("Lion", "IncorrectFood", 3);

        // then
        assertThatExceptionOfType(IncorrectAnimalTypeException.class)
                .isThrownBy(() -> zooService.saveAnimal(givenAnimal))
                .withMessage("Please specify correct animal food. Usage 'Carnivore' or 'Herbivore'.");
    }

    @Test
    void throws_exception_when_there_is_no_available_enclosures() {
        // given
        Animal givenAnimal = new Animal("Lion", "Carnivore", 3);

        // then
        assertThatExceptionOfType(EnclosureNotFoundException.class)
                .isThrownBy(() -> zooService.saveAnimal(givenAnimal))
                .withMessage("Can`t find suitable enclosure for given animal.");
    }

    @Test
    void deletes_animal_by_id() {
        // given
        Animal givenAnimal = new Animal(ID_1, "Lion", "Carnivore", 3, ENCLOSURE_ID);
        Enclosure givenEnclosure = new Enclosure(
                ENCLOSURE_ID,
                "Test Enclosure 1",
                "Large",
                "Inside",
                List.of("Pool", "Rocks", "Trees"),
                8,
                new ArrayList<>(List.of("Carnivore"))
        );
        when(animalRepository.findById(ID_1)).thenReturn(Optional.of(givenAnimal));
        when(enclosureRepository.findById(ENCLOSURE_ID)).thenReturn(Optional.of(givenEnclosure));
        doNothing().when(animalRepository).deleteById(ID_1);

        // when
        zooService.deleteAnimalById(ID_1);

        // then
        verify(animalRepository, times(1)).deleteById(ID_1);
        verifyNoMoreInteractions(animalRepository);
    }

    @Test
    void throws_exception_when_trying_to_delete_non_existing_animal() {
        // given
        doThrow(AnimalNotFoundException.class).when(animalRepository).findById(ID_1);

        // then
        assertThatExceptionOfType(AnimalNotFoundException.class)
                .isThrownBy(() -> zooService.deleteAnimalById(ID_1))
                .withMessage("Deletion failed. Could not find animal with id - " + ID_1);
    }

    @Test
    void updates_animal_by_id_with_provided_data() {
        // given
        Animal givenAnimal = new Animal("Lion", 3);
        Animal expectedAnimal = new Animal(ID_1, "Lion", "Carnivore", 3, ENCLOSURE_ID);
        ArrayList<String> animals = new ArrayList<>(List.of("Carnivore"));
        Enclosure givenEnclosure = new Enclosure(
                ENCLOSURE_ID, "Test Enclosure 1", "Large", "Inside", List.of("Pool", "Rocks", "Trees"), 8, animals);
        when(animalRepository.findById(ID_1)).thenReturn(Optional.of(expectedAnimal));
        when(enclosureRepository.findById(ENCLOSURE_ID)).thenReturn(Optional.of(givenEnclosure));
        when(animalRepository.save(expectedAnimal)).thenReturn(expectedAnimal);

        // when
        Animal actualAnimal = zooService.updateAnimal(ID_1, givenAnimal);

        // then
        assertThat(actualAnimal)
                .isEqualTo(expectedAnimal);
    }

    @Test
    void throws_exception_when_trying_to_update_non_existing_animal() {
        // given
        Animal givenAnimal = new Animal("Lion", 3);
        when(animalRepository.findById(ID_1)).thenReturn(Optional.empty());

        // then
        assertThatExceptionOfType(AnimalNotFoundException.class)
                .isThrownBy(() -> zooService.updateAnimal(ID_1, givenAnimal))
                .withMessage("Update failed. Could not find animal with id - " + ID_1);
    }

    @Test
    void throws_exception_when_trying_to_set_animals_amount_higher_than_actual_enclosure_free_space() {
        // given
        String message = "Update failed. Enclosure can`t store such amount of animals.";
        Animal givenAnimal = new Animal("Lion", 7);
        Animal actualAnimal = new Animal(ID_1, "Lion", "Carnivore", 5, ENCLOSURE_ID);
        ArrayList<String> animals = new ArrayList<>(List.of("Carnivore"));
        Enclosure givenEnclosure = new Enclosure(
                ENCLOSURE_ID, "Test Enclosure 1", "Large", "Inside", List.of("Pool", "Rocks", "Trees"), 1, animals);
        when(animalRepository.findById(ID_1)).thenReturn(Optional.of(actualAnimal));
        when(enclosureRepository.findById(ENCLOSURE_ID)).thenReturn(Optional.of(givenEnclosure));

        // then
        assertThatExceptionOfType(NotEnoughFreeSpaceInEnclosure.class)
                .isThrownBy(() -> zooService.updateAnimal(ID_1, givenAnimal))
                .withMessage("Update failed. Enclosure can`t store such amount of animals.");
    }

    @Test
    void stores_enclosures_data() throws IOException {
        // given
        List<Enclosure> expectedEnclosures = List.of(
                new Enclosure(ID_1, "Test Enclosure 1", "Large", "Outside", List.of("Pool", "Rocks", "Trees")),
                new Enclosure(ID_2, "Test Enclosure 2", "Medium", "Inside", List.of("Logs", "Rocks", "Trees"))
        );
        EnclosuresList expectedEnclosuresList = new EnclosuresList(
                List.of(
                        new EnclosureDetails("Test Enclosure 1", "Large", "Outside", List.of("Pool", "Rocks", "Trees")),
                        new EnclosureDetails("Test Enclosure 2", "Medium", "Inside", List.of("Logs", "Rocks", "Trees"))
                )
        );
        MockMultipartFile givenFile = new MockMultipartFile(
                "file",
                "enclosures_test_data.json",
                String.valueOf(MediaType.APPLICATION_JSON),
                new FileInputStream("src/test/resources/enclosures_test_data.json")
        );
        when(inputReadingService.retrieveEnclosuresData(givenFile)).thenReturn(expectedEnclosuresList);
        when(enclosureRepository.saveAll(
                argThat(matchesEnclosuresListToEntity(expectedEnclosuresList))
        ))
                .thenReturn(expectedEnclosures);
        // when
        List<Enclosure> actualEnclosures = zooService.storeEnclosures(givenFile);

        // then
        assertThat(actualEnclosures)
                .isEqualTo(expectedEnclosures);
    }

    @Test
    void throws_exception_when_enclosures_data_already_saved() throws IOException {
        // given
        MockMultipartFile givenFile = new MockMultipartFile(
                "file",
                "enclosures_test_data.json",
                String.valueOf(MediaType.APPLICATION_JSON),
                new FileInputStream("src/test/resources/enclosures_test_data.json")
        );
        when(enclosureRepository.count()).thenReturn(1L);

        // then
        assertThatExceptionOfType(DataAlreadyStoredException.class)
                .isThrownBy(() -> zooService.storeEnclosures(givenFile))
                .withMessage("File reading cancelled. Given enclosures are already stored in database.");
    }

    @Test
    void stores_animals_data() throws IOException {
        // given
        Animal expectedAnimal = new Animal(ID_1, "Test 1", "Herbivore", 3, ENCLOSURE_ID);
        AnimalDetails expectedAnimalDetails = new AnimalDetails("Test 1", "Herbivore", 2);
        Enclosure givenEnclosure = new Enclosure(
                ENCLOSURE_ID,
                "Test Enclosure 1",
                "Large",
                "Inside",
                List.of("Pool", "Rocks", "Trees"),
                11,
                new ArrayList<>(List.of("Empty"))
        );

        MockMultipartFile givenFile = new MockMultipartFile(
                "file",
                "animals_test_data.json",
                String.valueOf(MediaType.APPLICATION_JSON),
                new FileInputStream("src/test/resources/animals_test_data.json")
        );

        when(inputReadingService.retrieveAnimalsData(givenFile)).thenReturn(new AnimalsList(List.of(expectedAnimalDetails)));
        when(enclosureRepository.count()).thenReturn(1L);
        when(enclosureRepository.findAll()).thenReturn(List.of(givenEnclosure));
        when(animalRepository.save(argThat(matchesAnimalDetailsToEntity(expectedAnimalDetails)))).thenReturn(expectedAnimal);
        when(animalRepository.findAll()).thenReturn(List.of(expectedAnimal));

        // when
        List<Animal> actualAnimals = zooService.storeAnimals(givenFile);

        // then
        assertThat(actualAnimals)
                .isEqualTo(List.of(expectedAnimal));
    }

    @Test
    void throws_exception_when_trying_to_store_animals_without_storing_enclosures_before() throws IOException {
        // given
        MockMultipartFile givenFile = new MockMultipartFile(
                "file",
                "animals_test_data.json",
                String.valueOf(MediaType.APPLICATION_JSON),
                new FileInputStream("src/test/resources/animals_test_data.json")
        );
        when(enclosureRepository.count()).thenReturn(0L);

        // then
        assertThatExceptionOfType(EnclosureNotFoundException.class)
                .isThrownBy(() -> zooService.storeAnimals(givenFile))
                .withMessage("File reading cancelled. Please store enclosures before proceeding with animals.");
    }

    @Test
    void throws_exception_when_animals_data_already_saved() throws IOException {
        // given
        MockMultipartFile givenFile = new MockMultipartFile(
                "file",
                "animals_test_data.json",
                String.valueOf(MediaType.APPLICATION_JSON),
                new FileInputStream("src/test/resources/animals_test_data.json")
        );
        when(enclosureRepository.count()).thenReturn(1L);
        when(animalRepository.count()).thenReturn(1L);

        // then
        assertThatExceptionOfType(DataAlreadyStoredException.class)
                .isThrownBy(() -> zooService.storeAnimals(givenFile))
                .withMessage("File reading cancelled. Given animals are already stored in database.");
    }

    private ArgumentMatcher<List<Enclosure>> matchesEnclosuresListToEntity(EnclosuresList enclosuresList) {
        return enclosures -> enclosures.get(0).getName().equals(enclosuresList.enclosures().get(0).name()) &&
                             enclosures.get(0).getSize().equals(enclosuresList.enclosures().get(0).size()) &&
                             enclosures.get(0).getLocation().equals(enclosuresList.enclosures().get(0).location()) &&
                             enclosures.get(0).getObjects().equals(enclosuresList.enclosures().get(0).objects()) &&
                             enclosures.get(1).getName().equals(enclosuresList.enclosures().get(1).name()) &&
                             enclosures.get(1).getSize().equals(enclosuresList.enclosures().get(1).size()) &&
                             enclosures.get(1).getLocation().equals(enclosuresList.enclosures().get(1).location()) &&
                             enclosures.get(1).getObjects().equals(enclosuresList.enclosures().get(1).objects());
    }

    private ArgumentMatcher<Animal> matchesAnimalDetailsToEntity(AnimalDetails animalDetails) {
        return animal -> animal.getSpecies().equals(animalDetails.species()) &&
                         animal.getFood().equals(animalDetails.food()) &&
                         animal.getAmount() == animalDetails.amount();
    }
}
