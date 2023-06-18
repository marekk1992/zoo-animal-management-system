package com.example.zooanimalmanagementsystem.service.model;

import com.example.zooanimalmanagementsystem.repository.model.Animal;

public record AnimalDetails(String species, String food, int amount) {

    public Animal toEntity() {
        return new Animal(species, food, amount);
    }
}
