package com.example.zooanimalmanagementsystem.controller;

import com.example.zooanimalmanagementsystem.service.ZooService;
import com.example.zooanimalmanagementsystem.service.exception.DataAlreadyStoredException;
import com.example.zooanimalmanagementsystem.service.exception.EnclosuresDataNotFound;
import com.example.zooanimalmanagementsystem.service.exception.InputFileNotAvailableException;
import com.example.zooanimalmanagementsystem.service.exception.ReadingFromFileFailedException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.FileInputStream;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class ZooControllerTest {

    @MockBean
    private ZooService zooService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void reads_data_from_enclosures_json_file() throws Exception {
        // given
        MockMultipartFile givenFile = new MockMultipartFile(
                "file",
                "enclosures_test_data.json",
                String.valueOf(MediaType.APPLICATION_JSON),
                new FileInputStream("src/test/resources/enclosures_test_data.json")
        );
        String message = "Successfully read data from file: " + givenFile.getOriginalFilename();
        when(zooService.saveEnclosures(givenFile)).thenReturn(any());

        // then
        mockMvc.perform(multipart("/v1/zoo/upload/enclosures")
                        .file(givenFile))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void returns_404_response_when_enclosures_file_not_provided_or_empty() throws Exception {
        // given
        MockMultipartFile givenFile = new MockMultipartFile(
                "file",
                "enclosures_test_data.json",
                String.valueOf(MediaType.APPLICATION_JSON),
                (byte[]) null
        );
        String message = "Can`t read data. File is either not uploaded or empty.";
        doThrow(new InputFileNotAvailableException(message)).when(zooService).saveEnclosures(givenFile);

        // then
        mockMvc.perform(multipart("/v1/zoo/upload/enclosures")
                        .file(givenFile))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void returns_500_response_when_enclosures_file_is_already_read() throws Exception {
        // given
        MockMultipartFile givenFile = new MockMultipartFile(
                "file",
                "enclosures_test_data.json",
                String.valueOf(MediaType.APPLICATION_JSON),
                new FileInputStream("src/test/resources/enclosures_test_data.json")
        );
        String message = "File reading cancelled. Enclosures are already stored in database.";
        doThrow(new DataAlreadyStoredException(message)).when(zooService).saveEnclosures(givenFile);

        // then
        mockMvc.perform(multipart("/v1/zoo/upload/enclosures")
                        .file(givenFile))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void returns_500_response_enclosures_file_has_incorrect_format() throws Exception {
        // given
        MockMultipartFile givenFile = new MockMultipartFile(
                "file",
                "empty_file.txt",
                String.valueOf(MediaType.APPLICATION_JSON),
                new FileInputStream("src/test/resources/empty_file.txt")
        );
        String message = "Can`t read data from file. Make sure file has correct format";
        doThrow(new ReadingFromFileFailedException(message)).when(zooService).saveEnclosures(givenFile);

        // then
        mockMvc.perform(multipart("/v1/zoo/upload/enclosures")
                        .file(givenFile))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void reads_data_from_animals_json_file() throws Exception {
        // given
        MockMultipartFile givenFile = new MockMultipartFile(
                "file",
                "animals_test_data.json",
                String.valueOf(MediaType.APPLICATION_JSON),
                new FileInputStream("src/test/resources/animals_test_data.json")
        );
        String message = "Successfully read data from file: " + givenFile.getOriginalFilename();
        when(zooService.saveAnimals(givenFile)).thenReturn(any());

        // then
        mockMvc.perform(multipart("/v1/zoo/upload/animals")
                        .file(givenFile))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void returns_404_response_when_trying_to_store_animals_without_storing_enclosures_at_first() throws Exception {
        // given
        MockMultipartFile givenFile = new MockMultipartFile(
                "file",
                "animals_test_data.json",
                String.valueOf(MediaType.APPLICATION_JSON),
                (byte[]) null
        );
        String message = "File reading cancelled. Please store enclosures before proceeding with animals.";
        doThrow(new EnclosuresDataNotFound(message)).when(zooService).saveAnimals(givenFile);

        // then
        mockMvc.perform(multipart("/v1/zoo/upload/animals")
                        .file(givenFile))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void returns_404_response_when_animals_file_not_provided_or_empty() throws Exception {
        // given
        MockMultipartFile givenFile = new MockMultipartFile(
                "file",
                "animals_test_data.json",
                String.valueOf(MediaType.APPLICATION_JSON),
                (byte[]) null
        );
        String message = "Can`t read data. File is either not uploaded or empty.";
        doThrow(new InputFileNotAvailableException(message)).when(zooService).saveAnimals(givenFile);

        // then
        mockMvc.perform(multipart("/v1/zoo/upload/animals")
                        .file(givenFile))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void return_500_response_when_animals_data_is_already_stored() throws Exception {
        // given
        MockMultipartFile givenFile = new MockMultipartFile(
                "file",
                "animals_test_data.json",
                String.valueOf(MediaType.APPLICATION_JSON),
                new FileInputStream("src/test/resources/animals_test_data.json")
        );
        String message = "File reading cancelled. Given animals are already stored in database.";
        doThrow(new DataAlreadyStoredException(message)).when(zooService).saveAnimals(givenFile);

        // then
        mockMvc.perform(multipart("/v1/zoo/upload/animals")
                        .file(givenFile))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void returns_500_response_animals_file_has_incorrect_format() throws Exception {
        // given
        MockMultipartFile givenFile = new MockMultipartFile(
                "file",
                "empty_file.txt",
                String.valueOf(MediaType.APPLICATION_JSON),
                new FileInputStream("src/test/resources/empty_file.txt")
        );
        String message = "Can`t read data from file. Make sure file has correct format";
        doThrow(new ReadingFromFileFailedException(message)).when(zooService).saveAnimals(givenFile);

        // then
        mockMvc.perform(multipart("/v1/zoo/upload/animals")
                        .file(givenFile))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString(message)));
    }
}
