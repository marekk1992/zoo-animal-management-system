package com.example.zooanimalmanagementsystem.controller;

import com.example.zooanimalmanagementsystem.controller.model.AnimalCollectionResponse;
import com.example.zooanimalmanagementsystem.controller.model.AnimalResponse;
import com.example.zooanimalmanagementsystem.controller.model.CreateAnimalRequest;
import com.example.zooanimalmanagementsystem.controller.model.UpdateAnimalRequest;
import com.example.zooanimalmanagementsystem.service.ZooService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/v1/zoo")
public class ZooController {

    private final ZooService zooService;

    public ZooController(ZooService zooService) {
        this.zooService = zooService;
    }

    @GetMapping("/animals")
    public AnimalCollectionResponse findAll() {
        return AnimalCollectionResponse.fromEntity(zooService.findAllAnimals());
    }

    @GetMapping("/animals/{animalId}")
    public AnimalResponse findById(@PathVariable UUID animalId) {
        return AnimalResponse.fromEntity(zooService.findAnimalById(animalId));
    }

    @PostMapping("/animals")
    @ResponseStatus(HttpStatus.CREATED)
    public AnimalResponse addAnimal(@Valid @RequestBody CreateAnimalRequest createAnimalRequest) {
        return AnimalResponse.fromEntity(zooService.saveAnimal(createAnimalRequest.toEntity()));
    }

    @PutMapping("/animals/{animalId}")
    public AnimalResponse findById(@PathVariable UUID animalId, @Valid @RequestBody UpdateAnimalRequest updateAnimalRequest) {
        return AnimalResponse.fromEntity(zooService.updateAnimal(animalId, updateAnimalRequest.toEntity()));
    }

    @DeleteMapping("/animals/{animalId}")
    public void deleteAnimal(@PathVariable UUID animalId) {
        zooService.deleteAnimalById(animalId);
    }

    @PostMapping("/upload/animals")
    public String saveAnimals(@RequestParam MultipartFile file) {
        zooService.storeAnimals(file);
        return "Successfully read data from file: " + file.getOriginalFilename();
    }

    @PostMapping("/upload/enclosures")
    public String saveEnclosures(@RequestParam MultipartFile file) {
        zooService.storeEnclosures(file);
        return "Successfully read data from file: " + file.getOriginalFilename();
    }
}
