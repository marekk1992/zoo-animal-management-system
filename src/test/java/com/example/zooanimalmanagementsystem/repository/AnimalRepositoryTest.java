package com.example.zooanimalmanagementsystem.repository;

import com.example.zooanimalmanagementsystem.repository.model.Animal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class AnimalRepositoryTest {

    private static final UUID ID_1 = UUID.fromString("6879e088-6a43-4f13-bd57-6bddb54fbd1b");
    private static final UUID ID_2 = UUID.fromString("2159cfe1-a549-4aa5-8ff6-b8257366c94d");
    private static final UUID ENCLOSURE_ID = UUID.fromString("7c0e1530-3232-4547-854c-68876f4d6fd7");

    @Autowired
    private AnimalRepository animalRepository;

    @Test
    void saves_collection_of_animals() {
        //given
        List<Animal> savedAnimals = List.of(
                new Animal(ID_1, "Lion", "Carnivore", 3, ENCLOSURE_ID),
                new Animal(ID_2, "Giraffe", "Herbivore", 2, ENCLOSURE_ID)
        );

        // when
        List<Animal> actualAnimals = animalRepository.saveAll(savedAnimals);

        // then
        assertThat(actualAnimals)
                .extracting("id")
                .containsExactly(ID_1, ID_2);
    }

    @Test
    void counts_number_of_animals_in_repository() {
        //given
        List<Animal> savedAnimals = List.of(
                new Animal(ID_1, "Lion", "Carnivore", 3, ENCLOSURE_ID),
                new Animal(ID_2, "Giraffe", "Herbivore", 2, ENCLOSURE_ID)
        );
        animalRepository.saveAll(savedAnimals);

        // when
        long actualNumberOfAnimalsInRepository = animalRepository.count();

        // then
        assertThat(actualNumberOfAnimalsInRepository)
                .isEqualTo(2L);
    }
}
