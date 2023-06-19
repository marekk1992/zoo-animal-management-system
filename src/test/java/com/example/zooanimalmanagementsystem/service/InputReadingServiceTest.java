package com.example.zooanimalmanagementsystem.service;

import com.example.zooanimalmanagementsystem.service.exception.InputFileNotAvailableException;
import com.example.zooanimalmanagementsystem.service.exception.ReadingFromFileFailedException;
import com.example.zooanimalmanagementsystem.service.model.AnimalDetails;
import com.example.zooanimalmanagementsystem.service.model.AnimalsList;
import com.example.zooanimalmanagementsystem.service.model.EnclosureDetails;
import com.example.zooanimalmanagementsystem.service.model.EnclosuresList;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class InputReadingServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final InputReadingService inputReadingService = new InputReadingService();

    @Test
    void reads_data_from_enclosures_file_and_returns_enclosures_list() throws IOException {
        // given
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

        // when
        EnclosuresList actualEnclosuresList = inputReadingService.retrieveEnclosuresData(givenFile);

        // then
        assertThat(actualEnclosuresList)
                .isEqualTo(expectedEnclosuresList);
    }

    @Test
    void throws_exception_when_enclosures_file_is_not_provided_or_empty() {
        // given
        MockMultipartFile givenFile = new MockMultipartFile(
                "file",
                "enclosures_test_data.json",
                String.valueOf(MediaType.APPLICATION_JSON),
                (byte[]) null
        );

        // then
        assertThatExceptionOfType(InputFileNotAvailableException.class)
                .isThrownBy(() -> inputReadingService.retrieveEnclosuresData(givenFile))
                .withMessage("Can`t read data. File is either not uploaded or empty.");
    }

    @Test
    void throws_exception_when_enclosures_file_has_incorrect_format() throws IOException {
        MockMultipartFile givenFile = new MockMultipartFile(
                "file",
                "incorrect_format.txt",
                String.valueOf(MediaType.APPLICATION_JSON),
                new FileInputStream("src/test/resources/incorrect_format.txt")
        );

        // then
        assertThatExceptionOfType(ReadingFromFileFailedException.class)
                .isThrownBy(() -> inputReadingService.retrieveEnclosuresData(givenFile))
                .withMessage("Can`t read data from file. Make sure file has correct format");
    }

    @Test
    void reads_data_from_animals_file_and_returns_animals_list() throws IOException {
        // given
        AnimalsList expectedAnimalsList = new AnimalsList(
                List.of(
                        new AnimalDetails("Test 1", "Herbivore", 2),
                        new AnimalDetails("Test 2", "Carnivore", 3)
                )
        );
        MockMultipartFile givenFile = new MockMultipartFile(
                "file",
                "animals_test_data.json",
                String.valueOf(MediaType.APPLICATION_JSON),
                new FileInputStream("src/test/resources/animals_test_data.json")
        );

        // when
        AnimalsList actualAnimalList = inputReadingService.retrieveAnimalsData(givenFile);

        // then
        assertThat(actualAnimalList)
                .isEqualTo(expectedAnimalsList);
    }

    @Test
    void throws_exception_when_animals_file_is_not_provided_or_empty() {
        // given
        MockMultipartFile givenFile = new MockMultipartFile(
                "file",
                "animals_test_data.json",
                String.valueOf(MediaType.APPLICATION_JSON),
                (byte[]) null
        );

        // then
        assertThatExceptionOfType(InputFileNotAvailableException.class)
                .isThrownBy(() -> inputReadingService.retrieveAnimalsData(givenFile))
                .withMessage("Can`t read data. File is either not uploaded or empty.");
    }

    @Test
    void throws_exception_when_animals_file_has_incorrect_format() throws IOException {
        MockMultipartFile givenFile = new MockMultipartFile(
                "file",
                "incorrect_format.txt",
                String.valueOf(MediaType.APPLICATION_JSON),
                new FileInputStream("src/test/resources/incorrect_format.txt")
        );

        // then
        assertThatExceptionOfType(ReadingFromFileFailedException.class)
                .isThrownBy(() -> inputReadingService.retrieveAnimalsData(givenFile))
                .withMessage("Can`t read data from file. Make sure file has correct format");
    }
}
