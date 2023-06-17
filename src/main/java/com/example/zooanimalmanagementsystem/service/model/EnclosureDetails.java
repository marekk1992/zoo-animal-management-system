package com.example.zooanimalmanagementsystem.service.model;

import com.example.zooanimalmanagementsystem.repository.model.Enclosure;

import java.util.List;

public record EnclosureDetails(String name, String size, String location, List<String> objects) {

    public Enclosure toEntity() {
        return new Enclosure(name, size, location, objects);
    }
}
