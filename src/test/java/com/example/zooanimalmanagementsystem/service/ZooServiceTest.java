package com.example.zooanimalmanagementsystem.service;

import com.example.zooanimalmanagementsystem.repository.EnclosureRepository;
import com.example.zooanimalmanagementsystem.repository.model.Enclosure;
import com.example.zooanimalmanagementsystem.service.model.EnclosureDetails;
import com.example.zooanimalmanagementsystem.service.model.EnclosuresList;
import com.example.zooanimalmanagementsystem.service.exception.EnclosuresDataAlreadyStoredException;
import com.example.zooanimalmanagementsystem.service.exception.InputFileNotAvailableException;
import com.example.zooanimalmanagementsystem.service.exception.ReadingFromFileFailedException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @InjectMocks
    private ZooService zooService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void reads_and_saves_enclosures_data_from_file_to_database() throws IOException {
        // given
        List<Enclosure> expectedEnclosures = List.of(
                new Enclosure(ID_1, "Test Enclosure 1", "Large", "Outside", List.of("Pool", "Rocks", "Trees")),
                new Enclosure(ID_2, "Test Enclosure 2", "Medium", "Inside", List.of("Logs", "Rocks", "Trees"))
        );
        EnclosuresList enclosuresList = new EnclosuresList(
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
        when(enclosureRepository.saveAll(
                argThat(matchesEnclosuresListToEntity(enclosuresList))
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
        assertThatExceptionOfType(EnclosuresDataAlreadyStoredException.class)
                .isThrownBy(() -> zooService.saveEnclosures(givenFile))
                .withMessage("File reading cancelled. Enclosures are already stored in database.");
    }

    @Test
    void throws_exception_when_enclosures_file_not_provided_or_empty() throws IOException {
        // given
        MockMultipartFile givenFile = new MockMultipartFile(
                "file",
                "enclosures_test_data.json",
                String.valueOf(MediaType.APPLICATION_JSON),
                (byte[]) null
        );

        // then
        assertThatExceptionOfType(InputFileNotAvailableException.class)
                .isThrownBy(() -> zooService.saveEnclosures(givenFile))
                .withMessage("Can`t read data. File is either not uploaded or empty.");
    }

    @Test
    void throws_exception_when_data_in_file_has_incorrect_format() throws IOException {
        // given
        MockMultipartFile givenFile = new MockMultipartFile(
                "file",
                "empty_file.txt",
                String.valueOf(MediaType.APPLICATION_JSON),
                new FileInputStream("src/test/resources/empty_file.txt")
        );

        // then
        assertThatExceptionOfType(ReadingFromFileFailedException.class)
                .isThrownBy(() -> zooService.saveEnclosures(givenFile))
                .withMessage("Can`t read data from file. Make sure file has correct format");
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
