package com.secure.createLinkedin;

import com.fasterxml.jackson.databind.JsonNode;
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

   RestTemplate restTemplate = new RestTemplate();

   public void uploadImageToLinkedIn(MultipartFile file, String caption) {
       String recipeType = determineRecipeType(file);
       
       String mediaType = determineMediaType(file);
       // Step 1: Register the upload
       JsonNode uploadResponse = registerUpload(recipeType);

       // Step 2: Upload the media to the provided upload URL
       String uploadUrl = uploadResponse.get("value").get("uploadMechanism").get("com.linkedin.digitalmedia.uploading.MediaUploadHttpRequest").get("uploadUrl").asText();
       uploadImage(uploadUrl, file);

       // Step 3: Create a LinkedIn post with the uploaded media and caption
       String mediaAsset = uploadResponse.get("value").get("asset").asText();
       createLinkedInPost(mediaAsset, caption , mediaType);
   }

   private String determineRecipeType(MultipartFile file) {
       // Check the content type of the file
       String contentType = file.getContentType();
       if (contentType != null && contentType.startsWith("image")) {
           return "urn:li:digitalmediaRecipe:feedshare-image";
       } else {
           return "urn:li:digitalmediaRecipe:feedshare-video";
       }
   }
   
   private String determineMediaType(MultipartFile file) {
       String contentType = file.getContentType();
       if (contentType != null && contentType.startsWith("image")) {
           return "image";
       } else {
           return "video";
       }
   }


   private JsonNode registerUpload(String recipeType) {
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
    
    private void uploadImage(String uploadUrl, MultipartFile file) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set("Authorization", "Bearer " + accessToken);

        byte[] fileContent;
        try {
            fileContent = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read image file", e);
        }

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(fileContent, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                uploadUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        System.out.println(responseEntity);
        if (responseEntity.getStatusCode() != HttpStatus.CREATED) {
            throw new RuntimeException("Failed to upload image: " + responseEntity.getStatusCode());
        }
    }

    private void createLinkedInPost(String mediaAsset, String caption, String mediaType) {
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
                "                \"text\": \"" + caption + "\"\n" + // Use the provided caption
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
        System.out.println(responseEntity);
        if (responseEntity.getStatusCode() != HttpStatus.CREATED) {
            throw new RuntimeException("Failed to create LinkedIn post: " + responseEntity.getStatusCode());
        }
    }
    
}
