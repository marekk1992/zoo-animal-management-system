package com.example.zooanimalmanagementsystem.service;

import com.example.zooanimalmanagementsystem.service.exception.InputFileNotAvailableException;
import com.example.zooanimalmanagementsystem.service.exception.ReadingFromFileFailedException;
import com.example.zooanimalmanagementsystem.service.model.AnimalsList;
import com.example.zooanimalmanagementsystem.service.model.EnclosuresList;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class InputReadingService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public EnclosuresList retrieveEnclosuresData(MultipartFile file) {
        checkIfFileIsAvailable(file);
        return readEnclosuresFile(file);
    }

    public AnimalsList retrieveAnimalsData(MultipartFile file) {
        checkIfFileIsAvailable(file);
        return readAnimalsFile(file);
    }

    private void checkIfFileIsAvailable(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InputFileNotAvailableException("Can`t read data. File is either not uploaded or empty.");
        }
    }

    private EnclosuresList readEnclosuresFile(MultipartFile file) {
        try {
            return objectMapper.readValue(file.getBytes(), EnclosuresList.class);
        } catch (IOException e) {
            throw new ReadingFromFileFailedException("Can`t read data from file. Make sure file has correct format");
        }
    }

    private AnimalsList readAnimalsFile(MultipartFile file) {
        try {
            return objectMapper.readValue(file.getBytes(), AnimalsList.class);
        } catch (IOException e) {
            throw new ReadingFromFileFailedException("Can`t read data from file. Make sure file has correct format");
        }
    }
}
