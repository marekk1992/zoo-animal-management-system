package com.example.zooanimalmanagementsystem.controller.model;

import com.example.zooanimalmanagementsystem.repository.model.Animal;

import java.util.UUID;

public record AnimalResponse(UUID animalId, String species, String food, int amount, UUID assignedEnclosureId) {

    public static AnimalResponse fromEntity(Animal animal) {
        return new AnimalResponse(
                animal.getId(),
                animal.getSpecies(),
                animal.getFood(),
                animal.getAmount(),
                animal.getEnclosureId()
        );
    }
}
