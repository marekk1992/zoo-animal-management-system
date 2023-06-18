package com.example.zooanimalmanagementsystem.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "enclosure")
public class Enclosure {

    @Id
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "size")
    private String size;

    @Column(name = "location")
    private String location;

    @Column(name = "objects")
    private List<String> objects;

    @Column(name = "free_space")
    private int freeSpace;

    @Column(name = "animals")
    private List<String> animals;

    public Enclosure() {
    }

    public Enclosure(UUID id, String name, String size, String location, List<String> objects) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.location = location;
        this.objects = objects;
    }

    public Enclosure(String name, String size, String location, List<String> objects) {
        id = UUID.randomUUID();
        this.name = name;
        this.size = size;
        this.location = location;
        this.objects = objects;
        freeSpace = evaluateEnclosureCapacity(size);
        animals = new ArrayList<>();
        animals.add("Empty");
    }

    public Enclosure(UUID id, String name, String size, String location, List<String> objects, int freeSpace, List<String> animals) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.location = location;
        this.objects = objects;
        this.freeSpace = freeSpace;
        this.animals = animals;
    }

    @Override
    public String toString() {
        return "Enclosure{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", size='" + size + '\'' +
               ", location='" + location + '\'' +
               ", objects=" + objects +
               ", freeSpace=" + freeSpace +
               ", animals=" + animals +
               '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        Enclosure enclosure = (Enclosure) obj;

        return this.id.equals(enclosure.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public String getLocation() {
        return location;
    }

    public List<String> getObjects() {
        return objects;
    }

    public int getFreeSpace() {
        return freeSpace;
    }

    public List<String> getAnimals() {
        return animals;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setFreeSpace(int freeSpace) {
        this.freeSpace = freeSpace;
    }

    public void setAnimals(String animal) {
        if (animals.contains("Empty")) {
            animals.clear();
        }
        animals.add(animal);
    }

    private int evaluateEnclosureCapacity(String size) {
        return switch (size) {
            case "Small" -> 3;
            case "Medium" -> 7;
            case "Large" -> 11;
            case "Huge" -> 15;
            default -> 0;
        };
    }
}
