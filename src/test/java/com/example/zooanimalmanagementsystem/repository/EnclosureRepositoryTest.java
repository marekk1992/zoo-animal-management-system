package com.example.zooanimalmanagementsystem.repository;

import com.example.zooanimalmanagementsystem.repository.model.Enclosure;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class EnclosureRepositoryTest {

    @Autowired
    private EnclosureRepository enclosureRepository;

    @Test
    void saves_enclosures() {
        //given
        UUID firstId = UUID.fromString("6879e088-6a43-4f13-bd57-6bddb54fbd1b");
        UUID secondID = UUID.fromString("2159cfe1-a549-4aa5-8ff6-b8257366c94d");
        List<Enclosure> savedEnclosures = List.of(
                new Enclosure(firstId, "Test Enclosure 1", "Medium", "Outside", List.of("Pool", "Rocks", "Trees")),
                new Enclosure(secondID, "Test Enclosure 2", "Large", "Inside", List.of("Pool", "Rocks", "Trees"))
        );

        // when
        List<Enclosure> actualEnclosures = enclosureRepository.saveAll(savedEnclosures);

        // then
        assertThat(actualEnclosures)
                .extracting("id")
                .containsExactly(firstId, secondID);
    }
}
