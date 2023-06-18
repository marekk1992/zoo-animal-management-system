package com.example.zooanimalmanagementsystem.service.model;

import com.example.zooanimalmanagementsystem.repository.model.Animal;

import java.util.List;

public record AnimalsList(List<AnimalDetails> animals) {

    public List<Animal> toEntity() {
        return animals.stream()
                .map(AnimalDetails::toEntity)
                .toList();
    }
}
