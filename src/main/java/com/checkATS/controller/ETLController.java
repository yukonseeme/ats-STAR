package com.checkATS.controller;

import com.checkATS.service.ETLservice;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/ats")
@CrossOrigin(origins = "*")
public class ETLController {
    private final ETLservice etlService;

    public ETLController (ETLservice etlService){
        this.etlService = etlService;
    }

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> analyzeResume(
            @RequestParam("resume") MultipartFile resumeFile,
            @RequestParam("jobDescription") MultipartFile jdFile) {
        if (resumeFile.isEmpty() || jdFile.isEmpty()){
            return ResponseEntity.badRequest().body("Error: No resume & job description found.");

        }
        try{
            var resumeResource = resumeFile.getResource();
            var jdResource = jdFile.getResource();

            var result = etlService.compareResumeToJobDescription(resumeResource, jdResource);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred during parsing analysis: " + e.getMessage());
      }
    }
}


