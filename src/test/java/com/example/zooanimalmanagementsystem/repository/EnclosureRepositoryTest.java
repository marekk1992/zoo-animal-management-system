package com.example.zooanimalmanagementsystem.repository;

import com.example.zooanimalmanagementsystem.repository.model.Enclosure;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class EnclosureRepositoryTest {

    private static final UUID ID_1 = UUID.fromString("6879e088-6a43-4f13-bd57-6bddb54fbd1b");
    private static final UUID ID_2 = UUID.fromString("2159cfe1-a549-4aa5-8ff6-b8257366c94d");

    @Autowired
    private EnclosureRepository enclosureRepository;

    @Test
    void returns_collection_of_enclosures() {
        // given
        enclosureRepository.saveAll(List.of(
                new Enclosure(ID_1, "Test Enclosure 1", "Medium", "Outside", List.of("Pool", "Rocks", "Trees")),
                new Enclosure(ID_2, "Test Enclosure 2", "Large", "Inside", List.of("Pool", "Rocks", "Trees"))
        ));

        // when
        List<Enclosure> actualEnclosures = enclosureRepository.findAll();

        // then
        assertThat(actualEnclosures)
                .extracting("id")
                .containsExactly(ID_1, ID_2);
    }

    @Test
    void returns_enclosure_by_id() {
        // given
        enclosureRepository.saveAll(List.of(
                new Enclosure(ID_1, "Test Enclosure 1", "Medium", "Outside", List.of("Pool", "Rocks", "Trees")),
                new Enclosure(ID_2, "Test Enclosure 2", "Large", "Inside", List.of("Pool", "Rocks", "Trees"))
        ));

        // when
        Optional<Enclosure> actualEnclosure = enclosureRepository.findById(ID_1);

        // then
        assertThat(actualEnclosure.get().getId())
                .isEqualTo(ID_1);
    }

    @Test
    void saves_collection_of_enclosures() {
        //given
        List<Enclosure> savedEnclosures = List.of(
                new Enclosure(ID_1, "Test Enclosure 1", "Medium", "Outside", List.of("Pool", "Rocks", "Trees")),
                new Enclosure(ID_2, "Test Enclosure 2", "Large", "Inside", List.of("Pool", "Rocks", "Trees"))
        );

        // when
        List<Enclosure> actualEnclosures = enclosureRepository.saveAll(savedEnclosures);

        // then
        assertThat(actualEnclosures)
                .extracting("id")
                .containsExactly(ID_1, ID_2);
    }

    @Test
    void saves_enclosure() {
        // when
        Enclosure savedEnclosure = enclosureRepository.save(
                new Enclosure(ID_1, "Test Enclosure 1", "Medium", "Outside", List.of("Pool", "Rocks", "Trees"))
        );

        // then
        assertThat(savedEnclosure.getId())
                .isEqualTo(ID_1);
    }

    @Test
    void counts_number_of_enclosures_in_repository() {
        //given
        List<Enclosure> savedEnclosures = List.of(
                new Enclosure(ID_1, "Test Enclosure 1", "Medium", "Outside", List.of("Pool", "Rocks", "Trees")),
                new Enclosure(ID_2, "Test Enclosure 2", "Large", "Inside", List.of("Pool", "Rocks", "Trees"))
        );
        enclosureRepository.saveAll(savedEnclosures);

        // when
        long actualNumberOfEnclosuresInRepository = enclosureRepository.count();

        // then
        assertThat(actualNumberOfEnclosuresInRepository)
                .isEqualTo(2L);
    }
}
