package com.example.zooanimalmanagementsystem.repository;

import com.example.zooanimalmanagementsystem.repository.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, UUID> {
}
