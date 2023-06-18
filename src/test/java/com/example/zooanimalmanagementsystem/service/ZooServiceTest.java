package com.example.zooanimalmanagementsystem.service;

import com.example.zooanimalmanagementsystem.repository.AnimalRepository;
import com.example.zooanimalmanagementsystem.repository.EnclosureRepository;
import com.example.zooanimalmanagementsystem.repository.model.Enclosure;
import com.example.zooanimalmanagementsystem.service.exception.DataAlreadyStoredException;
import com.example.zooanimalmanagementsystem.service.exception.EnclosuresDataNotFound;
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
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ZooServiceTest {

    private static final UUID ID_1 = UUID.fromString("926e09f7-3c78-469d-bdbc-2d34d314c1b4");
    private static final UUID ID_2 = UUID.fromString("2d88924f-0f63-4280-9e58-a9a126049273");

    @Mock
    private EnclosureRepository enclosureRepository;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private InputReadingService inputReadingService;

    @InjectMocks
    private ZooService zooService;

    @Test
    void saves_collection_of_enclosures() throws IOException {
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
        List<Enclosure> actualEnclosures = zooService.saveEnclosures(givenFile);

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
                .isThrownBy(() -> zooService.saveEnclosures(givenFile))
                .withMessage("File reading cancelled. Given enclosures are already stored in database.");
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
        assertThatExceptionOfType(EnclosuresDataNotFound.class)
                .isThrownBy(() -> zooService.saveAnimals(givenFile))
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
                .isThrownBy(() -> zooService.saveAnimals(givenFile))
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
}
