package com.example.zooanimalmanagementsystem.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "animal")
public class Animal {

    @Id
    private UUID id;

    @Column(name = "species")
    private String species;

    @Column(name = "food")
    private String food;

    @Column(name = "amount")
    private int amount;

    @Column(name = "enclosure_id")
    private UUID enclosureId;

    public Animal() {
    }

    public Animal(String species, String food, int amount) {
        id = UUID.randomUUID();
        this.species = species;
        this.food = food;
        this.amount = amount;
    }

    public Animal(UUID id, String species, String food, int amount, UUID enclosureId) {
        this.id = id;
        this.species = species;
        this.food = food;
        this.amount = amount;
        this.enclosureId = enclosureId;
    }

    @Override
    public String toString() {
        return "Animal{" +
               "id=" + id +
               ", species='" + species + '\'' +
               ", food='" + food + '\'' +
               ", amount=" + amount +
               ", enclosureId=" + enclosureId +
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
        Animal animal = (Animal) obj;

        return id.equals(animal.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public UUID getId() {
        return id;
    }

    public String getSpecies() {
        return species;
    }

    public String getFood() {
        return food;
    }

    public int getAmount() {
        return amount;
    }

    public UUID getEnclosureId() {
        return enclosureId;
    }

    public void setEnclosureId(UUID enclosureId) {
        this.enclosureId = enclosureId;
    }
}
