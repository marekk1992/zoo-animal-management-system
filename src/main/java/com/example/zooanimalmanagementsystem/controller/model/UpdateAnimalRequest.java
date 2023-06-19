package com.example.zooanimalmanagementsystem.controller.model;

import com.example.zooanimalmanagementsystem.repository.model.Animal;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record UpdateAnimalRequest(
        @NotBlank(message = "Species is required.")
        String species,

        @Min(value = 1, message = "Animal amount should not be less than 1.")
        int amount
) {

    public Animal toEntity() {
        return new Animal(species, amount);
    }
}
