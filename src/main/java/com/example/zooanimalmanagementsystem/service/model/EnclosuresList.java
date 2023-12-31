package com.example.zooanimalmanagementsystem.service.model;

import com.example.zooanimalmanagementsystem.repository.model.Enclosure;

import java.util.List;

public record EnclosuresList(List<EnclosureDetails> enclosures) {

    public List<Enclosure> toEntity() {
        return enclosures.stream()
                .map(EnclosureDetails::toEntity)
                .toList();
    }
}
