package com.secure.createLinkedin.controller;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    						
    //TEXT AND MEDIA UPLOAD TO PROFILE
    @PostMapping("/postToProfile") 
    public ResponseEntity<ResponseStructure<String>> createPostTOProfile(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "caption", required = false) String caption) {

        ResponseStructure<String> response;

        if (file != null && !file.isEmpty() && caption != null && !caption.isEmpty()) {
            // Both file and caption are present
            response = linkedInService.uploadImageToLinkedIn(file, caption);
        } else if (caption != null && !caption.isEmpty()) {
            // Only caption is present
            response = linkedInService.createPostProfile(caption);
        } else if (file != null && !file.isEmpty()) {
            // Only file is present
        	 response = linkedInService.uploadImageToLinkedIn(file, "");
        } else {
            // Neither file nor caption are present
            response = new ResponseStructure<>();
            response.setStatus("Failure");
            response.setMessage("Either file or caption must be provided.");
            response.setCode(HttpStatus.BAD_REQUEST.value());
        }

        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }
    
    @PostMapping("/schedule-post")
    public ResponseEntity<ResponseStructure<String>> schedulePost(@RequestParam(value = "message") String message,
                                                                  @RequestParam(value = "scheduleDateTime")
                                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                                           LocalDateTime scheduleDateTime) {
        ResponseStructure<String> response = linkedInService.schedulePost(message, scheduleDateTime);
        return ResponseEntity.status(response.getCode()).body(response);
    }
    
    
    //TEXT POST TO LINKEDIN COMPANY / PAGE
    @PostMapping("post")
    public ResponseStructure<String> createLinkedInPost(@RequestParam String caption) {
        return linkedInService.createPostPage(caption);
    }
    
    
    //TEXT AND MEDIA UPLOAD TO PAGE/ORGANIZATION
    @PostMapping("/uploadToPage")
    public ResponseEntity<ResponseStructure<String>> uploadImageToLinkedInPage(@RequestParam(value = "file", required = false) MultipartFile file,
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
                ResponseStructure<String> response = linkedInService.createPostPage(caption);
                return ResponseEntity.status(response.getCode()).body(response);
            } else if (caption == null) {
                // Caption is missing, call uploadImageToLinkedIn method
                ResponseStructure<String> response = linkedInService.uploadImageToLinkedInPage(file, "");
                return ResponseEntity.status(response.getCode()).body(response);
            } else {
                // Both file and caption are present, call uploadImageToLinkedIn method
                ResponseStructure<String> response = linkedInService.uploadImageToLinkedInPage(file, caption);
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
    
 // TEXT AND MEDIA UPLOAD TO PAGE/ORGANIZATION
    @PostMapping("/postToPage")
    public ResponseEntity<ResponseStructure<String>> createPost(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "caption", required = false) String caption) {

        ResponseStructure<String> response;

        if (file != null && !file.isEmpty() && caption != null && !caption.isEmpty()) {
            // Both file and caption are present
            response = linkedInService.uploadImageToLinkedInPage(file, caption);
        } else if (caption != null && !caption.isEmpty()) {
            // Only caption is present
            response = linkedInService.createPostPage(caption);
        } else if (file != null && !file.isEmpty()) {
            // Only file is present
        	 response = linkedInService.uploadImageToLinkedInPage(file, "");
        } else {
            // Neither file nor caption are present
            response = new ResponseStructure<>();
            response.setStatus("Failure");
            response.setMessage("Either file or caption must be provided.");
            response.setCode(HttpStatus.BAD_REQUEST.value());
        }

        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }
    
    
    public ResponseStructure<String> getLinkedInProfile(String accessToken) {
    	ResponseStructure<String> responseStructure = new ResponseStructure<>();
        RestTemplate restTemplate = new RestTemplate();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>("", headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                "https://api.linkedin.com/v2/me?projection=(id,profilePicture(displayImage~:playableStreams))",
                HttpMethod.GET,
                entity,
                String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(responseBody);
                JsonNode elements = rootNode.path("profilePicture").path("displayImage~").path("elements");

                String imageUrl = null;
                if (elements.isArray() && elements.size() > 0) {
                    for (JsonNode element : elements) {
                        JsonNode displaySize = element.path("data").path("com.linkedin.digitalmedia.mediaartifact.StillImage").path("displaySize");
                        if (displaySize.path("width").asInt() == 200 && displaySize.path("height").asInt() == 200) {
                            JsonNode identifiers = element.path("identifiers");
                            if (identifiers.isArray() && identifiers.size() > 0) {
                                imageUrl = identifiers.get(0).path("identifier").asText();
                                break;
                            }
                        }
                    }
                }

                if (imageUrl != null) {
                    responseStructure.setStatus("Success");
                    responseStructure.setMessage("Profile fetched successfully");
                    responseStructure.setCode(HttpStatus.OK.value());
                    responseStructure.setData(imageUrl);
                } else {
                    responseStructure.setStatus("Failure");
                    responseStructure.setMessage("Profile image URL not found");
                    responseStructure.setCode(HttpStatus.NOT_FOUND.value());
                    responseStructure.setData(null);
                }
            } else {
                responseStructure.setStatus("Failure");
                responseStructure.setMessage("Failed to fetch profile: " + response.getStatusCode());
                responseStructure.setCode(response.getStatusCode().value());
                responseStructure.setData(null);
            }
        } catch (IOException e) {
            responseStructure.setStatus("Failure");
            responseStructure.setMessage("Exception occurred: " + e.getMessage());
            responseStructure.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseStructure.setData(null);
        }

        return responseStructure;
    }
       
}
