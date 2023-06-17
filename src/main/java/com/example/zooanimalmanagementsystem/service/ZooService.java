package com.example.zooanimalmanagementsystem.service;

import com.example.zooanimalmanagementsystem.repository.EnclosureRepository;
import com.example.zooanimalmanagementsystem.repository.model.Enclosure;
import com.example.zooanimalmanagementsystem.service.model.EnclosuresList;
import com.example.zooanimalmanagementsystem.service.exception.EnclosuresDataAlreadyStoredException;
import com.example.zooanimalmanagementsystem.service.exception.InputFileNotAvailableException;
import com.example.zooanimalmanagementsystem.service.exception.ReadingFromFileFailedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ZooService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EnclosureRepository enclosureRepository;

    public ZooService(EnclosureRepository enclosureRepository) {
        this.enclosureRepository = enclosureRepository;
    }

    public List<Enclosure> saveEnclosures(MultipartFile file) {
        EnclosuresList enclosuresList = retrieveEnclosuresData(file);
        return enclosureRepository.saveAll(enclosuresList.toEntity());
    }

    private EnclosuresList retrieveEnclosuresData(MultipartFile file) {
        evaluateIfEnclosuresAreNotStoredAlready();
        checkIfFileIsAvailable(file);
        return readFile(file);
    }

    private void evaluateIfEnclosuresAreNotStoredAlready() {
        if (enclosureRepository.count() > 0) {
            throw new EnclosuresDataAlreadyStoredException("File reading cancelled. Enclosures are already stored in database.");
        }
    }

    private void checkIfFileIsAvailable(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InputFileNotAvailableException("Can`t read data. File is either not uploaded or empty.");
        }
    }

    private EnclosuresList readFile(MultipartFile file) {
        try {
            return objectMapper.readValue(file.getBytes(), EnclosuresList.class);
        } catch (IOException e) {
            throw new ReadingFromFileFailedException("Can`t read data from file. Make sure file has correct format");
        }
    }
}
