package com.example.zooanimalmanagementsystem.controller;

import com.example.zooanimalmanagementsystem.controller.model.CreateAnimalRequest;
import com.example.zooanimalmanagementsystem.controller.model.UpdateAnimalRequest;
import com.example.zooanimalmanagementsystem.repository.model.Animal;
import com.example.zooanimalmanagementsystem.service.ZooService;
import com.example.zooanimalmanagementsystem.service.exception.AnimalNotFoundException;
import com.example.zooanimalmanagementsystem.service.exception.DataAlreadyStoredException;
import com.example.zooanimalmanagementsystem.service.exception.EnclosureNotFoundException;
import com.example.zooanimalmanagementsystem.service.exception.InputFileNotAvailableException;
import com.example.zooanimalmanagementsystem.service.exception.NotEnoughFreeSpaceInEnclosure;
import com.example.zooanimalmanagementsystem.service.exception.ReadingFromFileFailedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.ArgumentMatcher;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.FileInputStream;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.blankString;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class ZooControllerTest {

    private static final UUID ID_1 = UUID.fromString("6879e088-6a43-4f13-bd57-6bddb54fbd1b");
    private static final UUID ID_2 = UUID.fromString("2159cfe1-a549-4aa5-8ff6-b8257366c94d");
    private static final UUID ENCLOSURE_ID = UUID.fromString("7c0e1530-3232-4547-854c-68876f4d6fd7");
    private static final String ANIMALS_URL = "/v1/zoo/animals";
    private static final String ANIMAL_BY_ID_URL = ANIMALS_URL + "/{animalId}";
    private static final String DATA_UPLOAD_URL = "/v1/zoo/upload";

    @MockBean
    private ZooService zooService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void returns_collection_of_animals() throws Exception {
        // given
        when(zooService.findAllAnimals()).thenReturn(List.of(
                new Animal(ID_1, "Lion", "Carnivore", 3, ENCLOSURE_ID),
                new Animal(ID_2, "Giraffe", "Herbivore", 2, ENCLOSURE_ID)
        ));
        String actualResponseBody = mockMvc.perform(get(ANIMALS_URL))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // then
        JSONAssert.assertEquals(
                """
                           {
                              "animals": [
                                  {
                                      "animalId": "6879e088-6a43-4f13-bd57-6bddb54fbd1b",
                                      "species": "Lion",
                                      "food": "Carnivore",
                                      "amount": 3,
                                      "assignedEnclosureId": "7c0e1530-3232-4547-854c-68876f4d6fd7"
                                  },
                                  {
                                      "animalId": "2159cfe1-a549-4aa5-8ff6-b8257366c94d",
                                      "species": "Giraffe",
                                      "food": "Herbivore",
                                      "amount": 2,
                                      "assignedEnclosureId": "7c0e1530-3232-4547-854c-68876f4d6fd7"
                                  }
                              ]
                           }
                        """,
                actualResponseBody, true);
    }

    @Test
    void returns_animal_by_id() throws Exception {
        // given
        when(zooService.findAnimalById(ID_1)).
                thenReturn(new Animal(ID_1, "Lion", "Carnivore", 3, ENCLOSURE_ID));

        // when
        String actualResponseBody = mockMvc.perform(get(ANIMAL_BY_ID_URL, ID_1))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // then
        JSONAssert.assertEquals(
                """
                          {
                              "animalId": "6879e088-6a43-4f13-bd57-6bddb54fbd1b",
                              "species": "Lion",
                              "food": "Carnivore",
                              "amount": 3,
                              "assignedEnclosureId": "7c0e1530-3232-4547-854c-68876f4d6fd7"
                          }
                        """,
                actualResponseBody, true);
    }

    @Test
    void returns_response_404_when_trying_to_get_non_existing_animal() throws Exception {
        // given
        String message = "Could not find animal by id - " + ID_1;
        doThrow(new AnimalNotFoundException(message)).when(zooService).findAnimalById(ID_1);

        // then
        mockMvc.perform(get(ANIMAL_BY_ID_URL, ID_1))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void creates_animal() throws Exception {
        // given
        CreateAnimalRequest createAnimalRequest = new CreateAnimalRequest("Lion", "Carnivore", 3);
        Animal expectedAnimal = new Animal(ID_1, "Lion", "Carnivore", 3, ENCLOSURE_ID);
        when(zooService.saveAnimal(argThat(matchCreateAnimalRequestToEntity(createAnimalRequest)))).thenReturn(expectedAnimal);

        // when
        String actualResponseBody = mockMvc.perform(post(ANIMALS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createAnimalRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // then
        JSONAssert.assertEquals(
                """
                           {
                              "animalId": "6879e088-6a43-4f13-bd57-6bddb54fbd1b",
                              "species": "Lion",
                              "food": "Carnivore",
                              "amount": 3,
                              "assignedEnclosureId": "7c0e1530-3232-4547-854c-68876f4d6fd7"
                           }
                        """,
                actualResponseBody, true);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/invalid_animal_create_request_parameters.csv", numLinesToSkip = 1)
    void returns_500_response_when_user_input_validation_for_creating_animal_is_failed(
            String species, String food, int amount, String message
    ) throws Exception {
        // given
        String requestBody = objectMapper
                .writeValueAsString(new CreateAnimalRequest(species, food, amount));

        // then
        mockMvc.perform(post(ANIMALS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void return_500_response_when_provided_incorrect_animal_food() throws Exception {
        // given
        String message = "Please specify correct animal food. Usage 'Carnivore' or 'Herbivore'.";
        CreateAnimalRequest createAnimalRequest = new CreateAnimalRequest("Wolf", "InvalidFood", 3);
        String requestBody = objectMapper.writeValueAsString(createAnimalRequest);
        doThrow(new EnclosureNotFoundException(message))
                .when(zooService).saveAnimal(argThat(matchCreateAnimalRequestToEntity(createAnimalRequest)));

        // then
        mockMvc.perform(post(ANIMALS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void returns_404_response_when_there_is_no_suitable_enclosure() throws Exception {
        // given
        String message = "Can`t find suitable enclosure for given animal.";
        CreateAnimalRequest createAnimalRequest = new CreateAnimalRequest("Wolf", "Carnivore", 500);
        String requestBody = objectMapper
                .writeValueAsString(createAnimalRequest);
        doThrow(new EnclosureNotFoundException(message))
                .when(zooService).saveAnimal(argThat(matchCreateAnimalRequestToEntity(createAnimalRequest)));

        // then
        mockMvc.perform(post(ANIMALS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void updates_animal_by_id() throws Exception {
        // given
        UpdateAnimalRequest updateAnimalRequest = new UpdateAnimalRequest("Wolf", 3);
        Animal expectedAnimal = new Animal(ID_1, "Wolf", "Carnivore", 3, ENCLOSURE_ID);
        when(zooService.updateAnimal(eq(ID_1), argThat(matchUpdateAnimalRequestToEntity(updateAnimalRequest)))).thenReturn(expectedAnimal);

        // when
        String actualResponseBody = mockMvc.perform(put(ANIMAL_BY_ID_URL, ID_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateAnimalRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // then
        JSONAssert.assertEquals(
                """
                           {
                              "animalId": "6879e088-6a43-4f13-bd57-6bddb54fbd1b",
                              "species": "Wolf",
                              "food": "Carnivore",
                              "amount": 3,
                              "assignedEnclosureId": "7c0e1530-3232-4547-854c-68876f4d6fd7"
                           }
                        """,
                actualResponseBody, true);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/invalid_animal_update_request_parameters.csv", numLinesToSkip = 1)
    void returns_500_response_when_user_input_validation_for_updating_animal_is_failed(
            String species, int amount, String message
    ) throws Exception {
        // given
        String requestBody = objectMapper.writeValueAsString(new UpdateAnimalRequest(species, amount));

        // then
        mockMvc.perform(put(ANIMAL_BY_ID_URL, ID_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void return_404_response_when_trying_to_update_non_existing_animal() throws Exception {
        // given
        String message = "Update failed. Could not find animal by id - " + ID_1;
        String requestBody = objectMapper.writeValueAsString(new UpdateAnimalRequest("Wolf", 3));
        doThrow(new AnimalNotFoundException(message)).when(zooService).updateAnimal(any(UUID.class), any(Animal.class));

        // then
        mockMvc.perform(put(ANIMAL_BY_ID_URL, ID_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void return_500_response_when_provided_higher_animals_amount_for_update_than_enclosure_can_actually_store() throws Exception {
        // given
        String message = "Update failed. Enclosure can`t store such amount of animals.";
        String requestBody = objectMapper.writeValueAsString(new UpdateAnimalRequest("Wolf", 50));
        doThrow(new NotEnoughFreeSpaceInEnclosure(message)).when(zooService).updateAnimal(any(UUID.class), any(Animal.class));

        // then
        mockMvc.perform(put(ANIMAL_BY_ID_URL, ID_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void deletes_animal_by_id() throws Exception {
        // given
        doNothing().when(zooService).deleteAnimalById(ID_1);

        // then
        mockMvc.perform(delete(ANIMAL_BY_ID_URL, ID_1))
                .andExpect(status().isOk())
                .andExpect(content().string(blankString()));
    }

    @Test
    void returns_404_response_when_trying_to_delete_non_existing_animal() throws Exception {
        // given
        String message = "Deletion failed. Could not find animal with id - " + ID_1;
        doThrow(new AnimalNotFoundException(message)).when(zooService).deleteAnimalById(ID_1);

        // then
        mockMvc.perform(delete(ANIMAL_BY_ID_URL, ID_1))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void returns_500_response_when_trying_to_delete_animal_without_specifying_its_id() throws Exception {
        // expect
        mockMvc.perform(delete(ANIMALS_URL))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Request method 'DELETE' is not supported")));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/file_info.csv", numLinesToSkip = 1)
    void reads_data_from_json_file(String fileName, String urlPart) throws Exception {
        // given
        MockMultipartFile givenFile = new MockMultipartFile(
                "file",
                fileName,
                String.valueOf(MediaType.APPLICATION_JSON),
                new FileInputStream("src/test/resources/" + fileName)
        );
        String message = "Successfully read data from file: " + givenFile.getOriginalFilename();
        when(zooService.storeAnimals(givenFile)).thenReturn(anyList());

        // then
        mockMvc.perform(multipart(DATA_UPLOAD_URL + urlPart)
                        .file(givenFile))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void returns_404_response_when_enclosures_is_file_not_provided_or_empty() throws Exception {
        // given
        MockMultipartFile givenFile = new MockMultipartFile(
                "file",
                "enclosures_test_data.json",
                String.valueOf(MediaType.APPLICATION_JSON),
                (byte[]) null
        );
        String message = "Can`t read data. File is either not uploaded or empty.";
        doThrow(new InputFileNotAvailableException(message)).when(zooService).storeEnclosures(givenFile);

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
        doThrow(new DataAlreadyStoredException(message)).when(zooService).storeEnclosures(givenFile);

        // then
        mockMvc.perform(multipart("/v1/zoo/upload/enclosures")
                        .file(givenFile))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void returns_500_response_when_enclosures_file_has_incorrect_format() throws Exception {
        // given
        MockMultipartFile givenFile = new MockMultipartFile(
                "file",
                "empty_file.txt",
                String.valueOf(MediaType.APPLICATION_JSON),
                new FileInputStream("src/test/resources/empty_file.txt")
        );
        String message = "Can`t read data from file. Make sure file has correct format";
        doThrow(new ReadingFromFileFailedException(message)).when(zooService).storeEnclosures(givenFile);

        // then
        mockMvc.perform(multipart("/v1/zoo/upload/enclosures")
                        .file(givenFile))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void returns_404_response_when_trying_to_store_animals_without_storing_enclosures_first() throws Exception {
        // given
        MockMultipartFile givenFile = new MockMultipartFile(
                "file",
                "animals_test_data.json",
                String.valueOf(MediaType.APPLICATION_JSON),
                (byte[]) null
        );
        String message = "File reading cancelled. Please store enclosures before proceeding with animals.";
        doThrow(new EnclosureNotFoundException(message)).when(zooService).storeAnimals(givenFile);

        // then
        mockMvc.perform(multipart("/v1/zoo/upload/animals")
                        .file(givenFile))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void returns_404_response_when_animals_file_is_not_provided_or_empty() throws Exception {
        // given
        MockMultipartFile givenFile = new MockMultipartFile(
                "file",
                "animals_test_data.json",
                String.valueOf(MediaType.APPLICATION_JSON),
                (byte[]) null
        );
        String message = "Can`t read data. File is either not uploaded or empty.";
        doThrow(new InputFileNotAvailableException(message)).when(zooService).storeAnimals(givenFile);

        // then
        mockMvc.perform(multipart("/v1/zoo/upload/animals")
                        .file(givenFile))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void returns_500_response_when_animals_data_is_already_stored() throws Exception {
        // given
        MockMultipartFile givenFile = new MockMultipartFile(
                "file",
                "animals_test_data.json",
                String.valueOf(MediaType.APPLICATION_JSON),
                new FileInputStream("src/test/resources/animals_test_data.json")
        );
        String message = "File reading cancelled. Given animals are already stored in database.";
        doThrow(new DataAlreadyStoredException(message)).when(zooService).storeAnimals(givenFile);

        // then
        mockMvc.perform(multipart("/v1/zoo/upload/animals")
                        .file(givenFile))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void returns_500_response_when_animals_file_has_incorrect_format() throws Exception {
        // given
        MockMultipartFile givenFile = new MockMultipartFile(
                "file",
                "empty_file.txt",
                String.valueOf(MediaType.APPLICATION_JSON),
                new FileInputStream("src/test/resources/empty_file.txt")
        );
        String message = "Can`t read data from file. Make sure file has correct format";
        doThrow(new ReadingFromFileFailedException(message)).when(zooService).storeAnimals(givenFile);

        // then
        mockMvc.perform(multipart("/v1/zoo/upload/animals")
                        .file(givenFile))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString(message)));
    }

    private ArgumentMatcher<Animal> matchCreateAnimalRequestToEntity(CreateAnimalRequest createAnimalRequest) {
        return animal -> animal.getSpecies().equals(createAnimalRequest.species()) &&
                         animal.getFood().equals(createAnimalRequest.food()) &&
                         animal.getAmount() == createAnimalRequest.amount();
    }

    private ArgumentMatcher<Animal> matchUpdateAnimalRequestToEntity(UpdateAnimalRequest updateAnimalRequest) {
        return animal -> animal.getSpecies().equals(updateAnimalRequest.species()) &&
                         animal.getAmount() == updateAnimalRequest.amount();
    }
}
