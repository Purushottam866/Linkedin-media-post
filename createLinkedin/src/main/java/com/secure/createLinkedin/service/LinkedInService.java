 package com.secure.createLinkedin.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.secure.createLinkedin.helper.ResponseStructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class LinkedInService 
{

    @Value("${linkedin.access.token}")
    private String accessToken;

    @Autowired
    ResponseStructure<String> response;
    
   RestTemplate restTemplate = new RestTemplate();
   
   
   public ResponseStructure<String> createPost(String message) {
     
       try {
           String url = "https://api.linkedin.com/v2/ugcPosts";
           String requestBody = "{\"author\":\"urn:li:person:cHTCMRpubB\",\"lifecycleState\":\"PUBLISHED\",\"specificContent\":{\"com.linkedin.ugc.ShareContent\":{\"shareCommentary\":{\"text\":\"" + message + "\"},\"shareMediaCategory\":\"NONE\"}},\"visibility\":{\"com.linkedin.ugc.MemberNetworkVisibility\":\"PUBLIC\"}}";
           
           HttpHeaders headers = new HttpHeaders();
           headers.set("Authorization", "Bearer " + accessToken);
           headers.set("Content-Type", "application/json");
           HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

           ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
           if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
               response.setStatus("Success");
               response.setMessage("Post created successfully");
               response.setCode(HttpStatus.CREATED.value());
           } else {
               response.setStatus("Failure");
               response.setMessage("Failed to create post");
               response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
           }
       } catch (Exception e) {
           response.setStatus("Failure");
           response.setMessage("Internal Server Error");
           response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
       }
       return response;
   }

   public ResponseStructure<String> uploadImageToLinkedIn(MultipartFile file, String caption) {
	   
	    try {
	        String recipeType = determineRecipeType(file);
	        String mediaType = determineMediaType(file);
	        JsonNode uploadResponse = registerUpload(recipeType);
	        String uploadUrl = uploadResponse.get("value").get("uploadMechanism").get("com.linkedin.digitalmedia.uploading.MediaUploadHttpRequest").get("uploadUrl").asText();
	        String mediaAsset = uploadResponse.get("value").get("asset").asText();
	        uploadImage(uploadUrl, file);
	        ResponseStructure<String> postResponse = createLinkedInPost(mediaAsset, caption, mediaType);
	        response.setStatus("Success");
	        response.setMessage("Media uploaded successfully");
	        response.setCode(HttpStatus.CREATED.value());
	        // Extract meaningful data from postResponse and set it as data
	        response.setData(postResponse.getMessage()); // For example, setting the message from postResponse as data
	    } catch (IOException e) {
	        response.setStatus("Failure");
	        response.setMessage("Failed to upload media to LinkedIn: " + e.getMessage());
	        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	    }
	    return response;
	}

   private String determineRecipeType(MultipartFile file) {
       String contentType = file.getContentType();
       return contentType != null && contentType.startsWith("image") ? "urn:li:digitalmediaRecipe:feedshare-image" : "urn:li:digitalmediaRecipe:feedshare-video";
   }

   private String determineMediaType(MultipartFile file) {
       return file.getContentType() != null && file.getContentType().startsWith("image") ? "image" : "video";
   }

   private JsonNode registerUpload(String recipeType) throws IOException {
       HttpHeaders headers = new HttpHeaders();
       headers.setContentType(MediaType.APPLICATION_JSON);
       headers.set("Authorization", "Bearer " + accessToken);

       String requestBody = "{\"registerUploadRequest\": {\"recipes\": [\"" + recipeType + "\"],\"owner\": \"urn:li:person:cHTCMRpubB\",\"serviceRelationships\": [{\"relationshipType\": \"OWNER\",\"identifier\": \"urn:li:userGeneratedContent\"}]}}";

       HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

       ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
               "https://api.linkedin.com/v2/assets?action=registerUpload",
               HttpMethod.POST,
               requestEntity,
               JsonNode.class
       );

       if (responseEntity.getStatusCode() == HttpStatus.OK) {
           return responseEntity.getBody();
       } else {
           throw new RuntimeException("Failed to register upload: " + responseEntity.getStatusCode());
       }
   }

   private ResponseStructure<String> uploadImage(String uploadUrl, MultipartFile file) {
	  
	    try {
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	        headers.set("Authorization", "Bearer " + accessToken);

	        byte[] fileContent;
	        try {
	            fileContent = file.getBytes();
	        } catch (IOException e) {
	            response.setStatus("Failure");
	            response.setMessage("Failed to read image file");
	            response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	            return response;
	        }

	        HttpEntity<byte[]> requestEntity = new HttpEntity<>(fileContent, headers);

	        ResponseEntity<String> responseEntity = restTemplate.exchange(
	                uploadUrl,
	                HttpMethod.POST,
	                requestEntity,
	                String.class
	        );

	        if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
	            response.setStatus("Success");
	            response.setMessage("Media uploaded successfully");
	            response.setCode(HttpStatus.CREATED.value());
	        } else {
	            response.setStatus("Failure");
	            response.setMessage("Failed to upload media: " + responseEntity.getStatusCode());
	            response.setCode(responseEntity.getStatusCode().value());
	        }
	    } catch (Exception e) {
	        response.setStatus("Failure");
	        response.setMessage("Internal Server Error");
	        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	    }
	    return response;
	}

	private ResponseStructure<String> createLinkedInPost(String mediaAsset, String caption, String mediaType) {
	   
	    try {
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.set("Authorization", "Bearer " + accessToken);

	        String shareMediaCategory = mediaType.equals("image") ? "IMAGE" : "VIDEO";

	        String requestBody = "{\n" +
	                "    \"author\": \"urn:li:person:cHTCMRpubB\",\n" +
	                "    \"lifecycleState\": \"PUBLISHED\",\n" +
	                "    \"specificContent\": {\n" +
	                "        \"com.linkedin.ugc.ShareContent\": {\n" +
	                "            \"shareCommentary\": {\n" +
	                "                \"text\": \"" + caption + "\"\n" +
	                "            },\n" +
	                "            \"shareMediaCategory\": \"" + shareMediaCategory + "\",\n" +
	                "            \"media\": [\n" +
	                "                {\n" +
	                "                    \"status\": \"READY\",\n" +
	                "                    \"description\": {\n" +
	                "                        \"text\": \"Center stage!\"\n" +
	                "                    },\n" +
	                "                    \"media\": \"" + mediaAsset + "\",\n" +
	                "                    \"title\": {\n" +
	                "                        \"text\": \"LinkedIn Talent Connect 2021\"\n" +
	                "                    }\n" +
	                "                }\n" +
	                "            ]\n" +
	                "        }\n" +
	                "    },\n" +
	                "    \"visibility\": {\n" +
	                "        \"com.linkedin.ugc.MemberNetworkVisibility\": \"PUBLIC\"\n" +
	                "    }\n" +
	                "}";

	        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

	        ResponseEntity<String> responseEntity = restTemplate.exchange(
	                "https://api.linkedin.com/v2/ugcPosts",
	                HttpMethod.POST,
	                requestEntity,
	                String.class
	        );

	        if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
	            response.setStatus("Success");
	            response.setMessage("LinkedIn post created successfully");
	            response.setCode(HttpStatus.CREATED.value());
	        } else {
	            response.setStatus("Failure");
	            response.setMessage("Failed to create LinkedIn post: " + responseEntity.getStatusCode());
	            response.setCode(responseEntity.getStatusCode().value());
	        }
	    } catch (Exception e) {
	        response.setStatus("Failure");
	        response.setMessage("Internal Server Error");
	        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	    }
	    return response;
	}
    
}
