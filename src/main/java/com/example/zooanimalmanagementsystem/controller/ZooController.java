package com.example.zooanimalmanagementsystem.controller;

import com.example.zooanimalmanagementsystem.service.ZooService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/zoo")
public class ZooController {

    private final ZooService zooService;

    public ZooController(ZooService zooService) {
        this.zooService = zooService;
    }

    @PostMapping("/upload/enclosures")
    public String saveEnclosures(@RequestParam MultipartFile file) {
        zooService.saveEnclosures(file);
        return "Successfully read data from file: " + file.getOriginalFilename();
    }
}
