package com.secure.createLinkedin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.secure.createLinkedin.helper.ResponseStructure;
import com.secure.createLinkedin.service.LinkedInService;

@RestController
@RequestMapping("/linkedin")
public class LinkedInController {

    private final LinkedInService linkedInService;

    @Autowired
    public LinkedInController(LinkedInService linkedInService) {
        this.linkedInService = linkedInService;
    }
    
    @Autowired
    ResponseStructure<String> response;
    											   
    @PostMapping("/upload")
    public ResponseEntity<ResponseStructure<String>> uploadImageToLinkedIn(@RequestParam(value = "file", required = false) MultipartFile file,
                                                                           @RequestParam(value = "caption", required = false) String caption) {
        try {
            if (file == null && caption == null) {
                // Both file and caption are missing, return an error response
                ResponseStructure<String> errorResponse = new ResponseStructure<>();
                errorResponse.setStatus("Failure");
                errorResponse.setMessage("Both file and caption are missing");
                errorResponse.setCode(HttpStatus.BAD_REQUEST.value());
                return ResponseEntity.badRequest().body(errorResponse);
            } else if (file == null) {
                // File is missing, call createPost method
                if (caption == null) {
                    caption = "";
                }
                ResponseStructure<String> response = linkedInService.createPost(caption);
                return ResponseEntity.status(response.getCode()).body(response);
            } else if (caption == null) {
                // Caption is missing, call uploadImageToLinkedIn method
                ResponseStructure<String> response = linkedInService.uploadImageToLinkedIn(file, "");
                return ResponseEntity.status(response.getCode()).body(response);
            } else {
                // Both file and caption are present, call uploadImageToLinkedIn method
                ResponseStructure<String> response = linkedInService.uploadImageToLinkedIn(file, caption);
                return ResponseEntity.status(response.getCode()).body(response);
            }
        } catch (Exception e) {
            // Handle other exceptions
            ResponseStructure<String> errorResponse = new ResponseStructure<>();
            errorResponse.setStatus("Failure");
            errorResponse.setMessage("Internal Server Error");
            errorResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
