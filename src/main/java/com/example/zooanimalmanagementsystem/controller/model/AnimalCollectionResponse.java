package com.example.zooanimalmanagementsystem.controller.model;

import com.example.zooanimalmanagementsystem.repository.model.Animal;

import java.util.List;

public record AnimalCollectionResponse(List<AnimalResponse> animals) {

    public static AnimalCollectionResponse fromEntity(List<Animal> animals) {
        List<AnimalResponse> responses = animals.stream()
                .map(AnimalResponse::fromEntity)
                .toList();

        return new AnimalCollectionResponse(responses);
    }
}
