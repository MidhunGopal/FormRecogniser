package com.example.demo.controller;

import com.example.demo.model.Output;
import com.example.demo.service.FormRecognizerService;
import com.example.demo.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class FormRecogniserController {

    FormRecognizerService formRecognizerService;

    @Autowired
    public FormRecogniserController(FormRecognizerService formRecognizerService) {
        this.formRecognizerService = formRecognizerService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        Map<String, Object> response = new HashMap<>();
        if (multipartFile.isEmpty()) {
            response.put("status", "failure");
            response.put("message", "Please select a file to upload");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }
        try {
            File file = FileUtil.convertMultipartFileToFile(multipartFile);
            List<Output> results = formRecognizerService.extractData(file);
            response.put("status", "success");
            response.put("data", results);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            response.put("status", "failure");
            response.put("message", "Failed to upload file");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
