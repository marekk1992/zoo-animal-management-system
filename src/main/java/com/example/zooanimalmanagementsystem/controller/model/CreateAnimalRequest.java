package com.example.zooanimalmanagementsystem.controller.model;

import com.example.zooanimalmanagementsystem.repository.model.Animal;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAnimalRequest(
        @NotBlank(message = "Species is required.")
        String species,

        @NotNull(message = "Animal food is required.")
        String food,

        @Min(value = 1, message = "Animal amount should not be less than 1.")
        int amount
) {
        public Animal toEntity() {
            return new Animal(species, food, amount);
        }
}
